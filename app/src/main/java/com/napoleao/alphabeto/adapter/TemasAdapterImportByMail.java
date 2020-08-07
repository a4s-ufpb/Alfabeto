package com.napoleao.alphabeto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.model.Tema;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TemasAdapterImportByMail extends RecyclerView.Adapter<TemasAdapterImportByMail.MyViewHolder>{
    private Context context;
    private List<Tema> temasImportados;
    private List<Tema> temasSelecionados = new ArrayList<>();

    public TemasAdapterImportByMail(Context context, List<Tema> temasImportados){
        this.context = context;
        this.temasImportados = temasImportados;
    }

    @NonNull
    @Override
    public TemasAdapterImportByMail.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.import_adapter, parent, false);
        return new TemasAdapterImportByMail.MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Tema tema = temasImportados.get(position);
        holder.textNameTema.setText(tema.getNomeImagem());
        holder.checkBox.setOnClickListener(view -> {
            if (holder.checkBox.isChecked()){
                temasSelecionados.add(tema);
            }else {
                temasSelecionados.remove(tema);
            }
        });

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
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageTema = itemView.findViewById(R.id.imageTema);
            textNameTema = itemView.findViewById(R.id.textNameTema);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public List<Tema> getTemasSelecionados(){
        return temasSelecionados;
    }
}
