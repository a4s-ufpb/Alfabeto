package com.napoleao.alphabeto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentsUtil;
import com.napoleao.alphabeto.controller.ManageChallenges;

/**
 * Class responsible for managing the level selection screen.
 */
public class LevelsActivity extends AppCompatActivity implements View.OnClickListener {

    private int selectedTheme;
    private Long idTheme;
    private ManageChallenges manageChallenges;
    private ComponentsUtil componentsUtil;

    int[] buttons = {R.id.txtVowel, R.id.txtConsonant, R.id.txtAlphabet};

    /**
     * Method responsible for initializing the screen components.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_levels);
        super.onCreate(savedInstanceState);

        allocTextButtons();

        TextView txtNivel = findViewById(R.id.txtNivel);
        ComponentsUtil.setFont(this, txtNivel);

        manageChallenges = new ManageChallenges();
        componentsUtil = new ComponentsUtil();

        Bundle extras = getIntent().getExtras();
        selectedTheme = extras.getInt("tema");
        idTheme = extras.getLong("idTema", -1);
    }

    /**
     * Method responsible for instantiating the text buttons.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtVowel:
            case R.id.btnVogais:
                componentsUtil.disableTouchInWindow(this);
                manageChallenges.editWord("Vogais");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openIntent(LevelVowelActivity.class);
                    }
                }, 1300);
                break;

            case R.id.txtConsonant:
            case R.id.btnConsoantes:
                componentsUtil.disableTouchInWindow(this);
                manageChallenges.editWord("Consoantes");

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openIntent(LevelConsonantActivity.class);
                    }
                }, 1300);
                break;

            case R.id.txtAlphabet:
            case R.id.btnAlfabeto:
                componentsUtil.disableTouchInWindow(this);
                manageChallenges.editWord("Alfabeto");

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openIntent(LevelAlphabetActivity.class);
                    }
                }, 1300);
                break;

            case R.id.btnVoltarNiveis:
                onBackPressed();
                break;
        }
    }

    /**
     * Method responsible for mapping the native back button of Android, so that it closes the
     * current Activity and returns to the previous Activity.
     */
    @Override
    public void onBackPressed() {
        Intent it = new Intent(LevelsActivity.this, MainActivity.class);
        startActivity(it);
        finish();
    }

    /**
     * Method responsible for defining the ID's of the TextView's, adding click events and
     * modifying the font.
     */
    private void allocTextButtons() {
        for (int buttons : buttons) {
            TextView btn = findViewById(buttons);
            ComponentsUtil.setFont(this, btn);
            btn.setOnClickListener(this);
        }
    }

    /**
     * Method responsible for invoking a new Activity.
     *
     * @param novaActivity Activity to be opened
     */
    private void openIntent(Class novaActivity) {
        Intent it = new Intent(LevelsActivity.this, novaActivity);
        it.putExtra("tema", selectedTheme);
        it.putExtra("idTema", idTheme);
        startActivity(it);
        finish();
    }
}
