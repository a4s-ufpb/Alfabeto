package com.napoleao.alphabeto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentsUtil;

/**
 * Class responsible for managing the Splash Screen screen.
 */
public class SplashScreenActivity extends AppCompatActivity {

    /**
     * Method responsible for initializing the screen components.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        super.onCreate(savedInstanceState);

        TextView txtKids = findViewById(R.id.textKids);
        ComponentsUtil.setFont(this, txtKids);

        // Inserting delay time for Splash Screen
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                showGame();
            }
        }, 2000);

    }

    /**
     * Method responsible for opening the game screen.
     */
    protected void showGame() {
        Intent it = new Intent(SplashScreenActivity.this, HomeActivity.class);
        startActivity(it);
        finish();
    }
}
