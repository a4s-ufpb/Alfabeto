package com.napoleao.alphabeto.controller;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.model.Challenge;

import java.util.List;

public class GerenteDeTemas {

    private List<Challenge> challenges;

    public GerenteDeTemas(List<Challenge> challenges) {
        this.challenges = challenges;
    }


    public void escolhaDeTema(int select) {
        if (select == 0) {
            instanciarCores();
        } else if (select == 1) {
            instanciarObjetos();
        } else if (select == 2) {
            instanciarAnimais();
        }else if (select == 3){
            instanciarFrutas();
        } else if (select == 4) {
            instanciarBrinquedos();
        } else if (select == 5) {
            instanciarPartesDoCorpo();
        }else if (select == 6) {
            instanciarPaises();
        }
    }

    void instanciarAnimais(){
        challenges.clear();

        Challenge a1 = new Challenge(R.drawable.ovelha, "ovelha");
        Challenge a2 = new Challenge(R.drawable.touro, "touro");
        Challenge a3 = new Challenge(R.drawable.cachorro, "cachorro");
        Challenge a4 = new Challenge(R.drawable.cavalo, "cavalo");
        Challenge a5 = new Challenge(R.drawable.elefante, "elefante");
        Challenge a6 = new Challenge(R.drawable.gato, "gato");
        Challenge a7 = new Challenge(R.drawable.girafa, "girafa");
        Challenge a8 = new Challenge(R.drawable.leao, "leão");
        Challenge a9 = new Challenge(R.drawable.pavao, "pavão");
        Challenge a10 = new Challenge(R.drawable.peixe, "peixe");
        Challenge a11 = new Challenge(R.drawable.preguica, "preguiça");
        Challenge a12 = new Challenge(R.drawable.rato, "rato");
        Challenge a13 = new Challenge(R.drawable.rinoceronte, "rinoceronte");
        Challenge a14 = new Challenge(R.drawable.zebra, "zebra");
        Challenge a15 = new Challenge(R.drawable.tigre, "tigre");

        challenges.add(a1); challenges.add(a2);
        challenges.add(a3);
        challenges.add(a4);
        challenges.add(a5);
        challenges.add(a6);
        challenges.add(a7);
        challenges.add(a8);
        challenges.add(a9);
        challenges.add(a10);
        challenges.add(a11);
        challenges.add(a12);
        challenges.add(a13);
        challenges.add(a14);
        challenges.add(a15);
    }

    void instanciarPaises(){
        challenges.clear();

        Challenge p1 = new Challenge(R.drawable.australia, "austrália");
        Challenge p2 = new Challenge(R.drawable.brasil, "brasil");
        Challenge p3 = new Challenge(R.drawable.canada, "canadá");
        Challenge p4 = new Challenge(R.drawable.china, "china");
        Challenge p5 = new Challenge(R.drawable.colombia, "colômbia");
        Challenge p6 = new Challenge(R.drawable.cuba, "cuba");
        Challenge p7 = new Challenge(R.drawable.franca,  "frança");
        Challenge p8 = new Challenge(R.drawable.alemanha, "alemanha");
        Challenge p9 = new Challenge(R.drawable.holanda, "holanda");
        Challenge p10 = new Challenge(R.drawable.italia, "itália");
        Challenge p11 = new Challenge(R.drawable.japao, "japão");
        Challenge p12 = new Challenge(R.drawable.coreia, "coréia do\nsul");
        Challenge p13 = new Challenge(R.drawable.russia, "rússia");
        Challenge p14 = new Challenge(R.drawable.reino, "reino\nunido");
        Challenge p15 = new Challenge(R.drawable.usa, "estados\nunidos");

        challenges.add(p1); challenges.add(p2);
        challenges.add(p3);
        challenges.add(p4);
        challenges.add(p5);
        challenges.add(p6);
        challenges.add(p7);
        challenges.add(p8);
        challenges.add(p9);
        challenges.add(p10);
        challenges.add(p11);
        challenges.add(p12);
        challenges.add(p13);
        challenges.add(p14);
        challenges.add(p15);
    }

    void instanciarCores(){
        challenges.clear();

        Challenge c1 = new Challenge(R.drawable.amarelo, "amarelo");
        Challenge c2 = new Challenge(R.drawable.azul, "azul");
        Challenge c3 = new Challenge(R.drawable.cinza, "cinza");
        Challenge c4 = new Challenge(R.drawable.cor_laranja, "laranja");
        Challenge c5 = new Challenge(R.drawable.marrom, "marrom");
        Challenge c6 = new Challenge(R.drawable.preto, "preto");
        Challenge c7 = new Challenge(R.drawable.rosa, "rosa");
        Challenge c8 = new Challenge(R.drawable.roxo, "roxo");
        Challenge c9 = new Challenge(R.drawable.verde, "verde");
        Challenge c10 = new Challenge(R.drawable.vermelho, "vermelho");
        Challenge c11 = new Challenge(R.drawable.dourado, "dourado");
        Challenge c12 = new Challenge(R.drawable.azul_escuro, "azul\nmarinho");
        Challenge c13 = new Challenge(R.drawable.bege, "bege");
        Challenge c14 = new Challenge(R.drawable.prata, "prata");
        Challenge c15 = new Challenge(R.drawable.branco, "branco");

        challenges.add(c1); challenges.add(c2);
        challenges.add(c3);
        challenges.add(c4);
        challenges.add(c5);
        challenges.add(c6);
        challenges.add(c7);
        challenges.add(c8);
        challenges.add(c9);
        challenges.add(c10);
        challenges.add(c11);
        challenges.add(c12);
        challenges.add(c13);
        challenges.add(c14);
        challenges.add(c15);
    }

    void instanciarObjetos(){
        challenges.clear();

        Challenge o1 = new Challenge(R.drawable.pilha, "pilha");
        Challenge o2 = new Challenge(R.drawable.vaso, "vaso");
        Challenge o3 = new Challenge(R.drawable.mesa, "mesa");
        Challenge o4 = new Challenge(R.drawable.cubo_objeto, "cubo");
        Challenge o5 = new Challenge(R.drawable.livro, "livro");
        Challenge o6 = new Challenge(R.drawable.lupa, "lupa");
        Challenge o7 = new Challenge(R.drawable.martelo, "martelo");
        Challenge o8 = new Challenge(R.drawable.chave, "chave");
        Challenge o9 = new Challenge(R.drawable.oculos, "óculos");
        Challenge o10 = new Challenge(R.drawable.relogio, "relógio");
        Challenge o11 = new Challenge(R.drawable.televisao, "televisão");
        Challenge o12= new Challenge(R.drawable.tambor, "tambor");
        Challenge o13 = new Challenge(R.drawable.celular, "celular");
        Challenge o14 = new Challenge(R.drawable.xicara, "xícara");
        Challenge o15 = new Challenge(R.drawable.telefone, "telefone");

        challenges.add(o1); challenges.add(o2);
        challenges.add(o3);
        challenges.add(o4);
        challenges.add(o5);
        challenges.add(o6);
        challenges.add(o7);
        challenges.add(o8);
        challenges.add(o9);
        challenges.add(o10);
        challenges.add(o11);
        challenges.add(o12);
        challenges.add(o13);
        challenges.add(o14);
        challenges.add(o15);
    }

    void instanciarFrutas(){
        challenges.clear();

        Challenge f1 = new Challenge(R.drawable.abacaxi, "abacaxi");
        Challenge f2 = new Challenge(R.drawable.maca, "maçã");
        Challenge f3 = new Challenge(R.drawable.morango, "morango");
        Challenge f4 = new Challenge(R.drawable.pera, "pera");
        Challenge f5 = new Challenge(R.drawable.banana, "banana");
        Challenge f6 = new Challenge(R.drawable.laranja, "laranja");
        Challenge f7 = new Challenge(R.drawable.uva, "uva");
        Challenge f8 = new Challenge(R.drawable.manga, "manga");
        Challenge f9 = new Challenge(R.drawable.abacate, "abacate");
        Challenge f10 = new Challenge(R.drawable.caju, "caju");
        Challenge f11 = new Challenge(R.drawable.cereja, "cereja");
        Challenge f12 = new Challenge(R.drawable.coco, "coco");
        Challenge f13 = new Challenge(R.drawable.melancia, "melancia");
        Challenge f14 = new Challenge(R.drawable.mamao, "mamão");
        Challenge f15 = new Challenge(R.drawable.limao, "limão");

        challenges.add(f1); challenges.add(f2);
        challenges.add(f3);
        challenges.add(f4);
        challenges.add(f5);
        challenges.add(f6);
        challenges.add(f7);
        challenges.add(f8);
        challenges.add(f9);
        challenges.add(f10);
        challenges.add(f11);
        challenges.add(f12);
        challenges.add(f13);
        challenges.add(f14);
        challenges.add(f15);
    }

    void instanciarBrinquedos(){
        challenges.clear();

        Challenge b1 = new Challenge(R.drawable.aviao, "avião");
        Challenge b2 = new Challenge(R.drawable.moto, "moto");
        Challenge b3 = new Challenge(R.drawable.bola, "bola");
        Challenge b4 = new Challenge(R.drawable.pipa, "pipa");
        Challenge b5 = new Challenge(R.drawable.urso, "urso");
        Challenge b6 = new Challenge(R.drawable.balanco, "balanço");
        Challenge b7 = new Challenge(R.drawable.carro_brinquedo, "carro");
        Challenge b8 = new Challenge(R.drawable.trem, "trem");
        Challenge b9 = new Challenge(R.drawable.cavalo_brinquedo, "cavalo");
        Challenge b10 = new Challenge(R.drawable.robo, "robô");
        Challenge b11 = new Challenge(R.drawable.boneca, "boneca");
        Challenge b12 = new Challenge(R.drawable.boliche, "boliche");
        Challenge b13 = new Challenge(R.drawable.bicicleta, "bicicleta");
        Challenge b14 = new Challenge(R.drawable.gangorra, "gangorra");
        Challenge b15 = new Challenge(R.drawable.quebra_cabecas, "quebra-\ncabeça");

        challenges.add(b1); challenges.add(b2);
        challenges.add(b3);
        challenges.add(b4);
        challenges.add(b5);
        challenges.add(b6);
        challenges.add(b7);
        challenges.add(b8);
        challenges.add(b9);
        challenges.add(b10);
        challenges.add(b11);
        challenges.add(b12);
        challenges.add(b13);
        challenges.add(b14);
        challenges.add(b15);
    }

    void instanciarPartesDoCorpo(){
        challenges.clear();

        Challenge p1 = new Challenge(R.drawable.boca, "boca");
        Challenge p2 = new Challenge(R.drawable.mao, "mão");
        Challenge p3 = new Challenge(R.drawable.lingua, "língua");
        Challenge p4 = new Challenge(R.drawable.joelho, "joelho");
        Challenge p5 = new Challenge(R.drawable.cotovelo, "cotovelo");
        Challenge p6 = new Challenge(R.drawable.perna, "perna");
        Challenge p7 = new Challenge(R.drawable.braco, "braço");
        Challenge p8 = new Challenge(R.drawable.olho, "olho");
        Challenge p9 = new Challenge(R.drawable.pe, "pé");
        Challenge p10 = new Challenge(R.drawable.orelha, "orelha");
        Challenge p11 = new Challenge(R.drawable.cabeca, "cabeça");
        Challenge p12 = new Challenge(R.drawable.cerebro, "cérebro");
        Challenge p13 = new Challenge(R.drawable.coracao, "coração");
        Challenge p14 = new Challenge(R.drawable.nariz, "nariz");
        Challenge p15 = new Challenge(R.drawable.calcanhar, "calcanhar");

        challenges.add(p1); challenges.add(p2);
        challenges.add(p3);
        challenges.add(p4);
        challenges.add(p5);
        challenges.add(p6);
        challenges.add(p7);
        challenges.add(p8);
        challenges.add(p9);
        challenges.add(p10);
        challenges.add(p11);
        challenges.add(p12);
        challenges.add(p13);
        challenges.add(p14);
        challenges.add(p15);
    }

}
