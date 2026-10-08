package com.napoleao.alphabeto.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.napoleao.alphabeto.api.ContextService;
import com.napoleao.alphabeto.api.response.ContextPageResponse;
import com.napoleao.alphabeto.helper.dao.ChallengesDAO;
import com.napoleao.alphabeto.helper.dao.DBHelper;
import com.napoleao.alphabeto.helper.dao.TemasDAO;
import com.napoleao.alphabeto.model.Challenge;
import com.napoleao.alphabeto.model.Tema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Response;

/**
 * Sincroniza os temas e desafios cadastrados no EducAPI com o banco local do app.
 * <p>
 * - So temas que o usuario importou (tela de importacao) sao mantidos em dia: desafios
 * novos/alterados/removidos sao atualizados; temas removidos no EducAPI saem do app.
 * - Temas criados localmente (remoteId nulo) nunca sao alterados.
 * - Tema removido manualmente pelo usuario nao volta na proxima sincronizacao.
 * - Tudo e aplicado numa unica transacao: se a rede falhar no meio, o banco local nao muda.
 */
public class ThemeSyncManager {

    private static final String TAG = "ThemeSync";
    private static final String PREFS = "themeSync";
    private static final String KEY_LAST_SYNC = "lastSync";
    private static final String KEY_DISMISSED = "dismissedThemes";

    /** Intervalo minimo entre sincronizacoes automaticas. */
    private static final long MIN_INTERVAL_MS = 30 * 1000L;
    private static final int PAGE_SIZE = 20;
    private static final int MAX_PAGES = 100;

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    public interface Listener {
        void onFinished(Result result);
    }

    public static class Result {
        public boolean success;
        public int added;
        public int updated;
        public int removed;
        public String error;

        public boolean hasChanges() {
            return added + updated + removed > 0;
        }
    }

    private ThemeSyncManager() {
    }

    /**
     * Starts a sync in background. The listener is called on the main thread.
     *
     * @param force ignores the minimum interval between syncs
     */
    public static void sync(Context context, boolean force, final Listener listener) {
        final Context app = context.getApplicationContext();
        final SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        if (!force && System.currentTimeMillis() - prefs.getLong(KEY_LAST_SYNC, 0) < MIN_INTERVAL_MS) {
            return;
        }
        if (!RUNNING.compareAndSet(false, true)) {
            return;
        }

        final Handler main = new Handler(Looper.getMainLooper());
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                final Result result = new Result();
                try {
                    doSync(app, result);
                    result.success = true;
                    prefs.edit().putLong(KEY_LAST_SYNC, System.currentTimeMillis()).apply();
                } catch (Exception e) {
                    result.success = false;
                    result.error = e.getMessage();
                    Log.w(TAG, "Sync falhou: " + e.getMessage());
                } finally {
                    RUNNING.set(false);
                }
                if (listener != null) {
                    main.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFinished(result);
                        }
                    });
                }
            }
        });
    }

    private static void doSync(Context app, Result result) throws IOException {
        ApiConfig config = ApiConfig.getInstance(app);
        ContextService api = new RetrofitConfig().contextService();

        // 1) baixa todas as paginas antes de tocar no banco
        List<Tema> remote = new ArrayList<>();
        boolean complete = false;
        for (int page = 0; page < MAX_PAGES; page++) {
            Response<ContextPageResponse> response = api.getContexts(page, PAGE_SIZE, "id,asc").execute();
            ContextPageResponse body = response.body();
            if (!response.isSuccessful() || body == null) {
                throw new IOException("HTTP " + response.code());
            }
            remote.addAll(body.getContent());
            if (body.isLast() || body.getContent().isEmpty()) {
                complete = true;
                break;
            }
        }

        String base = config.getDomain();
        SQLiteDatabase db = DBHelper.getInstance(app).getWritableDatabase();
        TemasDAO temasDAO = TemasDAO.getInstance(app);
        ChallengesDAO challengesDAO = ChallengesDAO.getInstance(app);
        Set<Long> dismissed = getDismissed(app);
        Set<Long> keep = new HashSet<>();

        // 2) aplica tudo numa transacao
        db.beginTransaction();
        try {
            for (Tema t : remote) {
                if (t.getId() == null || isBlank(t.getNomeImagem())) {
                    continue;
                }
                List<Challenge> valid = validChallenges(t);
                if (valid.isEmpty()) {
                    continue; // tema sem desafios quebra o jogo
                }
                if (dismissed.contains(t.getId())) {
                    continue; // usuario removeu no app
                }
                keep.add(t.getId());

                String imageUrl = themeImageUrl(base, t);
                Tema local = temasDAO.findByRemote(t.getId(), t.getNomeImagem());
                long localId;
                boolean changed = false;

                if (local == null) {
                    // tema nao escolhido pelo usuario: a sincronizacao so atualiza os ja importados
                    continue;
                } else {
                    localId = local.getId();
                    if (!Objects.equals(local.getNomeImagem(), t.getNomeImagem())
                            || !Objects.equals(local.getImageUrl(), imageUrl)) {
                        ContentValues cv = new ContentValues();
                        cv.put("nomeImagem", t.getNomeImagem());
                        cv.put("imagemUrl", imageUrl);
                        db.update(DBHelper.TABLE_TEMAS, cv, "idTema=?", new String[]{String.valueOf(localId)});
                        changed = true;
                    }
                    if (syncChallenges(db, challengesDAO, base, localId, valid)) {
                        changed = true;
                    }
                    if (changed) {
                        result.updated++;
                    }
                }
            }

            // 3) remove temas que sairam do EducAPI (so se a lista veio completa)
            if (complete) {
                result.removed += pruneRemoved(db, keep);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @return true if anything changed
     */
    private static boolean syncChallenges(SQLiteDatabase db, ChallengesDAO challengesDAO, String base,
                                          long localThemeId, List<Challenge> remoteChallenges) {
        boolean changed = false;

        Map<Long, Challenge> byRemote = new HashMap<>();
        List<Challenge> legacy = new ArrayList<>();
        for (Challenge c : challengesDAO.findById(localThemeId)) {
            if (c.getRemoteId() != null) {
                byRemote.put(c.getRemoteId(), c);
            } else {
                legacy.add(c);
            }
        }

        Set<Long> seen = new HashSet<>();
        for (Challenge rc : remoteChallenges) {
            seen.add(rc.getId());
            String imageUrl = challengeImageUrl(base, rc);
            Challenge lc = byRemote.get(rc.getId());

            if (lc == null) {
                // desafio importado antes da sincronizacao (id local == id do EducAPI)
                for (Challenge l : legacy) {
                    if (rc.getId().equals(l.getId()) && Objects.equals(rc.getWord(), l.getWord())) {
                        lc = l;
                        ContentValues adopt = new ContentValues();
                        adopt.put("remoteId", rc.getId());
                        db.update(DBHelper.TABLE_DESAFIOS, adopt, "idDesafio=?", new String[]{String.valueOf(l.getId())});
                        break;
                    }
                }
            }

            if (lc == null) {
                ContentValues cv = new ContentValues();
                cv.put("word", rc.getWord());
                cv.put("imageUrl", imageUrl);
                cv.put("idTema", localThemeId);
                cv.put("remoteId", rc.getId());
                db.insertOrThrow(DBHelper.TABLE_DESAFIOS, null, cv);
                changed = true;
            } else if (!Objects.equals(lc.getWord(), rc.getWord()) || !Objects.equals(lc.getImageUrl(), imageUrl)) {
                ContentValues cv = new ContentValues();
                cv.put("word", rc.getWord());
                cv.put("imageUrl", imageUrl);
                db.update(DBHelper.TABLE_DESAFIOS, cv, "idDesafio=?", new String[]{String.valueOf(lc.getId())});
                changed = true;
            }
        }

        // desafios que vieram do EducAPI e foram removidos la
        for (Challenge lc : byRemote.values()) {
            if (!seen.contains(lc.getRemoteId())) {
                db.delete(DBHelper.TABLE_DESAFIOS, "idDesafio=?", new String[]{String.valueOf(lc.getId())});
                changed = true;
            }
        }
        return changed;
    }

    private static int pruneRemoved(SQLiteDatabase db, Set<Long> keep) {
        List<long[]> toDelete = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT idTema, remoteId FROM " + DBHelper.TABLE_TEMAS
                + " WHERE remoteId IS NOT NULL", null);
        while (cursor.moveToNext()) {
            long localId = cursor.getLong(0);
            long remoteId = cursor.getLong(1);
            if (!keep.contains(remoteId)) {
                toDelete.add(new long[]{localId, remoteId});
            }
        }
        cursor.close();

        for (long[] ids : toDelete) {
            String[] args = {String.valueOf(ids[0])};
            db.delete(DBHelper.TABLE_DESAFIOS, "idTema=?", args);
            db.delete(DBHelper.TABLE_TEMAS, "idTema=?", args);
        }
        return toDelete.size();
    }

    private static List<Challenge> validChallenges(Tema t) {
        List<Challenge> valid = new ArrayList<>();
        if (t.getChallenges() == null) {
            return valid;
        }
        for (Challenge c : t.getChallenges()) {
            if (c != null && c.getId() != null && !isBlank(c.getWord())) {
                valid.add(c);
            }
        }
        return valid;
    }

    // ---- URLs de imagem: usa os endpoints publicos do EducAPI (MinIO + fallback de backup)

    private static String themeImageUrl(String base, Tema t) {
        return base + "v1/api/contexts/" + t.getId() + "/image?v=" + version(t.getImageUrl());
    }

    private static String challengeImageUrl(String base, Challenge c) {
        return base + "v1/api/challenges/" + c.getId() + "/image?v=" + version(c.getImageUrl());
    }

    /** Muda quando a imagem muda, para o cache do Glide nao servir imagem velha. */
    private static int version(String imageUrl) {
        return imageUrl == null ? 0 : Math.abs(imageUrl.hashCode());
    }

    // ---- Importacao manual (por ID / por e-mail)

    /**
     * Prepares a theme fetched from EducAPI to be saved locally: the EducAPI id goes to remoteId and
     * local ids are generated by SQLite (avoids collision with locally created themes).
     */
    public static void prepareForLocalSave(Context context, Tema t) {
        String base = ApiConfig.getInstance(context.getApplicationContext()).getDomain();
        Long themeRemoteId = t.getId();
        if (themeRemoteId != null) {
            t.setImageUrl(themeImageUrl(base, t));
        }
        t.setRemoteId(themeRemoteId);
        t.setId(null);
        if (t.getChallenges() != null) {
            for (Challenge c : t.getChallenges()) {
                if (c.getId() != null) {
                    c.setImageUrl(challengeImageUrl(base, c));
                }
                c.setRemoteId(c.getId());
                c.setId(null);
            }
        }
        if (themeRemoteId != null) {
            clearDismissed(context, themeRemoteId);
        }
    }

    // ---- Temas removidos pelo usuario

    public static void markDismissed(Context context, Long remoteId) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>(prefs.getStringSet(KEY_DISMISSED, new HashSet<String>()));
        set.add(String.valueOf(remoteId));
        prefs.edit().putStringSet(KEY_DISMISSED, set).apply();
    }

    public static void clearDismissed(Context context, Long remoteId) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>(prefs.getStringSet(KEY_DISMISSED, new HashSet<String>()));
        if (set.remove(String.valueOf(remoteId))) {
            prefs.edit().putStringSet(KEY_DISMISSED, set).apply();
        }
    }

    private static Set<Long> getDismissed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Set<Long> result = new HashSet<>();
        for (String s : prefs.getStringSet(KEY_DISMISSED, new HashSet<String>())) {
            try {
                result.add(Long.parseLong(s));
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
