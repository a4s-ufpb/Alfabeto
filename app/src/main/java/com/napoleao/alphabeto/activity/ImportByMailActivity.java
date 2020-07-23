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
import com.napoleao.alphabeto.adapter.TemasAdapter;
import com.napoleao.alphabeto.helper.RetrofitConfig;
import com.napoleao.alphabeto.helper.dao.ChallengesDAO;
import com.napoleao.alphabeto.helper.dao.TemasDAO;
import com.napoleao.alphabeto.model.Challenge;
import com.napoleao.alphabeto.model.Tema;
import com.napoleao.alphabeto.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ImportByMailActivity extends AppCompatActivity {

    private User user;
    private List<User> userList = new ArrayList<>();
    private ConstraintLayout constraintImportByMail;
    private EditText editEmail;
    private ProgressBar progressBarEmail;
    private RecyclerView recyclerImportByMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_by_mail);

        constraintImportByMail = findViewById(R.id.constraintImportByMail);
        editEmail = findViewById(R.id.editTextTextEmail);
        progressBarEmail = findViewById(R.id.progressBarEmail);
        recyclerImportByMail = findViewById(R.id.recyclerTemasByMail);

        getAllUser();
    }

    private boolean verificarUsuario(String email){
        for (int i = 0; i < userList.size(); i++){
            if (userList.get(i).getEmail().equals(email)){
                user = userList.get(i);
                Log.d("DEBUG", "ID" + user.getId());
                return true;
            }
        }
        return false;
    }

    public void importByEmail(View view){
        if (editEmail.getText().toString().isEmpty()){
            Toast.makeText(ImportByMailActivity.this, "Insira um endereço de e-mail!", Toast.LENGTH_SHORT).show();
        }else {
            boolean userExists = verificarUsuario(editEmail.getText().toString());
            if (userExists){
                progressBarEmail.setVisibility(View.VISIBLE);
                Call call = new RetrofitConfig().userService().getUserById(user.getId());
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()){
                            user = response.body();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(ImportByMailActivity.this, "Erro ao montar visualização!", Toast.LENGTH_SHORT).show();
                    }
                });
                getContextsByUser();
                Log.d("DEBUG", "TEM?" + user.getContexts().size());
            }
            else {
                Toast.makeText(ImportByMailActivity.this, "E-mail não encontrado. Tente novamente!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void getAllUser(){
        Call call = new RetrofitConfig().userService().allUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    userList = response.body();
                    Log.d("DEBUG", "SIZE" + userList.size());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ImportByMailActivity.this, "Não foi possível recuperar a lista de usuários. " +
                        "Volte e tente novamente!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getContextsByUser(){
        for (User u: userList){
            System.out.println(u.getEmail());
        }
        Call callContexts = new RetrofitConfig().contextService().getContextsByUser(user.getId());
        callContexts.enqueue(new Callback<List<Tema>>() {
            @Override
            public void onResponse(Call<List<Tema>> call, Response<List<Tema>> response) {
                if (response.isSuccessful()){
                    user.setContexts(response.body());
                    progressBarEmail.setVisibility(View.GONE);
                    configureRecyclerView();
                    constraintImportByMail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Tema>> call, Throwable t) {
                Toast.makeText(ImportByMailActivity.this, "Erro na requisição. Tente novamente!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Configura o RecyclerView.
     */
    private void configureRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        TemasAdapter temasAdapter = new TemasAdapter(ImportByMailActivity.this, user.getContexts());
        recyclerImportByMail.setLayoutManager(layoutManager);
        recyclerImportByMail.setHasFixedSize(true);
        recyclerImportByMail.setAdapter(temasAdapter);
    }

    public void voltar(View view){
        onBackPressed();
    }

    public void salvarTemaByMail(View view){
        TemasDAO temasDAO = new TemasDAO(getApplicationContext());
        ChallengesDAO challengesDAO = new ChallengesDAO(getApplicationContext());

        if (user == null){
            Toast.makeText(getApplicationContext(), "Erro! Nenhum contexto carregado!\nVocê inseriu algum e-mail?", Toast.LENGTH_LONG).show();
        }else{
            for (Tema t: user.getContexts()){
                temasDAO.save(t);

                for (Challenge c: t.getChallenges()){
                    c.setIdTema(t.getId());
                    Log.d("DESAFIO", "Desafio attr: " + c.getId());
                    challengesDAO.save(c);
                }
            }

            Toast.makeText(ImportByMailActivity.this, "Temas salvos com sucesso!", Toast.LENGTH_LONG).show();
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