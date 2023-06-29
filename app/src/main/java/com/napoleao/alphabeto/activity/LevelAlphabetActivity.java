package com.napoleao.alphabeto.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentsUtil;
import com.napoleao.alphabeto.config.AppConfig;
import com.napoleao.alphabeto.controller.ManageChallenges;
import com.napoleao.alphabeto.controller.SelectorChallenge;
import com.napoleao.alphabeto.controller.SingletonPlayer;
import com.napoleao.alphabeto.helper.dao.ChallengesDAO;
import com.napoleao.alphabeto.model.Challenge;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for managing the alphabet challenge screen.
 */
public class LevelAlphabetActivity extends AppCompatActivity implements View.OnClickListener {
    //Components of interface
    private ImageView imageView;
    private TextView txtQuiz;
    private View buttonsAlphabetView;
    private LottieAnimationView animationView;
    int[] buttons = {R.id.btnA, R.id.btnB, R.id.btnC, R.id.btnD, R.id.btnE, R.id.btnF, R.id.btnG, R.id.btnH,
            R.id.btnI, R.id.btnJ, R.id.btnK, R.id.btnL, R.id.btnM, R.id.btnN, R.id.btnO, R.id.btnP, R.id.btnQ,
            R.id.btnR, R.id.btnS, R.id.btnT, R.id.btnU, R.id.btnV, R.id.btnW, R.id.btnX, R.id.btnY, R.id.btnZ};
    //--------------------------------------------------------------------------------------------//
    private List<Challenge> listChallenge = new ArrayList<>();
    private ManageChallenges manageChallenges;
    private ComponentsUtil componentsUtil;
    private int imageType = 0;
    private SelectorChallenge selectorChallenge = new SelectorChallenge(listChallenge);
    private static final int LEVEL_SELECTED = 2;
    private int selectedTheme;
    private Long idTheme;
    private char[] challenge;
    private int index = 0;
    private SingletonPlayer jogador = SingletonPlayer.getPlayer();

    /**
     * Method responsible for initializing the screen components.
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alfabeto);

        manageChallenges = new ManageChallenges();
        componentsUtil = new ComponentsUtil();

        // Getting the chosen theme
        Bundle extras = getIntent().getExtras();
        idTheme = extras.getLong("idTema");
        selectedTheme = extras.getInt("tema");

        //Loading themes according to choice
        if (idTheme != -1) {
            ChallengesDAO challengesDAO = ChallengesDAO.getInstance(this);
            listChallenge = challengesDAO.findById(idTheme);
            listChallenge = manageChallenges.randomListDesafios(listChallenge);
            imageType = 1;
            Log.d("DEBUG", "LIST: " + listChallenge.size());
        } else {
            selectorChallenge.choosenTheme(selectedTheme);
            listChallenge = manageChallenges.carregarTemas(listChallenge, LEVEL_SELECTED);
        }

        //Alloc the interface
        imageView = findViewById(R.id.imageAlfabeto);
        txtQuiz = findViewById(R.id.textAlfabeto);
        buttonsAlphabetView = findViewById(R.id.botoesAlfabeto);
        componentsUtil.allocAllButtons(buttonsAlphabetView, this, buttons, this);
        animationView = findViewById(R.id.animationAlfabeto);

        manageChallenges.setAtributosAlfabeto(imageView, txtQuiz, listChallenge, index, imageType, getApplicationContext());

        // Defining the first elements to be started
        txtQuiz.setText(manageChallenges.definirPalavraAlfabeto(manageChallenges.dandoEspacos(listChallenge.get(index).getWord())));
        challenge = manageChallenges.definirPalavraAlfabeto(listChallenge.get(index).getWord()).toCharArray();
    }

    /**
     * Method responsible for managing the click on the keyboard buttons.
     *
     * @param v needed for XML mapping
     */
    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String alternativa = button.getText().toString();
        validadeTheResponse(alternativa.charAt(0));
    }

    /**
     * Plays the name of the image that represents the challenge.
     *
     * @param v needed for XML mapping
     */
    public void speakImage(View v) {
        manageChallenges.falarImagem(listChallenge, index);
    }

    /**
     * Checks if the chosen character exists in the challenge.
     *
     * @param alternative Character chosen (button clicked on the keyboard).
     */
    private void validadeTheResponse(char alternative) {
        String resposta = manageChallenges.verificarAlternativa(this, alternative, listChallenge.get(index).getWord().toLowerCase(), challenge, jogador);
        txtQuiz.setText(manageChallenges.dandoEspacos(resposta));

        boolean acertou = manageChallenges.verificaResposta(listChallenge.get(index).getWord().toLowerCase(), resposta);
        if (acertou) {
            animationView.playAnimation();
            ManageChallenges.acertou(this, AppConfig.getInstance(this).getCurrentSound());
            index++;
            if (index == listChallenge.size() || index == 5) {
                componentsUtil.startIntent(this, EndOfGameActivity.class, selectedTheme, idTheme);
            } else if (index < listChallenge.size()) {
                componentsUtil.disableAllButtons(buttons, buttonsAlphabetView);
                Handler handle = new Handler();
                handle.postDelayed(() -> {
                    manageChallenges.setAtributosAlfabeto(imageView, txtQuiz, listChallenge, index, imageType, getApplicationContext());
                    challenge = manageChallenges.definirPalavraAlfabeto(listChallenge.get(index).getWord()).toCharArray();
                    //Mudando o desafio. Para isso é chamado o método que seta a quantidade de espaços que formam a palavra
                    txtQuiz.setText(manageChallenges.definirPalavraAlfabeto(manageChallenges.dandoEspacos(listChallenge.get(index).getWord())));
                    componentsUtil.enableAllButtons(buttons, buttonsAlphabetView);
                }, 2000);
            }
        }
    }

    /**
     * Maps the native Android back button, displays an AlertDialog asking if the player
     * wants to return to the theme selection menu.
     */
    @Override
    public void onBackPressed() {
        componentsUtil.showBackConfirmation(this);
    }

    /**
     * Maps the back button present in the activity.
     *
     * @param v View mapped
     */
    public void backAlfabeto(View v) {
        onBackPressed();
    }

    /**
     * Maps the button to close the activity.
     *
     * @param v View mapped
     */
    public void closeAlfabeto(View v) {
        componentsUtil.showCloseConfirmation(this);
    }

}
