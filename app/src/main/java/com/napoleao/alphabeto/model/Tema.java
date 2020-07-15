package com.napoleao.alphabeto.model;

import android.widget.ImageView;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Tema {

    private Long id;
    private int idImagem;

    @SerializedName(value = "name")
    private String nomeImagem;
    private String imageUrl;
    private List<Challenge> challenges = new ArrayList<>();

    public Tema() {
    }


    public Tema(Long id, String nomeImagem, String imageUrl, List<Challenge> challenges){
        this.id = id;
        this.nomeImagem = nomeImagem;
        this.imageUrl = imageUrl;
        this.challenges = challenges;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIdImagem() {
        return idImagem;
    }

    public void setIdImagem(int idImagem) {
        this.idImagem = idImagem;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public int getImagem() {
        return idImagem;
    }

    public void setImagem(ImageView imagem) {
        this.idImagem = idImagem;
    }

    public String getNomeImagem() {
        return nomeImagem;
    }

    public void setNomeImagem(String nomeImagem) {
        this.nomeImagem = nomeImagem;
    }


}
