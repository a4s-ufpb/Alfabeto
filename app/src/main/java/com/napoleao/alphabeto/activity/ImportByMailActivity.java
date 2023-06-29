package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.adapter.ThemeAdapterImportByMail;
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

/**
 * Class responsible for managing the screen for importing themes by e-mail.
 */
public class ImportByMailActivity extends AppCompatActivity {

    private ConstraintLayout constraintImportByMail;
    private EditText editEmail;
    private ProgressBar progressBarEmail;
    private RecyclerView recyclerImportByMail;
    private List<Tema> loadedThemes = new ArrayList<>();
    private List<Tema> selectedThemes = new ArrayList<>();

    /**
     * Method responsible for initializing the screen components.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_by_mail);

        constraintImportByMail = findViewById(R.id.constraintImportByMail);
        editEmail = findViewById(R.id.editTextTextEmail);
        progressBarEmail = findViewById(R.id.progressBarEmail);
        recyclerImportByMail = findViewById(R.id.recyclerTemasByMail);

    }

    /**
     * Method responsible for importing themes by e-mail.
     *
     * @param view
     */
    public void importByEmail(View view) {
        if (editEmail.getText().toString().isEmpty()) {
            Toast.makeText(ImportByMailActivity.this, "Insira um endereço de e-mail!", Toast.LENGTH_SHORT).show();
        } else {
            progressBarEmail.setVisibility(View.VISIBLE);
            Call call = new RetrofitConfig().contextService().getContextsByUser(editEmail.getText().toString());
            call.enqueue(new Callback<ContextPageResponse>() {
                @Override
                public void onResponse(Call<ContextPageResponse> call, Response<ContextPageResponse> response) {
                    if (response.isSuccessful()) {
                        ContextPageResponse apiResponse = response.body();
                        loadedThemes = apiResponse.getContent();
                        progressBarEmail.setVisibility(View.GONE);
                        configureRecyclerView();
                        constraintImportByMail.setVisibility(View.VISIBLE);
                    } else {
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
            //Log.d("DEBUG", "TEM?" + temasCarregados.size());
        }
    }

    /**
     * Configure RecyclerView.
     */
    private void configureRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        ThemeAdapterImportByMail themeAdapterImportByMail = new ThemeAdapterImportByMail(ImportByMailActivity.this, loadedThemes);
        recyclerImportByMail.setLayoutManager(layoutManager);
        recyclerImportByMail.setHasFixedSize(true);
        recyclerImportByMail.setAdapter(themeAdapterImportByMail);
        selectedThemes = themeAdapterImportByMail.getSelectedThemes();
    }

    /**
     * Method responsible for returning to the previous screen.
     *
     * @param view
     */
    public void backAction(View view) {
        onBackPressed();
    }

    /**
     * Method responsible for saving the selected themes.
     *
     * @param view
     */
    public void saveThemeByEmail(View view) {
        TemasDAO temasDAO = TemasDAO.getInstance(getApplicationContext());
        ChallengesDAO challengesDAO = ChallengesDAO.getInstance(getApplicationContext());
        int contSucessos = 0;

        if (loadedThemes == null) {
            Toast.makeText(getApplicationContext(), "Erro! Nenhum contexto carregado!\nVocê inseriu algum e-mail?", Toast.LENGTH_LONG).show();
        } else {
            for (Tema t : selectedThemes) {
                if (!t.getChallenges().isEmpty()) {

                    if (temasDAO.save(t)) {
                        for (Challenge c : t.getChallenges()) {
                            c.setIdTema(t.getId());
                            //Log.d("DESAFIO", "Desafio attr: " + c.getId());
                            challengesDAO.save(c);
                        }
                        contSucessos++;
                    }
                }
            }
            Toast.makeText(ImportByMailActivity.this, contSucessos + " novo(s) tema(s) salvo(s)!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(ImportByMailActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**
     * Method responsible for returning to the previous screen.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it = new Intent(ImportByMailActivity.this, ImportOptionsActivity.class);
        startActivity(it);
        finish();
    }

}