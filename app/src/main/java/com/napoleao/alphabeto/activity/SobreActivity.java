package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentesAuxiliares;

/**
 * Classe responsável por gerenciar a tela de informações sobre o aplicativo.
 */
public class SobreActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        TextView txtSobre, txtDevby, txtProjeto, txtCreditos;
        txtSobre = findViewById(R.id.txtSobre);
        txtDevby = findViewById(R.id.txtDev);
        txtProjeto = findViewById(R.id.txtProjeto);
        txtCreditos = findViewById(R.id.txtCredit);

        ComponentesAuxiliares.definirFonte(this, txtSobre);
        ComponentesAuxiliares.definirFonte(this, txtDevby);
        ComponentesAuxiliares.definirFonte(this, txtProjeto);
        ComponentesAuxiliares.definirFonte(this, txtCreditos);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnMenuInicialSobre:
                finish();
                break;
            case R.id.btnPixabay:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pixabay.com/pt/"));
                startActivity(intent);
                break;
        }
    }
}
