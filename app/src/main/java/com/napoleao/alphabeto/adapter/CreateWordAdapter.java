package com.napoleao.alphabeto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.napoleao.alphabeto.R;

import androidx.recyclerview.widget.RecyclerView;

import com.napoleao.alphabeto.activity.CreateThemeActivity;
import com.napoleao.alphabeto.activity.util.ImageLoadUtil;

/**
 * Class responsible for managing the adapter of the theme creation screen.
 */
public class CreateWordAdapter extends RecyclerView.Adapter<CreateWordAdapter.ViewHolder> {
    private CreateThemeActivity createTheme;
    private String TAG = "CreateWordAdapter";
    private Context activityContext;
    private boolean isDeleteMode;
    private boolean isEditMode;

    /**
     * Constructor method of the class.
     *
     * @param createTheme
     * @param context
     * @param isDeleteMode
     * @param isEditMode
     */
    public CreateWordAdapter(CreateThemeActivity createTheme, Context context, boolean isDeleteMode, boolean isEditMode) {
        this.createTheme = createTheme;
        this.activityContext = context;
        this.isDeleteMode = isDeleteMode;
        this.isEditMode = isEditMode;
    }

    /**
     * Method responsible for inflating the layout of the card.
     *
     * @param parent
     * @param viewType
     * @return
     */
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_theme, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Method responsible for returning the number of items in the list.
     *
     * @return
     */
    public int getItemCount() {
        return createTheme.theme.getChallenges().size();
    }

    /**
     * Method responsible for binding the data to the card.
     *
     * @param holder
     * @param position
     */
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (this.isDeleteMode) {
            holder.btnDel.setVisibility(View.VISIBLE);
        }
        if (this.isEditMode) {
            holder.btnEdit.setVisibility(View.VISIBLE);
        }

        holder.themeName.setText(createTheme.theme.getChallenges().get(holder.getAdapterPosition()).getWord());
        ImageLoadUtil.getInstance().loadImage(createTheme.theme.getChallenges().get(holder.getAdapterPosition()).getImageUrl(), holder.themeImage, activityContext);

        holder.themeImage.setOnClickListener(v -> {
            v.setClickable(false);
        });

        holder.btnDel.setOnClickListener(v -> {
            this.deleteSelectedTheme(holder.getAdapterPosition());
        });

        holder.btnEdit.setOnClickListener(v -> {
            this.editSelectedTheme(holder.getAdapterPosition());
        });
    }

    /**
     * Method responsible for deleting the selected theme.
     *
     * @param position
     */
    private void deleteSelectedTheme(int position) {
        createTheme.removeWord(createTheme.theme.getChallenges().get(position));
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, createTheme.theme.getChallenges().size());
    }

    /**
     * Method responsible for editing the selected theme.
     *
     * @param position
     */
    private void editSelectedTheme(int position) {
        createTheme.editWord(createTheme.theme.getChallenges().get(position));
    }

    /**
     * Class responsible for managing the card view.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView themeImage;
        TextView themeName;
        ImageButton btnDel;
        ImageButton btnEdit;

        private ViewHolder(View itemView) {
            super(itemView);
            themeImage = itemView.findViewById(R.id.img_left);
            themeName = itemView.findViewById(R.id.tv_theme_name_left);
            btnDel = itemView.findViewById(R.id.btnDel);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

}