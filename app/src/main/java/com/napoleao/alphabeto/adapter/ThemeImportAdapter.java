package com.napoleao.alphabeto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ImageLoadUtil;
import com.napoleao.alphabeto.helper.dao.TemasDAO;
import com.napoleao.alphabeto.model.Tema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter of the list of EducAPI themes shown in the import screen.
 * <p>
 * Each row shows the theme name, how many challenges it has, who created it and a tag:
 * "Sem desafios" (cannot be imported) or "Ja importado" (already in the app). Rows with a tag
 * are disabled. The list of ticked themes is owned by the caller, so the ticks survive new
 * searches, new pages and (in the next phase) tab changes.
 */
public class ThemeImportAdapter extends RecyclerView.Adapter<ThemeImportAdapter.MyViewHolder> {

    private final Context context;
    private final List<Tema> themes;
    private final List<Tema> selectedThemes;
    // EducAPI theme id -> already saved in the app? (avoids querying SQLite on every bind)
    private final Map<Long, Boolean> importedCache = new HashMap<>();

    public ThemeImportAdapter(Context context, List<Tema> themes, List<Tema> selectedThemes) {
        this.context = context;
        this.themes = themes;
        this.selectedThemes = selectedThemes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_import_theme, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Tema tema = themes.get(position);
        boolean hasChallenges = hasChallenges(tema);
        boolean imported = isImported(tema);
        boolean selectable = hasChallenges && !imported;

        holder.textNameTheme.setText(tema.getNomeImagem());

        int total = hasChallenges ? tema.getChallenges().size() : 0;
        holder.textQuantChallenge.setText(total == 1
                ? context.getString(R.string.import_desafio_um)
                : context.getString(R.string.import_desafios_n, total));

        String creator = tema.getCreatorName();
        if (creator == null) {
            holder.textCreator.setVisibility(View.GONE);
        } else {
            holder.textCreator.setVisibility(View.VISIBLE);
            holder.textCreator.setText(context.getString(R.string.import_criado_por, creator));
        }

        if (!hasChallenges) {
            holder.textTag.setVisibility(View.VISIBLE);
            holder.textTag.setText(R.string.import_tag_sem_desafios);
            holder.textTag.setBackgroundResource(R.drawable.bg_tag_empty);
            holder.textTag.setTextColor(ContextCompat.getColor(context, R.color.tag_empty_text));
        } else if (imported) {
            holder.textTag.setVisibility(View.VISIBLE);
            holder.textTag.setText(R.string.import_tag_importado);
            holder.textTag.setBackgroundResource(R.drawable.bg_tag_imported);
            holder.textTag.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            holder.textTag.setVisibility(View.GONE);
        }

        // disabled rows are dimmed, but the tag stays fully visible
        float alpha = selectable ? 1f : 0.55f;
        holder.imageTheme.setAlpha(alpha);
        holder.textNameTheme.setAlpha(alpha);
        holder.textQuantChallenge.setAlpha(alpha);
        holder.textCreator.setAlpha(alpha);
        holder.checkBox.setAlpha(alpha);

        holder.checkBox.setEnabled(selectable);
        holder.checkBox.setChecked(selectable && indexOfSelected(tema) >= 0);

        // the whole row is the touch target (the check box itself is not clickable)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) {
                    return;
                }
                Tema clicked = themes.get(pos);
                if (!hasChallenges(clicked)) {
                    Toast.makeText(context, R.string.import_msg_sem_desafios, Toast.LENGTH_LONG).show();
                } else if (isImported(clicked)) {
                    Toast.makeText(context, R.string.import_msg_ja_importado, Toast.LENGTH_SHORT).show();
                } else {
                    int idx = indexOfSelected(clicked);
                    if (idx >= 0) {
                        selectedThemes.remove(idx);
                    } else {
                        selectedThemes.add(clicked);
                    }
                    notifyItemChanged(pos);
                }
            }
        });

        ImageLoadUtil.getInstance().loadImage(tema.getImageUrl(), holder.imageTheme, context);
    }

    private boolean hasChallenges(Tema tema) {
        return tema.getChallenges() != null && !tema.getChallenges().isEmpty();
    }

    /** True if the theme is already saved in the app (matched by the EducAPI id). */
    private boolean isImported(Tema tema) {
        Long id = tema.getId();
        if (id == null) {
            return false;
        }
        Boolean cached = importedCache.get(id);
        if (cached == null) {
            cached = TemasDAO.getInstance(context.getApplicationContext())
                    .findByRemote(id, tema.getNomeImagem()) != null;
            importedCache.put(id, cached);
        }
        return cached;
    }

    /** Compares by EducAPI id (the same theme can come again after a new search). */
    private int indexOfSelected(Tema tema) {
        for (int i = 0; i < selectedThemes.size(); i++) {
            Tema s = selectedThemes.get(i);
            if (s == tema || (s.getId() != null && s.getId().equals(tema.getId()))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    public List<Tema> getSelectedThemes() {
        return selectedThemes;
    }

    /**
     * Class responsible for managing the view holder.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageTheme;
        TextView textNameTheme;
        TextView textQuantChallenge;
        TextView textCreator;
        TextView textTag;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageTheme = itemView.findViewById(R.id.imageTheme);
            textNameTheme = itemView.findViewById(R.id.textNameTheme);
            textQuantChallenge = itemView.findViewById(R.id.textQuantChallenge);
            textCreator = itemView.findViewById(R.id.textCreator);
            textTag = itemView.findViewById(R.id.textTag);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
