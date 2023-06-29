package com.napoleao.alphabeto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ImageLoadUtil;
import com.napoleao.alphabeto.model.Tema;

import java.util.List;

/**
 * Class responsible for managing the adapter of the theme import screen.
 */
public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.MyViewHolder> {

    private Context context;
    private List<Tema> importedThemes;

    /**
     * Constructor method.
     *
     * @param context
     * @param importedThemes
     */
    public ThemeAdapter(Context context, List<Tema> importedThemes) {
        this.context = context;
        this.importedThemes = importedThemes;
    }

    /**
     * Method responsible for creating the view holder.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.temas_importados_adapter, parent, false);
        return new ThemeAdapter.MyViewHolder(itemList);
    }

    /**
     * Method responsible for binding the view holder.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Tema tema = importedThemes.get(position);
        holder.textNameTheme.setText(tema.getNomeImagem());

        ImageLoadUtil.getInstance().loadImage(tema.getImageUrl(), holder.imageTheme, context);
    }

    /**
     * Method responsible for returning the size of the list.
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return importedThemes.size();
    }

    /**
     * Class responsible for creating the view holder.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageTheme;
        TextView textNameTheme;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageTheme = itemView.findViewById(R.id.imageTheme);
            textNameTheme = itemView.findViewById(R.id.textNameTheme);
        }
    }
}
