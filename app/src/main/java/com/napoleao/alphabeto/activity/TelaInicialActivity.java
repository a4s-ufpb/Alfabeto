package com.napoleao.alphabeto.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentesAuxiliares;
import com.napoleao.alphabeto.controller.SingletonAudio;

/**
 * Classe responsável por gerenciar a tela inicial do aplicativo.
 */
public class TelaInicialActivity extends AppCompatActivity implements View.OnClickListener {

    int[] botoes = {R.id.btnIniciar, R.id.btnConfig, R.id.btnSobre};
    private SingletonAudio tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_tela_inicial);
        super.onCreate(savedInstanceState);

        TextView txtKidsInicial = findViewById(R.id.txtKidsInicial);
        TextView txtJogar = findViewById(R.id.textJogar);
        TextView txtConfig = findViewById(R.id.textConfig);
        TextView txtSobre = findViewById(R.id.textSobre);

        ComponentesAuxiliares.definirFonte(this, txtKidsInicial);
        ComponentesAuxiliares.definirFonte(this, txtJogar);
        ComponentesAuxiliares.definirFonte(this, txtConfig);
        ComponentesAuxiliares.definirFonte(this, txtSobre);

        tts = SingletonAudio.getSingleton(this);

        instanciarBotoes();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnIniciar:
                Intent it = new Intent(TelaInicialActivity.this, MainActivity.class);
                startActivity(it);
                break;
            case R.id.btnSobre:
                it = new Intent(TelaInicialActivity.this, SobreActivity.class);
                startActivity(it);
                break;
            case R.id.btnConfig:
                it = new Intent(TelaInicialActivity.this, ConfiguracoesActivity.class);
                startActivity(it);
                break;
        }
    }

    public void instanciarBotoes(){
        int i;
        for(i = 0; i < botoes.length; i++){
            ImageButton btn = findViewById(botoes[i]);
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        mensagem.setTitle("Confirmação");
        mensagem.setIcon(null);
        mensagem.setMessage("Deseja sair do jogo?");

        mensagem.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast = Toast.makeText(TelaInicialActivity.this, "Até a próxima!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                tts.stopTts();
                finish();
            }
        });

        mensagem.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast = Toast.makeText(TelaInicialActivity.this, "Continuando...", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        });
        mensagem.show();
    }

}
