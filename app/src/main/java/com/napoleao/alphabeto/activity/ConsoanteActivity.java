package com.napoleao.alphabeto.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentesAuxiliares;
import com.napoleao.alphabeto.config.AppConfig;
import com.napoleao.alphabeto.controller.GerenteDeDesafios;
import com.napoleao.alphabeto.controller.FabricaDesafios;
import com.napoleao.alphabeto.controller.SingletonJogador;
import com.napoleao.alphabeto.helper.dao.ChallengesDAO;
import com.napoleao.alphabeto.model.Challenge;

import java.util.ArrayList;
import java.util.List;

public class ConsoanteActivity extends AppCompatActivity implements View.OnClickListener {
    //Componentes da interface
    private ImageView imagem;
    private TextView txtQuiz;
    private View botoesConsoantes;
    private LottieAnimationView animationView;
    private int[] botoes = {R.id.btnB,R.id.btnC,R.id.btnD,R.id.btnF,R.id.btnG,R.id.btnH,R.id.btnJ,R.id.btnK,R.id.btnL,
            R.id.btnM,R.id.btnN,R.id.btnP,R.id.btnQ,R.id.btnR,R.id.btnS,R.id.btnT,R.id.btnV,R.id.btnW,R.id.btnX,
            R.id.btnY,R.id.btnZ};
    //--------------------------------------------------------------------------------------------//
    private List<Challenge> listDesafios = new ArrayList<com.napoleao.alphabeto.model.Challenge>();
    private GerenteDeDesafios gerenteDeDesafios;
    private ComponentesAuxiliares componentesAuxiliares;
    private int tipoImagem = 0;
    private FabricaDesafios fabricaDesafios = new FabricaDesafios(listDesafios);
    private static final int NIVEL_SELECIONADO = 1;
    private int temaSelecionado;
    private Long idTema;
    private char[] desafio;//Array responsável por guardar e atualizar o desafio de acordo com as respostas
    private int indice = 0;
    private SingletonJogador jogador = SingletonJogador.getJogador();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consoantes);

        gerenteDeDesafios = new GerenteDeDesafios();
        componentesAuxiliares = new ComponentesAuxiliares();

        //Obtendo o tema escolhido
        Bundle extras = getIntent().getExtras();
        idTema = extras.getLong("idTema");
        temaSelecionado = extras.getInt("tema");

        //Carregando os temas de acordo com a escolha
        if (idTema != -1){
            ChallengesDAO challengesDAO = new ChallengesDAO(this);
            listDesafios = challengesDAO.findById(idTema);
            listDesafios = gerenteDeDesafios.randomListDesafios(listDesafios);
            tipoImagem = 1;
            Log.d("DEBUG", "LIST: " + listDesafios.size());
        }else {
            fabricaDesafios.escolhaDeTema(temaSelecionado);
            listDesafios = gerenteDeDesafios.carregarTemas(listDesafios, NIVEL_SELECIONADO);
        }

        //Instanciando a interface
        imagem = findViewById(R.id.imageConsoante);
        txtQuiz = findViewById(R.id.textConsoante);
        botoesConsoantes = findViewById(R.id.botoesConsoantes);
        componentesAuxiliares.instanciarBotoes(botoesConsoantes, this, botoes, this);
        animationView = findViewById(R.id.animationConsoantes);

        gerenteDeDesafios.setAtributosConsoantes(imagem, txtQuiz, listDesafios, indice, tipoImagem);

        //Definindo os primeiros elementos a serem iniciados
        txtQuiz.setText(gerenteDeDesafios.definirPalavraConsoante(gerenteDeDesafios.dandoEspacos(listDesafios.get(indice).getWord())));
        desafio = gerenteDeDesafios.definirPalavraConsoante(listDesafios.get(indice).getWord()).toCharArray();
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String alternativa = button.getText().toString();
        verificaResposta(alternativa.charAt(0));
    }

    /**
     * Reproduz o nome da imagem que representa o desafio.
     * @param v necessário para o mapeamento via XML
     */
    public void falarImagem(View v){
        gerenteDeDesafios.falarImagem(listDesafios, indice);
    }

    /**
     * Verifica se o caractere escolhido existe no desafio.
     * @param alternativa Caractere escolhido (botão clicado no teclado).
     */
    private void verificaResposta(char alternativa){
        String resposta = gerenteDeDesafios.verificarAlternativa(this, alternativa, listDesafios.get(indice).getWord().toLowerCase(),desafio, jogador);
        txtQuiz.setText(gerenteDeDesafios.dandoEspacos(resposta));

        boolean acertou = gerenteDeDesafios.verificaResposta(listDesafios.get(indice).getWord().toLowerCase(), resposta);
        if (acertou){
            animationView.playAnimation();
            GerenteDeDesafios.acertou(this, AppConfig.getInstance(this).getCurrentSound());
            indice++;
            if(indice == listDesafios.size() || indice == 5) {
                componentesAuxiliares.invocarIntent(this, FimDeJogoActivity.class, temaSelecionado, idTema);
            }else if(indice < listDesafios.size()){
                componentesAuxiliares.desligarBotoes(botoes, botoesConsoantes);
                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gerenteDeDesafios.setAtributosConsoantes(imagem, txtQuiz, listDesafios, indice, tipoImagem);
                        desafio = gerenteDeDesafios.definirPalavraConsoante(listDesafios.get(indice).getWord()).toCharArray();
                        //Mudando o desafio. Para isso é chamado o método que seta a quantidade de espaços que formam a palavra
                        txtQuiz.setText(gerenteDeDesafios.definirPalavraConsoante(gerenteDeDesafios.dandoEspacos(listDesafios.get(indice).getWord())));
                        componentesAuxiliares.ligarBotoes(botoes, botoesConsoantes);
                    }
                }, 2000);
            }
        }
    }

    /**
     * Mapeia o botão de voltar nativo do Android, exibe um AlertDialog perguntando se o jogador
     * deseja voltar ao menu de seleção de temas.
     */
    @Override
    public void onBackPressed(){
        componentesAuxiliares.exibirConfirmacaoVoltar(this);
    }

    /**
     * Mapeia o botão de voltar presente.
     * @param v View mapeada
     */
    public void voltarConsoantes(View v){
        onBackPressed();
    }

    /**
     * Mapeia o botão de fechar atividade.
     * @param v View mapeada
     */
    public void fecharConsoantes(View v){
        componentesAuxiliares.exibirConfirmacaoFechar(this);
    }

}