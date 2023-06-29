package com.napoleao.alphabeto.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentsUtil;
import com.napoleao.alphabeto.controller.ManageChallenges;
import com.napoleao.alphabeto.controller.SingletonPlayer;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

/**
 * Class responsible for managing the end of game screen.
 */
public class EndOfGameActivity extends AppCompatActivity implements View.OnClickListener {

    private final SingletonPlayer userPlayer = SingletonPlayer.getPlayer();
    private int select;
    private Long idTheme;
    int[] textIds = {R.id.txtPoints, R.id.txtReturnToThemes, R.id.txtRepeat, R.id.txtTextEnd};
    private ManageChallenges manageChallenges;
    private ComponentsUtil componentsUtil;

    /**
     * Method responsible for creating the screen and setting the text of the TextViews.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fim_de_jogo);

        allocAllTextButtons();

        Bundle extras = getIntent().getExtras();
        select = extras.getInt("tema");
        idTheme = extras.getLong("idTema");

        manageChallenges = new ManageChallenges();
        componentsUtil = new ComponentsUtil();

        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(this, R.raw.applause);
        mediaPlayer.start();

        TextView txt = findViewById(R.id.txtTextEnd);
        String texto;
        if (userPlayer.getPoints() <= 1) {
            texto = "Você é capaz!\nContinue tentando!";
            txt.setText(texto);
            manageChallenges.editWord(texto);
        } else if (userPlayer.getPoints() > 1 && userPlayer.getPoints() <= 2) {
            texto = "Parabéns!\nVocê foi muito bem!";
            txt.setText(texto);
            manageChallenges.editWord(texto);
        } else if (userPlayer.getPoints() > 2 && userPlayer.getPoints() <= 3) {
            texto = "Parabéns!\n Você foi demais!";
            txt.setText(texto);
            manageChallenges.editWord(texto);
        }

        final ScaleRatingBar ratingBar = findViewById(R.id.simpleRatingBar);
        // Number of stars that appear
        ratingBar.setNumStars(3);
        // Number minimum of stars that start filled
        ratingBar.setMinimumStars(0);
        // Number thats defines the size of the stars
        ratingBar.setRating(userPlayer.getPoints());
        ratingBar.setStarPadding(10);
        ratingBar.setStepSize(0.5f);
        ratingBar.setIsIndicator(false);
        ratingBar.setClickable(false);
        ratingBar.setScrollable(false);
        ratingBar.setClearRatingEnabled(false);
        ratingBar.setEmptyDrawableRes(R.drawable.estrela_vazia);
        ratingBar.setFilledDrawableRes(R.drawable.estrela_preenchida);
        ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
            }
        });
        userPlayer.resetPoint();
    }

    /**
     * Method responsible for managing the click of the buttons.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voltar_temas:
                Intent it = new Intent(EndOfGameActivity.this, MainActivity.class);
                startActivity(it);
                finish();
                break;
            case R.id.repetir:
                it = new Intent(EndOfGameActivity.this, LevelsActivity.class);
                it.putExtra("tema", select);
                it.putExtra("idTema", idTheme);
                startActivity(it);
                finish();
                break;
        }
    }

    /**
     * Define os ID's dos TextView's e define a fonte do TextView.
     */
    private void allocAllTextButtons() {
        for (int buttons : textIds) {
            TextView btn = findViewById(buttons);
            ComponentsUtil.setFont(this, btn);
        }
    }

    /**
     * Method responsible for managing the back button.
     */
    @Override
    public void onBackPressed() {
        componentsUtil.showCloseConfirmation(this);
    }
}
