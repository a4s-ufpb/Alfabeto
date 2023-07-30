package com.napoleao.alphabeto.model;

import java.io.Serializable;

/**
 * Class responsable for represent challenge.
 */
public class Challenge implements Serializable {

    private Long id;
    private int idImagem;
    private String word;
    private String imageUrl;
    private Long idTema;

    /**
     * Constructor for Challenge.
     */
    public Challenge() {
    }

    /**
     * Constructor for Challenge.
     *
     * @param idImagem
     * @param nomeImagem
     */
    public Challenge(int idImagem, String nomeImagem) {
        this.idImagem = idImagem;
        this.word = nomeImagem;
    }

    /**
     * Constructor for Challenge.
     *
     * @param id
     * @param word
     * @param imageUrl
     */
    public Challenge(Long id, String word, String imageUrl) {
        this.id = id;
        this.word = word;
        this.imageUrl = imageUrl;
    }

    /**
     * Constructor for Challenge.
     *
     * @param id
     * @param word
     * @param imageUrl
     * @param idTema
     */
    public Challenge(Long id, String word, String imageUrl, Long idTema) {
        this.id = id;
        this.word = word;
        this.imageUrl = imageUrl;
        this.idTema = idTema;
    }

    public int getIdImagem() {
        return idImagem;
    }

    public Long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Long getIdTema() {
        return idTema;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIdTema(Long idTema) {
        this.idTema = idTema;
    }
}
