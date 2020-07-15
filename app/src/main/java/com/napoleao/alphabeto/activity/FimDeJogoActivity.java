package com.napoleao.alphabeto.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentesAuxiliares;
import com.napoleao.alphabeto.controller.GerenteDeDesafios;
import com.napoleao.alphabeto.controller.SingletonJogador;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

public class FimDeJogoActivity extends AppCompatActivity implements View.OnClickListener {

    private SingletonJogador jogador = SingletonJogador.getJogador();
    private int select;
    private Long idTema;
    int[] textIds = {R.id.txtPontuacao, R.id.txtRetornarTemas, R.id.txtRepetir, R.id.txtFraseFim};
    private GerenteDeDesafios gerenteDeDesafios;
    private ComponentesAuxiliares componentesAuxiliares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fim_de_jogo);

        instanciarTextButtons();

        Bundle extras = getIntent().getExtras();
        select = extras.getInt("tema");
        idTema = extras.getLong("idTema");

        gerenteDeDesafios = new GerenteDeDesafios();
        componentesAuxiliares = new ComponentesAuxiliares();

        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(this, R.raw.applause);
        mediaPlayer.start();

        TextView txt = findViewById(R.id.txtFraseFim);
        String texto;
        if(jogador.getPontuacao() <= 1){
            texto = "Você é capaz!\nContinue tentando!";
            txt.setText(texto);
            gerenteDeDesafios.ditarPalavra(texto);
        }else if(jogador.getPontuacao() > 1 && jogador.getPontuacao() <= 2){
            texto = "Parabéns!\nVocê foi muito bem!";
            txt.setText(texto);
            gerenteDeDesafios.ditarPalavra(texto);
        }else if(jogador.getPontuacao() > 2 && jogador.getPontuacao() <= 3){
            texto = "Parabéns!\n Você foi demais!";
            txt.setText(texto);
            gerenteDeDesafios.ditarPalavra(texto);
        }

        final ScaleRatingBar ratingBar = findViewById(R.id.simpleRatingBar);
        ratingBar.setNumStars(3);//Número de estrelas que aparecem
        ratingBar.setMinimumStars(0);//Número mínimo de de estrelas que iniciam preenchidas
        ratingBar.setRating(jogador.getPontuacao());//Número que define o preenchimento das estrelas
        ratingBar.setStarPadding(10);
        ratingBar.setStepSize(0.5f);
        ratingBar.setIsIndicator(false);
        ratingBar.setClickable(false);
        ratingBar.setScrollable(false);
        ratingBar.setClearRatingEnabled(false);
        ratingBar.setEmptyDrawableRes(R.drawable.estrela_vazia);
        ratingBar.setFilledDrawableRes(R.drawable.estrela_preenchida);
        ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
            }
        });
        jogador.resetarPontuacao();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.voltar_temas:
                Intent it = new Intent(FimDeJogoActivity.this, MainActivity.class);
                startActivity(it);
                finish();
                break;
            case R.id.repetir:
                it = new Intent(FimDeJogoActivity.this, NiveisActivity.class);
                it.putExtra("tema", select);
                it.putExtra("idTema", idTema);
                startActivity(it);
                finish();
                break;
        }
    }

    /**
     * Define os ID's dos TextView's e define a fonte do TextView.
     */
    private void instanciarTextButtons(){
        for (int buttons : textIds) {
            TextView btn = findViewById(buttons);
            ComponentesAuxiliares.definirFonte(this, btn);
        }
    }

    @Override
    public void onBackPressed(){
        componentesAuxiliares.exibirConfirmacaoFechar(this);
    }
}
