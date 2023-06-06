package com.napoleao.alphabeto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentesAuxiliares;

/**
 * Classe responsável por gerenciar a tela de Splash Screen.
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        super.onCreate(savedInstanceState);

        TextView txtKids = findViewById(R.id.textKids);
        ComponentesAuxiliares.definirFonte(this, txtKids);

        //Inserindo tempo de delay para Splash Screen
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarJogo();
            }
        }, 2000);

    }

    protected void mostrarJogo(){
        Intent it = new Intent(SplashScreenActivity.this,  TelaInicialActivity.class);
        startActivity(it);
        finish();
    }
}
