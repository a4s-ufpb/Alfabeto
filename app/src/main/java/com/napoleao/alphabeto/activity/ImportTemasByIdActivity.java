package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class ImportTemasByIdActivity extends AppCompatActivity {

    private Tema tema;
    private ConstraintLayout constraintLayout;
    private ImageView imageTema;
    private TextView txtNameTema, txtQuantDesafios;
    private EditText idContext;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_by_id);

        constraintLayout = findViewById(R.id.constraintImport);
        progressBar = findViewById(R.id.progressBar);
        imageTema = findViewById(R.id.imageTemaImportado);
        txtNameTema = findViewById(R.id.textNameTemaImportado);
        txtQuantDesafios = findViewById(R.id.textQuantDesafios);
        idContext = findViewById(R.id.editIdContext);
    }

    public void importContext(View view){
        String idToSearch = idContext.getText().toString();
        if (idToSearch.isEmpty()){
            Toast.makeText(ImportTemasByIdActivity.this, "Insira um número de ID!", Toast.LENGTH_SHORT).show();
        }else{
            Long id = Long.parseLong(idToSearch);
            progressBar.setVisibility(View.VISIBLE);
            Call call = new RetrofitConfig().contextService().getContextById(id);
            call.enqueue(new Callback<Tema>() {
                @Override
                public void onResponse(Call<Tema> call, Response<Tema> response) {
                    if (response.isSuccessful()){
                        Log.d("DEBUG", "Entrou");
                        tema = response.body();
                        txtNameTema.setText(tema.getNomeImagem());
                        carregarImagem();
                        String quantDesafios = "("+tema.getChallenges().size() + " Desafios)";
                        txtQuantDesafios.setText(quantDesafios);
                        progressBar.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    }else {
                        Toast.makeText(getApplicationContext(), "Tema não encontrado, verifique o ID!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Houve um erro na requisição. Tente novamente!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void salvarTema(View view){
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

    private void carregarImagem(){
        String url = tema.getImageUrl();
        Picasso.get()
                .load(url)
                .fit()
                .error(R.drawable.error)
                .centerCrop()
                .into(imageTema);
    }

    public void voltar(View v){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it = new Intent(ImportTemasByIdActivity.this, ImportOptionsActivity.class);
        startActivity(it);
        finish();
    }
}