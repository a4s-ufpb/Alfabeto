package com.napoleao.alphabeto.activity;

import android.os.Bundle;
import android.os.Handler;
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
 * Classe responsável por gerenciar a tela de desafios de vogais.
 */
public class LevelVowelActivity extends AppCompatActivity implements View.OnClickListener {
    //Componentes da interface
    private ImageView imageView;
    private TextView txtQuiz;
    private View buttonsView;
    private LottieAnimationView animationView;
    private int[] buttons = {R.id.btnA, R.id.btnE, R.id.btnI, R.id.btnO, R.id.btnU};
    //--------------------------------------------------------------------------------------------//
    // Array with the loaded themes;
    private List<Challenge> listChallenges = new ArrayList<>();
    //Class responsible for the application logic;
    private ManageChallenges manageChallenges = new ManageChallenges();
    private ComponentsUtil componentsUtil;
    private int imageType = 0;
    // Class responsible for instantiating the challenges of the chosen theme;
    private SelectorChallenge selectorChallenge = new SelectorChallenge(listChallenges);
    // Variable that indicates the level for selecting the challenges;
    private static final int LEVEL_SELECTED = 0;
    // Variable that indicates the theme to be loaded;
    private int selectedTheme;
    private Long idTheme;
    // Array responsible for storing and updating the challenge according to the answers;
    private char[] challenge;
    // Variable that indicates the current index in the list of themes;
    private int index = 0;
    // Variable used to define the player's score.
    private SingletonPlayer jogador = SingletonPlayer.getPlayer();

    /**
     * Method responsible for creating the vowel challenge screen.
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vogais);

        manageChallenges = new ManageChallenges();
        componentsUtil = new ComponentsUtil();

        // Getting the chosen theme
        Bundle extras = getIntent().getExtras();
        idTheme = extras.getLong("idTema");
        selectedTheme = extras.getInt("tema");

        // Loading themes according to choice
        if (idTheme != -1) {
            ChallengesDAO challengesDAO = ChallengesDAO.getInstance(this);
            listChallenges = challengesDAO.findById(idTheme);
            listChallenges = manageChallenges.randomListDesafios(listChallenges);
            imageType = 1;
            //Log.d("DEBUG", "LIST: " + listChallenges.size());
        } else {
            selectorChallenge.choosenTheme(selectedTheme);
            listChallenges = manageChallenges.carregarTemas(listChallenges, LEVEL_SELECTED);
        }

        //Alloc the interface
        imageView = findViewById(R.id.imageVogal);
        txtQuiz = findViewById(R.id.txtQuiz);
        buttonsView = findViewById(R.id.botoesVogais);
        componentsUtil.allocAllButtons(buttonsView, this, buttons, this);
        animationView = findViewById(R.id.animationVogais);

        manageChallenges.setAtributosVogais(imageView, txtQuiz, listChallenges, index, imageType, this);

        // Defining the first elements to be started
        txtQuiz.setText(manageChallenges.dandoEspacos(manageChallenges.definirPalavraVogal(listChallenges.get(index).getWord())));
        challenge = manageChallenges.definirPalavraVogal(listChallenges.get(index).getWord()).toCharArray();
    }

    /**
     * Method responsible for defining the action of the buttons.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String alternativa = button.getText().toString();
        validadeTheResponse(alternativa.charAt(0));
    }

    /**
     * Play the name of the image that represents the challenge.
     *
     * @param v
     */
    public void speakImage(View v) {
        manageChallenges.falarImagem(listChallenges, index);
    }

    /**
     * Method responsible for verifying the answer given by the player.
     *
     * @param alternative
     */
    private void validadeTheResponse(char alternative) {
        String resposta = manageChallenges.verificarAlternativa(this, alternative, listChallenges.get(index).getWord().toLowerCase(), challenge, jogador);
        txtQuiz.setText(manageChallenges.dandoEspacos(resposta));

        boolean acertou = manageChallenges.verificaResposta(listChallenges.get(index).getWord().toLowerCase(), resposta);
        if (acertou) {
            animationView.playAnimation();
            ManageChallenges.acertou(this, AppConfig.getInstance(this).getCurrentSound());
            index++;
            if (index == listChallenges.size() || index == 5) {
                componentsUtil.startIntent(this, EndOfGameActivity.class, selectedTheme, idTheme);
            } else if (index < listChallenges.size()) {
                componentsUtil.disableAllButtons(buttons, buttonsView);
                Handler handle = new Handler();
                handle.postDelayed(() -> {
                    manageChallenges.setAtributosVogais(imageView, txtQuiz, listChallenges, index, imageType, getApplicationContext());
                    challenge = manageChallenges.definirPalavraVogal(listChallenges.get(index).getWord()).toCharArray();
                    // Change the challenge. For this the method that sets the number of spaces that form the word is called
                    txtQuiz.setText(manageChallenges.dandoEspacos(manageChallenges.definirPalavraVogal(listChallenges.get(index).getWord())));
                    componentsUtil.enableAllButtons(buttons, buttonsView);
                }, 2000);
            }
        }
    }

    /**
     * Map the native Android back button, display an AlertDialog asking if the player
     */
    @Override
    public void onBackPressed() {
        componentsUtil.showBackConfirmation(this);
    }

    /**
     * Map the back button present.
     *
     * @param v View mapped
     */
    public void backVowel(View v) {
        onBackPressed();
    }

    /**
     * Map the close button present.
     *
     * @param v View mapped
     */
    public void closeVowel(View v) {
        componentsUtil.showCloseConfirmation(this);
    }
}

