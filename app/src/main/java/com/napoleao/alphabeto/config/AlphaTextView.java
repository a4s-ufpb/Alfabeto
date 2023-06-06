package com.napoleao.alphabeto.config;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Classe responsável por gerenciar a fonte do aplicativo.
 */
public class AlphaTextView extends AppCompatTextView {
    private Context context;
    private String fontFamily;
    private String fontCase;


    public AlphaTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public AlphaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public AlphaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        this.fontFamily = AppConfig.getInstance(this.context).getCurrentLetterType();
        this.fontCase = AppConfig.getInstance(this.context).getCurrentLetterCase();
        Log.i("AlphaTextView","fontFamily charged: "+this.fontFamily);
        Log.i("AlphaTextView","fontCase charged: "+this.fontCase);

        switch (this.fontFamily) {
            case AppConfig.CURSIVA: {
                final String cursivaFontPath = "fonts/cursiva.ttf";
                Typeface tf = Typeface.createFromAsset(this.context.getAssets(), cursivaFontPath);
                this.setTypeface(tf);
                break;
            }

            case AppConfig.BASTAO: {
                final String bastaoFontPath = "fonts/bastão.ttf";
                Typeface tf = Typeface.createFromAsset(this.context.getAssets(), bastaoFontPath);
                this.setTypeface(tf);
                break;
            }

        }

        switch (this.fontCase){
            case AppConfig.UPPER:{
                this.setAllCaps(true);
                break;
            }

            case AppConfig.LOWER:{
                this.setAllCaps(false);
                break;
            }
        }
    }
}