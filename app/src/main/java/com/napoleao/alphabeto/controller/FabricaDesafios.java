package com.napoleao.alphabeto.controller;

import com.napoleao.alphabeto.model.Challenge;

import java.util.List;

/**
 * Classe responsável por gerenciar a fabricação de desafios.
 */
public class FabricaDesafios {

    private GerenteDeTemas gerenteDeTemas;

    public FabricaDesafios(List<Challenge> challenges) {
        this.gerenteDeTemas = new GerenteDeTemas(challenges);
    }

    public void escolhaDeTema(int temaSelecionado) {
        this.gerenteDeTemas.escolhaDeTema(temaSelecionado);
    }

}
