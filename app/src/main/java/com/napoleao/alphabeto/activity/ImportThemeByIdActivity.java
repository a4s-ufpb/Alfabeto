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
import com.napoleao.alphabeto.activity.util.ImageLoadUtil;
import com.napoleao.alphabeto.helper.RetrofitConfig;
import com.napoleao.alphabeto.helper.dao.ChallengesDAO;
import com.napoleao.alphabeto.helper.dao.TemasDAO;
import com.napoleao.alphabeto.model.Challenge;
import com.napoleao.alphabeto.model.Tema;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class responsible for managing the theme import screen by ID.
 */
public class ImportThemeByIdActivity extends AppCompatActivity {

    private Tema theme;
    private ConstraintLayout constraintLayout;
    private ImageView imageTheme;
    private TextView txtNameTheme, txtQuantChallenge;
    private EditText idContext;
    private ProgressBar progressBar;

    /**
     * Method responsible for initializing the screen components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_by_id);

        constraintLayout = findViewById(R.id.constraintImport);
        progressBar = findViewById(R.id.progressBar);
        imageTheme = findViewById(R.id.imageThemeImported);
        txtNameTheme = findViewById(R.id.textNameThemeImported);
        txtQuantChallenge = findViewById(R.id.textQuantChallenge);
        idContext = findViewById(R.id.editIdContext);
    }

    /**
     * Method responsible for importing the theme by ID.
     *
     * @param view
     */
    public void importContext(View view) {
        String idToSearch = idContext.getText().toString();
        if (idToSearch.isEmpty()) {
            Toast.makeText(ImportThemeByIdActivity.this, "Insira um número de ID!", Toast.LENGTH_SHORT).show();
        } else {
            Long id = Long.parseLong(idToSearch);
            progressBar.setVisibility(View.VISIBLE);
            Call call = new RetrofitConfig().contextService().getContextById(id);
            call.enqueue(new Callback<Tema>() {
                @Override
                public void onResponse(Call<Tema> call, Response<Tema> response) {
                    if (response.isSuccessful()) {
                        //Log.d("DEBUG", "Entrou");
                        theme = response.body();
                        txtNameTheme.setText(theme.getNomeImagem());
                        loadImage();
                        String quantDesafios = "(" + theme.getChallenges().size() + " Desafios)";
                        txtQuantChallenge.setText(quantDesafios);
                        progressBar.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    } else {
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

    /**
     * Method responsible for saving the theme in the database.
     *
     * @param view
     */
    public void saveChanges(View view) {
        TemasDAO temasDAO = TemasDAO.getInstance(getApplicationContext());
        ChallengesDAO challengesDAO = ChallengesDAO.getInstance(getApplicationContext());
        if (theme == null) {
            Toast.makeText(getApplicationContext(), "Erro! Você carregou algum tema?", Toast.LENGTH_LONG).show();
        } else if (theme.getChallenges().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Erro! Este contexto não possui desafios!", Toast.LENGTH_LONG).show();
        } else {
            if (temasDAO.save(theme)) {
                for (Challenge c : theme.getChallenges()) {
                    c.setIdTema(theme.getId());
                    Log.d("DESAFIO", "Desafio attr: " + c.getId());
                    challengesDAO.save(c);
                }
                Toast.makeText(getApplicationContext(), "Tema salvo com sucesso!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ImportThemeByIdActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(), "Ops! Esse tema já foi salvo anteriormente!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * Method responsible for loading the theme image.
     */
    private void loadImage() {
        ImageLoadUtil.getInstance().loadImage(theme.getImageUrl(), imageTheme, getApplicationContext());
    }

    /**
     * Action button for returning to the previous screen.
     *
     * @param v
     */
    public void backAction(View v) {
        onBackPressed();
    }

    /**
     * Method responsible for returning to the previous screen.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it = new Intent(ImportThemeByIdActivity.this, ImportOptionsActivity.class);
        startActivity(it);
        finish();
    }
}