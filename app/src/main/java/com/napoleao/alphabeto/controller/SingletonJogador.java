package com.napoleao.alphabeto.controller;

/**
 * Classe responsável por gerenciar a pontuação do jogador.
 */
public class SingletonJogador {

    private float pontuacao = 3;

    private static SingletonJogador jogador;

    private SingletonJogador(){

    }

    public static SingletonJogador getJogador(){
        if(jogador == null){
            jogador = new SingletonJogador();
        }
        return jogador;
    }

    public float getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(float pontuacao) {
        this.pontuacao = pontuacao;
    }

    public void resetarPontuacao(){
        pontuacao = 3;
    }

}
