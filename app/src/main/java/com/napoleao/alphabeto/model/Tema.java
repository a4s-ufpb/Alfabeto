package com.napoleao.alphabeto.model;

import android.widget.ImageView;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsable for represent theme.
 */
public class Tema {

    private Long id;
    private int idImagem;

    @SerializedName(value = "name")
    private String nomeImagem;
    private String imageUrl;
    private List<Challenge> challenges = new ArrayList<>();
    // id do tema no EducAPI (null se foi criado localmente)
    private Long remoteId;

    // quem criou o tema no EducAPI. Pode vir como objeto (com "name") ou como texto, por isso
    // fica como JsonElement: assim um formato diferente nunca quebra a leitura da lista.
    @SerializedName(value = "user", alternate = {"owner", "creator", "author", "createdBy",
            "userName", "ownerName", "creatorName"})
    private JsonElement creator;

    /**
     * Constructor for Tema.
     */
    public Tema() {
    }

    /**
     * Constructor for Tema.
     *
     * @param id
     * @param nomeImagem
     * @param imageUrl
     */
    public Tema(Long id, String nomeImagem, String imageUrl, List<Challenge> challenges) {
        this.id = id;
        this.nomeImagem = nomeImagem;
        this.imageUrl = imageUrl;
        this.challenges = challenges;
    }

    /**
     * @return name of who created the theme in EducAPI, or null if the API did not send it.
     */
    public String getCreatorName() {
        if (creator == null || creator.isJsonNull()) {
            return null;
        }
        if (creator.isJsonPrimitive()) {
            return creator.getAsJsonPrimitive().isString() ? clean(creator.getAsString()) : null;
        }
        if (creator.isJsonObject()) {
            JsonObject obj = creator.getAsJsonObject();
            String[] keys = {"name", "nome", "username"};
            for (String key : keys) {
                if (obj.has(key) && obj.get(key).isJsonPrimitive()) {
                    String value = clean(obj.get(key).getAsString());
                    if (value != null) {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    private static String clean(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
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
