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

public class AlfabetoActivity extends AppCompatActivity implements View.OnClickListener {
    //Componentes da interface
    private ImageView imagem;
    private TextView txtQuiz;
    private View botoesAlfabeto;
    private LottieAnimationView animationView;
    int[] botoes = {R.id.btnA,R.id.btnB,R.id.btnC,R.id.btnD,R.id.btnE,R.id.btnF,R.id.btnG,R.id.btnH,
            R.id.btnI,R.id.btnJ,R.id.btnK,R.id.btnL,R.id.btnM,R.id.btnN,R.id.btnO,R.id.btnP,R.id.btnQ,
            R.id.btnR,R.id.btnS,R.id.btnT,R.id.btnU,R.id.btnV,R.id.btnW,R.id.btnX,R.id.btnY,R.id.btnZ};
    //--------------------------------------------------------------------------------------------//
    private List<Challenge> listDesafios = new ArrayList<>();
    private GerenteDeDesafios gerenteDeDesafios;
    private ComponentesAuxiliares componentesAuxiliares;
    private int tipoImagem = 0;
    private FabricaDesafios fabricaDesafios = new FabricaDesafios(listDesafios);
    private static final int NIVEL_SELECIONADO = 2;
    private int temaSelecionado;
    private Long idTema;
    private char[] desafio;
    private int indice = 0;
    private SingletonJogador jogador = SingletonJogador.getJogador();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alfabeto);

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
        imagem = findViewById(R.id.imageAlfabeto);
        txtQuiz = findViewById(R.id.textAlfabeto);
        botoesAlfabeto = findViewById(R.id.botoesAlfabeto);
        componentesAuxiliares.instanciarBotoes(botoesAlfabeto, this, botoes, this);
        animationView = findViewById(R.id.animationAlfabeto);

        gerenteDeDesafios.setAtributosAlfabeto(imagem, txtQuiz, listDesafios, indice, tipoImagem);

        //Definindo os primeiros elementos a serem iniciados
        txtQuiz.setText(gerenteDeDesafios.definirPalavraAlfabeto(gerenteDeDesafios.dandoEspacos(listDesafios.get(indice).getWord())));
        desafio = gerenteDeDesafios.definirPalavraAlfabeto(listDesafios.get(indice).getWord()).toCharArray();
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
        String resposta = gerenteDeDesafios.verificarAlternativa(this, alternativa, listDesafios.get(indice).getWord(),desafio, jogador);
        txtQuiz.setText(gerenteDeDesafios.dandoEspacos(resposta));

        boolean acertou = gerenteDeDesafios.verificaResposta(listDesafios.get(indice).getWord(), resposta);
        if (acertou){
            animationView.playAnimation();
            GerenteDeDesafios.acertou(this, AppConfig.getInstance(this).getCurrentSound());
            indice++;
            if(indice == listDesafios.size() || indice == 5) {
                componentesAuxiliares.invocarIntent(this, FimDeJogoActivity.class, temaSelecionado, idTema);
            }else if(indice < listDesafios.size()){
                componentesAuxiliares.desligarBotoes(botoes, botoesAlfabeto);
                Handler handle = new Handler();
                handle.postDelayed(() -> {
                    gerenteDeDesafios.setAtributosAlfabeto(imagem, txtQuiz, listDesafios, indice, tipoImagem);
                    desafio = gerenteDeDesafios.definirPalavraAlfabeto(listDesafios.get(indice).getWord()).toCharArray();
                    //Mudando o desafio. Para isso é chamado o método que seta a quantidade de espaços que formam a palavra
                    txtQuiz.setText(gerenteDeDesafios.definirPalavraAlfabeto(gerenteDeDesafios.dandoEspacos(listDesafios.get(indice).getWord())));
                    componentesAuxiliares.ligarBotoes(botoes, botoesAlfabeto);
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
    public void voltarAlfabeto(View v){
        onBackPressed();
    }

    /**
     * Mapeia o botão de fechar atividade.
     * @param v View mapeada
     */
    public void fecharAlfabeto(View v){
        componentesAuxiliares.exibirConfirmacaoFechar(this);
    }

}
