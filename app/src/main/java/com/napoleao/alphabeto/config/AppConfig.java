package com.napoleao.alphabeto.config;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Class responsible for managing the application settings.
 */
public class AppConfig {
    private static AppConfig instance;
    SharedPreferences sPreferences = null;

    public final static String CURSIVA = "cursiva";
    public final static String BASTAO = "bastão";
    public final static String UPPER = "maiúsculas";
    public final static String LOWER = "minúsculas";
    public final static String OPCAO01 = "Opção 01";
    public final static String OPCAO02 = "Opção 02";
    public final static String OPCAO03 = "Opção 03";
    private String currentLetterType;
    private String currentLetterCase;
    private String currentSound;
    private String currentButtonConfig;
    private boolean currentButtonCase;

    /**
     * Constructor responsible for initializing the application settings.
     *
     * @param appContext
     */
    private AppConfig(Context appContext) {
        try {
            if (sPreferences == null) {
                sPreferences = appContext.getSharedPreferences("configs", MODE_PRIVATE);
            }
            this.currentLetterType = sPreferences.getString("letter_type", BASTAO);
            this.currentLetterCase = sPreferences.getString("letter_case", UPPER);
            this.currentSound = sPreferences.getString("hit_sound", OPCAO03);
            this.currentButtonConfig = sPreferences.getString("button_font_preferences", "fonts/bastão.ttf");
            this.currentButtonCase = sPreferences.getBoolean("button_case_preferences", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method responsible for returning the instance of the class.
     *
     * @param appContext
     * @return
     */
    public static AppConfig getInstance(Context appContext) {
        if (instance == null) {
            instance = new AppConfig(appContext);
        }
        return instance;
    }

    public boolean isCurrentButtonCase() {
        return currentButtonCase;
    }

    public void setCurrentButtonCase(boolean currentButtonCase) {
        this.currentButtonCase = currentButtonCase;
    }

    public String getCurrentLetterType() {
        return currentLetterType;
    }

    public void setCurrentLetterType(String currentLetterType) {
        this.currentLetterType = currentLetterType.toLowerCase();
    }

    public String getCurrentLetterCase() {
        return currentLetterCase;
    }

    public void setCurrentLetterCase(String currentLetterCase) {
        this.currentLetterCase = currentLetterCase.toLowerCase();
    }

    public String getCurrentSound() {
        return currentSound;
    }

    public void setCurrentSound(String currentSound) {
        this.currentSound = currentSound;
    }

    public String getCurrentButtonConfig() {
        return currentButtonConfig;
    }

    public void setCurrentButtonConfig(String currentButtonConfig) {
        this.currentButtonConfig = currentButtonConfig;
    }

    /**
     * Method responsible for saving the changes made in the application settings.
     */
    public void saveAllChange() {
        try {
            SharedPreferences.Editor edit = sPreferences.edit();
            edit.putString("letter_type", this.currentLetterType);
            edit.putString("letter_case", this.currentLetterCase);
            edit.putString("hit_sound", this.currentSound);
            edit.putString("button_font_preferences", this.currentButtonConfig);
            edit.putBoolean("button_case_preferences", this.currentButtonCase);
            edit.apply();
            //Log.i("AppConfig", "All changes have been saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
