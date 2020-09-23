package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.adapter.TemasAdapterImportByMail;
import com.napoleao.alphabeto.helper.RetrofitConfig;
import com.napoleao.alphabeto.helper.dao.ChallengesDAO;
import com.napoleao.alphabeto.helper.dao.TemasDAO;
import com.napoleao.alphabeto.model.Challenge;
import com.napoleao.alphabeto.api.response.ContextPageResponse;
import com.napoleao.alphabeto.model.Tema;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImportByMailActivity extends AppCompatActivity {

    private ConstraintLayout constraintImportByMail;
    private EditText editEmail;
    private ProgressBar progressBarEmail;
    private RecyclerView recyclerImportByMail;
    private List<Tema> temasCarregados = new ArrayList<>();
    private List<Tema> temasSelecionados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_by_mail);

        constraintImportByMail = findViewById(R.id.constraintImportByMail);
        editEmail = findViewById(R.id.editTextTextEmail);
        progressBarEmail = findViewById(R.id.progressBarEmail);
        recyclerImportByMail = findViewById(R.id.recyclerTemasByMail);

    }

    public void importByEmail(View view){
        if (editEmail.getText().toString().isEmpty()){
            Toast.makeText(ImportByMailActivity.this, "Insira um endereço de e-mail!", Toast.LENGTH_SHORT).show();
        }else {
            progressBarEmail.setVisibility(View.VISIBLE);
            Call call = new RetrofitConfig().contextService().getContextsByUser(editEmail.getText().toString());
                call.enqueue(new Callback<ContextPageResponse>() {
                    @Override
                    public void onResponse(Call<ContextPageResponse> call, Response<ContextPageResponse> response) {
                        if (response.isSuccessful()){
                            ContextPageResponse apiResponse = response.body();
                            temasCarregados = apiResponse.getContent();
                            progressBarEmail.setVisibility(View.GONE);
                            configureRecyclerView();
                            constraintImportByMail.setVisibility(View.VISIBLE);
                        }else{
                            progressBarEmail.setVisibility(View.GONE);
                            Toast.makeText(ImportByMailActivity.this, "Erro. Verifique o e-mail digitado e tente novamente!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ContextPageResponse> call, Throwable t) {
                        Toast.makeText(ImportByMailActivity.this, "Erro na requisição. Tente novamente!", Toast.LENGTH_SHORT).show();
                        progressBarEmail.setVisibility(View.GONE);
                    }

                });
                Log.d("DEBUG", "TEM?" + temasCarregados.size());
        }
    }

    /**
     * Configura o RecyclerView.
     */
    private void configureRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        TemasAdapterImportByMail temasAdapterImportByMail = new TemasAdapterImportByMail(ImportByMailActivity.this, temasCarregados);
        recyclerImportByMail.setLayoutManager(layoutManager);
        recyclerImportByMail.setHasFixedSize(true);
        recyclerImportByMail.setAdapter(temasAdapterImportByMail);
        temasSelecionados = temasAdapterImportByMail.getTemasSelecionados();
    }

    public void voltar(View view){
        onBackPressed();
    }

    public void salvarTemaByMail(View view){
        TemasDAO temasDAO = new TemasDAO(getApplicationContext());
        ChallengesDAO challengesDAO = new ChallengesDAO(getApplicationContext());
        int contSucessos = 0;

        if (temasCarregados == null){
            Toast.makeText(getApplicationContext(), "Erro! Nenhum contexto carregado!\nVocê inseriu algum e-mail?", Toast.LENGTH_LONG).show();
        }else{
            for (Tema t: temasSelecionados){
                if (!t.getChallenges().isEmpty()){

                    if(temasDAO.save(t)){
                        for (Challenge c: t.getChallenges()){
                            c.setIdTema(t.getId());
                            Log.d("DESAFIO", "Desafio attr: " + c.getId());
                            challengesDAO.save(c);
                        }
                        contSucessos++;
                    }
                }
            }
            Toast.makeText(ImportByMailActivity.this, contSucessos + " novo(s) tema(s) salvo(s)!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it = new Intent(ImportByMailActivity.this, ImportOptionsActivity.class);
        startActivity(it);
        finish();
    }

}