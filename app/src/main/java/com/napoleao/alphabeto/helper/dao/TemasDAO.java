package com.napoleao.alphabeto.helper.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.napoleao.alphabeto.helper.ThemeSyncManager;
import com.napoleao.alphabeto.model.Tema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsable for access database and manipulate Tema.
 */
public class TemasDAO implements ITemasDAO {

    private static TemasDAO instance;
    private SQLiteDatabase write;
    private SQLiteDatabase read;
    private Context context;

    /**
     * Method responsable for return instance of TemasDAO.
     *
     * @param context
     * @return instance
     */
    public static synchronized TemasDAO getInstance(Context context) {
        if (instance == null) {
            instance = new TemasDAO(context);
        }
        return instance;
    }

    /**
     * Constructor for TemasDAO.
     *
     * @param context
     */
    public TemasDAO(Context context) {
        this.context = context;
        DBHelper dbHelper = DBHelper.getInstance(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    /**
     * Method responsable for get a theme.
     *
     * @param idGet id of theme
     * @return Tema
     */
    @Override
    public Tema get(Long idGet) {

        Long id = null, remoteId = null;
        String nomeImegem = null, imagemUrl = null;

        String sql = "SELECT * FROM " + DBHelper.TABLE_TEMAS + " WHERE idTema = ?";

        Cursor cursor = read.rawQuery(sql, new String[]{Long.toString(idGet)});

        if (cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndex("idTema"));
            nomeImegem = cursor.getString(cursor.getColumnIndex("nomeImagem"));
            imagemUrl = cursor.getString(cursor.getColumnIndex("imagemUrl"));
            remoteId = DBHelper.getLongOrNull(cursor, "remoteId");
        }
        cursor.close();

        if (id != null) {
            Tema tema = new Tema();
            tema.setId(id);
            tema.setNomeImagem(nomeImegem);
            tema.setImageUrl(imagemUrl);
            tema.setRemoteId(remoteId);
            return tema;
        }

        return null;
    }

    /**
     * Method responsable for save a theme.
     *
     * @param theme theme to save
     * @return boolean
     */
    @Override
    public boolean save(Tema theme) {
        // tema vindo do EducAPI que ja existe localmente (sincronizado ou importado antes)
        if (theme.getRemoteId() != null && findByRemote(theme.getRemoteId(), theme.getNomeImagem()) != null) {
            return false;
        }
        ContentValues cv = new ContentValues();
        if (theme.getId() != null) {
            cv.put("idTema", theme.getId());
        }
        if (theme.getRemoteId() != null) {
            cv.put("remoteId", theme.getRemoteId());
        }
        cv.put("nomeImagem", theme.getNomeImagem());
        cv.put("imagemUrl", theme.getImageUrl());
        try {
            Long id = write.insertOrThrow(DBHelper.TABLE_TEMAS, null, cv);
            theme.setId(id);
            cv.clear();
            Log.d("SAVE", "Tema salvo com sucesso!");
        } catch (Exception e) {
            Log.d("SAVE", "Erro ao salvar tema!");
            return false;
        }
        return true;
    }

    /**
     * Method responsable for delete a theme.
     *
     * @param temaDel theme to delete
     * @return boolean
     */
    @Override
    public boolean delete(Tema temaDel) {
        try {
            return deleteById(temaDel.getId());
        } catch (Exception e) {
            //Log.d("SAVE", "Erro ao remover!");
            return false;
        }
    }

    /**
     * Method responsable for delete a theme.
     *
     * @param temaId id of theme
     * @return boolean
     */
    @Override
    public boolean deleteById(Long temaId) {
        try {

            // delete saved local image file
            Tema tema = get(temaId);
            if (tema != null && tema.getRemoteId() != null) {
                // usuario removeu: a sincronizacao nao deve trazer de volta
                ThemeSyncManager.markDismissed(context, tema.getRemoteId());
            }
            if (tema != null && tema.getImageUrl() != null) {
                String imageSt = tema.getImageUrl();
                if (imageSt.startsWith("/")) {
                    File imageFile = new File(imageSt);
                    imageFile.delete();
                }
            }

            String[] args = {temaId.toString()};
            write.delete(DBHelper.TABLE_DESAFIOS, "idTema=?", args);
            write.delete(DBHelper.TABLE_TEMAS, "idTema=?", args);
            Toast.makeText(context, "Tema removido com sucesso!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Erro ao tentar remover!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Method responsable for update a theme.
     *
     * @param tema
     * @return boolean
     */
    @Override
    public boolean update(Tema tema) {
        try {
            ContentValues cv = new ContentValues();

            cv.put("nomeImagem", tema.getNomeImagem());
            cv.put("imagemUrl", tema.getImageUrl());

            write.update(DBHelper.TABLE_TEMAS, cv, "idTema=" + tema.getId(), null);
            cv.clear();
        } catch (Exception e) {
            Toast.makeText(context, "Erro ao atualizar tema!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Method responsable for get all themes.
     *
     * @return List<Tema>
     */
    @Override
    public List<Tema> findAll() {

        List<Tema> temas = new ArrayList<>();

        String sql = "SELECT * FROM " + DBHelper.TABLE_TEMAS + " ;";
        Cursor cursor = read.rawQuery(sql, null);

        cursor.move(0);
        while (cursor.moveToNext()) {
            Tema tema = new Tema();
            Long id = cursor.getLong(cursor.getColumnIndex("idTema"));
            String nomeImegem = cursor.getString(cursor.getColumnIndex("nomeImagem"));
            String imagemUrl = cursor.getString(cursor.getColumnIndex("imagemUrl"));

            tema.setId(id);
            tema.setNomeImagem(nomeImegem);
            tema.setImageUrl(imagemUrl);
            tema.setRemoteId(DBHelper.getLongOrNull(cursor, "remoteId"));

            temas.add(tema);
            //Log.i("Size: ", "List Size " + temas.size());
        }
        cursor.close();

        return temas;
    }

    /**
     * Finds the local copy of a theme that came from EducAPI.
     * Also adopts themes imported before the sync existed (they were stored using the
     * EducAPI id as local id), matching by id + name.
     *
     * @param remoteId theme id in EducAPI
     * @param name     theme name in EducAPI
     * @return local theme or null
     */
    public Tema findByRemote(Long remoteId, String name) {
        Cursor cursor = read.rawQuery("SELECT * FROM " + DBHelper.TABLE_TEMAS + " WHERE remoteId = ?",
                new String[]{Long.toString(remoteId)});
        if (cursor.moveToFirst()) {
            Tema tema = fromCursor(cursor);
            cursor.close();
            return tema;
        }
        cursor.close();

        cursor = read.rawQuery("SELECT * FROM " + DBHelper.TABLE_TEMAS
                + " WHERE remoteId IS NULL AND idTema = ?", new String[]{Long.toString(remoteId)});
        Tema legacy = null;
        if (cursor.moveToFirst()) {
            legacy = fromCursor(cursor);
        }
        cursor.close();

        if (legacy != null && name != null && name.equals(legacy.getNomeImagem())) {
            ContentValues cv = new ContentValues();
            cv.put("remoteId", remoteId);
            write.update(DBHelper.TABLE_TEMAS, cv, "idTema=?", new String[]{legacy.getId().toString()});
            legacy.setRemoteId(remoteId);
            return legacy;
        }
        return null;
    }

    private Tema fromCursor(Cursor cursor) {
        Tema tema = new Tema();
        tema.setId(cursor.getLong(cursor.getColumnIndex("idTema")));
        tema.setNomeImagem(cursor.getString(cursor.getColumnIndex("nomeImagem")));
        tema.setImageUrl(cursor.getString(cursor.getColumnIndex("imagemUrl")));
        tema.setRemoteId(DBHelper.getLongOrNull(cursor, "remoteId"));
        return tema;
    }
}
