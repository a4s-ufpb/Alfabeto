package com.napoleao.alphabeto.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentesAuxiliares;
import com.napoleao.alphabeto.model.Challenge;
import com.napoleao.alphabeto.model.Tema;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GerenteDeDesafios {

    private ComponentesAuxiliares componentesAuxiliares;
    private SingletonAudio tts;


    public GerenteDeDesafios() {
        this.componentesAuxiliares = new ComponentesAuxiliares();
        this.tts = SingletonAudio.getSingleton();
    }

    /**
     * Reproduz o nome da palavra indicada.
     * @param palavra Palavra a ser reproduzida.
     */
    public void ditarPalavra(String palavra){
        tts.ditarFoto(palavra);
    }

    /**
     * Recebe uma palavra (desafio) e transforma todos os caracteres vogais em espaços.
     * @param vogal Desafio do nível vogais
     * @return String referente ao desafio
     */
    //Recebe o nome do desafio, deixa as consoantes e transforma as vogais em espaços.
    public String definirPalavraVogal(String vogal) {
        char[] caracteres = {'a','e','i','o','u'};
        char[] palavra = vogal.toCharArray();

        for(int i = 0; i < palavra.length; i++){
            for (char caractere : caracteres) {
                if (palavra[i] == caractere) {
                    palavra[i] = '_';
                    break;
                }
            }
        }

        return formatarDesafio(palavra);
    }

    /**
     * Recebe uma palavra (desafio) e transforma todos os caracteres consoantes em espaços.
     * @param consoante Desafio do nível consoantes
     * @return String referente ao desafio
     */
    public String definirPalavraConsoante(String consoante){
        char[] caracteres = {'b','c','d','f','g','h','j','k','l','m','n','p','q',
                'r','s','t','v','w','x','y','z'};
        char[] palavra = consoante.toCharArray();

        for(int i = 0; i < palavra.length; i++){
            for (char caractere : caracteres) {
                if (palavra[i] == caractere) {
                    palavra[i] = '_';
                    break;
                }
            }
        }

        return formatarDesafio(palavra);
    }

    /**
     * Recebe uma palavra (desafio) e transforma todas as letras em espaços (com excecção ao caracteres especiais).
     * @param alfabeto Desafio do nível alfabeto
     * @return String referente ao desafio
     */
    public String definirPalavraAlfabeto(String alfabeto){
        char[] caracteres = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q',
                'r','s','t','u','v','w','x','y','z'};
        char[] palavra = alfabeto.toCharArray();

        for(int i = 0; i < palavra.length; i++){
            for (char caractere : caracteres) {
                if (palavra[i] == caractere) {
                    palavra[i] = '_';
                    break;
                }
            }
        }

        return formatarDesafio(palavra);
    }

    /**
     * Transforma um Array de Char em uma String.
     * @param palavra Array de char
     * @return String formatada
     */
    private String formatarDesafio(char[] palavra){
        StringBuilder nome = new StringBuilder();
        for (char c : palavra) {
            nome.append(c);
        }

        return nome.toString();
    }

    /**
     * Recebe um caractere, uma palavra, e um array de caracteres, depois verifica se o caractere inserido existe na palavra.
     * @param context Activity atual
     * @param alternativa Caractere (alternativa do teclado)
     * @param palavra Palavra referente ao desafio que deve ser resolvido
     * @param palavraFormatada Array com os caracteres que estão sendo exibidos no desafio
     * @param jogador Objeto utilizado para representar o jogador, nele esta contido a pontuação do mesmo, que é decrementada em 0.20f se a
     *                resposta dada for incorreta.
     * @return desafio atualizado mediante a resposta do usuário.
     */
    public String verificarAlternativa(Context context, char alternativa, String palavra, char[] palavraFormatada, SingletonJogador jogador){
        char[] palavraPura = palavra.toCharArray();
        boolean certo = false;

        for(int i = 0; i < palavraPura.length; i++){
            if(palavraPura[i] == alternativa){
                palavraFormatada[i] = alternativa;
                certo = true;
            }
        }

        if (!certo){
            componentesAuxiliares.vibrar(context);
            jogador.setPontuacao(jogador.getPontuacao() - 0.20f);

        }else{
            componentesAuxiliares.vibrar(context);
        }

        return formatarDesafio(palavraFormatada);
    }

    /**
     * Recebe uma lista com temas e escolhe 5 deles à depender do nível escolhido.
     * @param listTemas Lista com temas carregados
     * @param nivel Inteiro que indica qual o nível selecionado pelo usuário
     * @return Uma lista com 5 desafios do tema e nível selecionado.
     */
    public List<Challenge> carregarTemas(List<Challenge> listTemas, int nivel){
        List<Challenge> temas = new ArrayList<>();

        int i;
        if(nivel == 0){
            for(i = 0; i < 5; i++){
                temas.add(listTemas.get(i));
            }
        }else if(nivel == 1){
            for(i = 5; i < 10; i++){
                temas.add(listTemas.get(i));
            }
        }else{
            for(i = 10; i <= 14; i++){
                temas.add(listTemas.get(i));
            }
        }

        return temas;
    }
    /**
     * Recebe uma lista com desafios e randoniza os índices.
     * @param desafios Lista com desafios importados
     * @return Uma lista com os desafios randonizados sem repetição.
     */
    public List<Challenge> randomListDesafios(List<Challenge> desafios){
        List<Challenge> desafiosRandom = new ArrayList<>();
        Set<Integer> indices = new HashSet<>();
        Random random = new Random();

        for (int i = 0; i < desafios.size(); i++){
            indices.add(random.nextInt(desafios.size()));
        }
        Integer[] indicesConvertidos = indices.toArray(new Integer[indices.size()]);

        for (Integer indicesConvertido : indicesConvertidos) {
            desafiosRandom.add(desafios.get(indicesConvertido));
        }

        return desafiosRandom;
    }

    /**
     * Verifica se a resposta é igual ao desafio.
     * @param palavra Nome da palavra que deve ser completada
     * @param resposta Resposta atual do usuário
     * @return true se ambas Strings forem iguais;
     *         false se não forem iguais.
     */
    public boolean verificaResposta(String palavra, String resposta){
        return resposta.equals(palavra);
    }

    /**
     * Define os elementos que irão ser mostrados na tela de desafios do nível Vogais.
     * @param imagem ImageView da interface
     * @param textoImagem TextView da interface que mostrará o desafio
     * @param temas Lista dos temas carregados
     * @param indice Índice atual da lista de temas
     */
    public void setAtributosVogais(ImageView imagem, TextView textoImagem, List<Challenge> temas, int indice, int tipoImagem){
        if (tipoImagem == 0){
            imagem.setImageResource(temas.get(indice).getIdImagem());
            textoImagem.setText(definirPalavraVogal(temas.get(indice).getWord()));
        }else {
            Picasso.get()
                    .load(temas.get(indice).getImageUrl())
                    .fit()
                    .error(R.drawable.error)
                    .centerInside()
                    .into(imagem);
        }

    }

    /**
     * Define os elementos que irão ser mostrados na tela de desafios do nível Consoantes.
     * @param imagem ImageView da interface
     * @param textoImagem TextView da interface que mostrará o desafio
     * @param temas Lista dos temas carregados
     * @param indice Índice atual da lista de temas
     */
    public void setAtributosConsoantes(ImageView imagem, TextView textoImagem, List<Challenge> temas, int indice, int tipoImagem){
        if (tipoImagem == 0){
            imagem.setImageResource(temas.get(indice).getIdImagem());
            textoImagem.setText(definirPalavraConsoante(temas.get(indice).getWord()));
        }else {
            Picasso.get()
                    .load(temas.get(indice).getImageUrl())
                    .fit()
                    .error(R.drawable.error)
                    .centerInside()
                    .into(imagem);
        }
    }

    /**
     * Define os elementos que irão ser mostrados na tela de desafios do nível Alfabeto.
     * @param imagem ImageView da interface
     * @param textoImagem TextView da interface que mostrará o desafio
     * @param temas Lista dos temas carregados
     * @param indice Índice atual da lista de temas
     */
    public void setAtributosAlfabeto(ImageView imagem, TextView textoImagem, List<Challenge> temas, int indice, int tipoImagem){
        if (tipoImagem == 0){
            imagem.setImageResource(temas.get(indice).getIdImagem());
            textoImagem.setText(definirPalavraAlfabeto(temas.get(indice).getWord()));
        }else {
            Picasso.get()
                    .load(temas.get(indice).getImageUrl())
                    .fit()
                    .error(R.drawable.error)
                    .centerInside()
                    .into(imagem);
        }
    }

    /**
     * Reproduz um som indicando o acerto do usuário.
     * @param context Activity atual
     * @param opcao String que define qual som deve ser reproduzido
     */
    public static void acertou(Context context, String opcao){
        MediaPlayer acertou;

        if(opcao.equals("Opção 01")){
            acertou = MediaPlayer.create(context, R.raw.acertou);
        }else if(opcao.equals("Opção 02")){
            acertou = MediaPlayer.create(context, R.raw.acertou2);
        }else {
            acertou = MediaPlayer.create(context, R.raw.acertou3);

        }
        acertou.start();
    }

    /**
     * Adiciona um espaço a cada caractere que forma o desafio.
     * @param desafio Palavra que deve ser completada pelo usuário
     * @return Desafio com espaços entre caracteres
     */
    public String dandoEspacos(String desafio) {
        StringBuilder novaString = new StringBuilder();
        novaString.append(" ");
        for(int i = 0; i < desafio.length(); i++) {
            novaString.append(desafio.charAt(i)) ;
            novaString.append(" ");
        }

        return novaString.toString();
    }

    /**
     * Reproduz o nome da imagem que está sendo exibida ao usuário.
     * @param listTema Lista dos temas carregados
     * @param indice Índice atual da lista de temas
     */
    public void falarImagem(List<Challenge> listTema, int indice){
        tts.ditarFoto(listTema.get(indice).getWord());
    }

}
