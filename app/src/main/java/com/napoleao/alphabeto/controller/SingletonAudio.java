package com.napoleao.alphabeto.controller;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Class responsible for managing the audio service.
 */
public class SingletonAudio implements TextToSpeech.OnInitListener {

    private Context myContext;
    private static SingletonAudio singleton;
    private TextToSpeech tts;

    /**
     * Constructor of the class.
     *
     * @param context
     */
    private SingletonAudio(Context context) {
        myContext = context;
        tts = new TextToSpeech(context, this, "com.google.android.tts");
    }

    /**
     * Method responsible for returning the instance of the class.
     *
     * @param context
     * @return
     */
    public static synchronized SingletonAudio getSingleton(Context context) {
        if (singleton == null) {
            singleton = new SingletonAudio(context);
        }

        return singleton;
    }

    /**
     * Method responsible for returning the instance of the class.
     *
     * @return
     */
    public static synchronized SingletonAudio getSingleton() {
        if (singleton == null) {
            throw new RuntimeException("Não há instância do serviço de áudio! Alguma activity deve" +
                    " chamar o SingletonAudio inicialmente");
        } else {
            return singleton;
        }
    }

    /**
     * Method responsible for dictating the word.
     *
     * @param word
     */
    public void speakWord(String word) {
        tts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());
            //Log.d("AUDIO", "Serviço de áudio disponível");
        } else {
            //Log.d("AUDIO", "Serviço de áudio disponível");
        }
    }

    /**
     * Method responsible for stopping the audio service.
     */
    public void stopTts() {
        tts.stop();
        tts.shutdown();
        singleton = null;
    }
}
