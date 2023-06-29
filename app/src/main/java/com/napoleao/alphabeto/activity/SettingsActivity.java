package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentsUtil;
import com.napoleao.alphabeto.config.AppConfig;

/**
 * Class responsible for managing the application settings screen.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private AppConfig configurator;
    private RadioGroup rgLetterType;
    private RadioButton rbCursiva;
    private RadioButton rbBastao;
    private RadioGroup rgLetterCase;
    private RadioButton rbUpper;
    private RadioButton rbLower;
    private RadioGroup rgSound;
    private RadioButton rbOption1;
    private RadioButton rbOption2;
    private RadioButton rbOption3;
    int[] textIds = {R.id.txtConfig, R.id.txtTypeLetter, R.id.txtTextAreaView, R.id.txtSoundCorrect,
            R.id.txtBack, R.id.txtSave};

    /**
     * Method responsible for initializing the screen components.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        allocTextButtons();

        this.configurator = AppConfig.getInstance(getApplicationContext());

        this.rgLetterType = findViewById(R.id.rgLetterType);
        this.rgLetterCase = findViewById(R.id.rgLetterCase);
        this.rgSound = findViewById(R.id.rgSound);
        this.rbCursiva = findViewById(R.id.rdButtonCursiva);
        this.rbBastao = findViewById(R.id.rdButtonBastao);
        this.rbUpper = findViewById(R.id.rdButtonMaiusculas);
        this.rbLower = findViewById(R.id.rdButtonMinusculas);
        this.rbOption1 = findViewById(R.id.rbOpcao1);
        this.rbOption2 = findViewById(R.id.rbOpcao2);
        this.rbOption3 = findViewById(R.id.rbOpcao3);
    }

    /**
     * Method responsible for instantiating the load configs button.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadConfigsInView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Changes the settings in AppConfig according to the options selected on the settings screen.
     */
    private void pushChanges() {
        String rgSelectedLetterType = ((RadioButton) findViewById(this.rgLetterType.getCheckedRadioButtonId())).getText().toString();
        String rgSelectedLetterCase = ((RadioButton) findViewById(this.rgLetterCase.getCheckedRadioButtonId())).getText().toString();
        String rgSelectedSound = ((RadioButton) findViewById(this.rgSound.getCheckedRadioButtonId())).getText().toString();
        this.configurator.setCurrentLetterType(rgSelectedLetterType);
        this.configurator.setCurrentLetterCase(rgSelectedLetterCase);
        this.configurator.setCurrentSound(rgSelectedSound);

        if (rgSelectedLetterType.equals("Cursiva")) {
            this.configurator.setCurrentButtonConfig("fonts/cursiva.ttf");
        } else {
            this.configurator.setCurrentButtonConfig("fonts/bastão.ttf");
        }

        if (rgSelectedLetterCase.equals("Maiúsculas")) {
            this.configurator.setCurrentButtonCase(true);
        } else {
            this.configurator.setCurrentButtonCase(false);
        }

        Log.d("Button Config", this.configurator.getCurrentButtonConfig());
    }

    /**
     * Marks the RadioButton options according to the AppConfig settings.
     */
    private void loadConfigsInView() {
        //Log.i("Json-Config","Entrou em LoadConfigs");
        //Log.i("Json-Config","CurrentLetterType: " + this.configurator.getCurrentLetterType());
        //Log.i("Json-Config","CurrentLetterCase: " + this.configurator.getCurrentLetterCase());
        //Log.i("Json-Config","CurrentSound: " + this.configurator.getCurrentSound());
        //Log.i("Json-Config","CurrentButtonConfig: " + this.configurator.getCurrentButtonConfig());
        //Log.i("Json-Config","CurrentButtonCase: " + this.configurator.isCurrentButtonCase());
        switch (this.configurator.getCurrentLetterType()) {
            case (AppConfig.CURSIVA):
                rgLetterType.check(rbCursiva.getId());
                break;

            case (AppConfig.BASTAO):
                rgLetterType.check(rbBastao.getId());
                break;
        }

        switch (this.configurator.getCurrentLetterCase()) {
            case (AppConfig.UPPER):
                rgLetterCase.check(rbUpper.getId());
                break;

            case (AppConfig.LOWER):
                rgLetterCase.check(rbLower.getId());
                break;
        }

        switch (this.configurator.getCurrentSound()) {
            case (AppConfig.OPCAO01):
                rgSound.check(rbOption1.getId());
                break;
            case (AppConfig.OPCAO02):
                rgSound.check(rbOption2.getId());
                break;
            case (AppConfig.OPCAO03):
                rgSound.check(rbOption3.getId());
                break;
        }
    }

    /**
     * Saves the current settings of the settings screen and reloads the screen.
     *
     * @param view
     */
    public void saveChanges(View view) {
        pushChanges();
        this.configurator.saveAllChange();
        this.recreate();
        Toast.makeText(this, "Configurações salvas com sucesso!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Define the correct sound according to the selected option.
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        MediaPlayer acertou = null;
        switch (view.getId()) {
            case R.id.rbOpcao1:
                acertou = MediaPlayer.create(this, R.raw.acertou);
                break;
            case R.id.rbOpcao2:
                acertou = MediaPlayer.create(this, R.raw.acertou2);
                break;
            case R.id.rbOpcao3:
                acertou = MediaPlayer.create(this, R.raw.acertou3);
                break;
        }
        acertou.start();
    }

    /**
     * Back to the menu screen.
     *
     * @param v
     */
    public void backSettings(View v) {
        onBackPressed();
    }

    /**
     * Define the ID's of the TextView's and defines the font of the TextView.
     */
    private void allocTextButtons() {
        for (int buttons : textIds) {
            TextView btn = findViewById(buttons);
            ComponentsUtil.setFont(this, btn);
        }
    }

    /**
     * Returns to the menu screen.
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
