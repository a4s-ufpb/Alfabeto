package com.napoleao.alphabeto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentesAuxiliares;
import com.napoleao.alphabeto.controller.GerenteDeDesafios;

public class NiveisActivity extends AppCompatActivity implements View.OnClickListener{

    private int temaSelecionado;
    private Long idTema;
    private GerenteDeDesafios gerenteDeDesafios;
    private ComponentesAuxiliares componentesAuxiliares;

    int[] botoes = {R.id.txtVogais, R.id.txtConsoantes, R.id.txtAlfabeto};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_niveis);
        super.onCreate(savedInstanceState);

        instanciarTextButtons();

        TextView txtNivel = findViewById(R.id.txtNivel);
        ComponentesAuxiliares.definirFonte(this, txtNivel);

        gerenteDeDesafios = new GerenteDeDesafios();
        componentesAuxiliares = new ComponentesAuxiliares();

        Bundle extras = getIntent().getExtras();
        temaSelecionado = extras.getInt("tema");
        idTema = extras.getLong("idTema", -1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtVogais:
            case R.id.btnVogais:
                componentesAuxiliares.impedirDuploClique(this);
                gerenteDeDesafios.ditarPalavra("Vogais");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        invocarIntent(VogalActivity.class);
                    }
                }, 1300);
                break;

            case R.id.txtConsoantes:
            case R.id.btnConsoantes:
                componentesAuxiliares.impedirDuploClique(this);
                gerenteDeDesafios.ditarPalavra("Consoantes");

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        invocarIntent(ConsoanteActivity.class);
                    }
                }, 1300);
                break;

            case R.id.txtAlfabeto:
            case R.id.btnAlfabeto:
                componentesAuxiliares.impedirDuploClique(this);
                gerenteDeDesafios.ditarPalavra("Alfabeto");

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        invocarIntent(AlfabetoActivity.class);
                    }
                }, 1300);
                break;

            case R.id.btnVoltarNiveis:
                onBackPressed();
                break;
        }
    }

    /**
     * Mapeia o botão de voltar nativo do Android, para que feche a Activity atual e retorne à
     * Activity anterior.
     */
    @Override
    public void onBackPressed(){
        Intent it = new Intent(NiveisActivity.this, MainActivity.class);
        startActivity(it);
        finish();
    }

    /**
     * Define os ID's dos TextView's, adicionam eventos de clique e modifica a fonte.
     */
    private void instanciarTextButtons(){
        for (int buttons : botoes) {
            TextView btn = findViewById(buttons);
            ComponentesAuxiliares.definirFonte(this, btn);
            btn.setOnClickListener(this);
        }
    }

    /**
     * Abre uma nova Activity.
     * @param novaActivity Activity a ser aberta
     */
    private void invocarIntent(Class novaActivity){
        Intent it = new Intent(NiveisActivity.this, novaActivity);
        it.putExtra("tema", temaSelecionado);
        it.putExtra("idTema", idTema);
        startActivity(it);
        finish();
    }
}
