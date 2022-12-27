package com.napoleao.alphabeto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.helper.RetrofitConfig;
import com.napoleao.alphabeto.helper.dao.ChallengesDAO;
import com.napoleao.alphabeto.helper.dao.TemasDAO;
import com.napoleao.alphabeto.model.Challenge;
import com.napoleao.alphabeto.model.Tema;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTemaActivity extends AppCompatActivity {

    private Tema tema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        if(this.tema == null) {
            this.tema = new Tema();
        }


    }

    public void salvarTema(View view) {
        TemasDAO temasDAO = new TemasDAO(getApplicationContext());
        ChallengesDAO challengesDAO = new ChallengesDAO(getApplicationContext());
        if (tema == null){
            Toast.makeText(getApplicationContext(), "Erro! Você carregou algum tema?", Toast.LENGTH_LONG).show();
        }else if (tema.getChallenges().isEmpty()){
            Toast.makeText(getApplicationContext(), "Erro! Este contexto não possui desafios!", Toast.LENGTH_LONG).show();
        }else {
            if (temasDAO.save(tema)){
                for (Challenge c: tema.getChallenges()){
                    c.setIdTema(tema.getId());
                    Log.d("DESAFIO", "Desafio attr: " + c.getId());
                    challengesDAO.save(c);
                }
                Toast.makeText(getApplicationContext(), "Tema salvo com sucesso!", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "Ops! Esse tema já foi salvo anteriormente!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void voltar(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it = new Intent(CreateTemaActivity.this, ImportOptionsActivity.class);
        startActivity(it);
        finish();
    }
}