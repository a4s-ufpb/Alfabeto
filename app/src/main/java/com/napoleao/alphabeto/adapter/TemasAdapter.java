package com.napoleao.alphabeto.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.NiveisActivity;
import com.napoleao.alphabeto.controller.SingletonAudio;
import com.napoleao.alphabeto.model.Tema;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TemasAdapter extends RecyclerView.Adapter<TemasAdapter.MyViewHolder> {

    private Context context;
    private List<Tema> temasImportados;

    public TemasAdapter(Context context, List<Tema> temasImportados){
        this.context = context;
        this.temasImportados = temasImportados;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.temas_importados_adapter, parent, false);
        return new TemasAdapter.MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Tema tema = temasImportados.get(position);
        holder.textNameTema.setText(tema.getNomeImagem());

        Picasso.get()
                .load(tema.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageTema);
    }

    @Override
    public int getItemCount() {
        return temasImportados.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageTema;
        TextView textNameTema;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageTema = itemView.findViewById(R.id.imageTema);
            textNameTema = itemView.findViewById(R.id.textNameTema);
        }
    }
}
