package com.napoleao.alphabeto.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentsUtil;
import com.napoleao.alphabeto.controller.SingletonAudio;

/**
 * Class responsible for managing the initial screen of the application.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    int[] buttons = {R.id.btnHome, R.id.btnConfig, R.id.btnAbout};
    private SingletonAudio tts;

    /**
     * Method responsible for creating the initial screen of the application.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_window_home);
        super.onCreate(savedInstanceState);

        TextView txtKidsHome = findViewById(R.id.txtKidsHome);
        TextView textUserPlayer = findViewById(R.id.textUserPlayer);
        TextView txtConfig = findViewById(R.id.textConfig);
        TextView textAbout = findViewById(R.id.textAbout);

        ComponentsUtil.setFont(this, txtKidsHome);
        ComponentsUtil.setFont(this, textUserPlayer);
        ComponentsUtil.setFont(this, txtConfig);
        ComponentsUtil.setFont(this, textAbout);

        tts = SingletonAudio.getSingleton(this);

        allocButtons();

    }

    /**
     * Method responsible for managing the click events of the buttons on the initial screen.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHome:
                Intent it = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(it);
                break;
            case R.id.btnAbout:
                it = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(it);
                break;
            case R.id.btnConfig:
                it = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(it);
                break;
        }
    }

    /**
     * Method responsible for instantiating the buttons on the initial screen.
     */
    public void allocButtons() {
        int i;
        for (i = 0; i < buttons.length; i++) {
            ImageButton btn = findViewById(buttons[i]);
            btn.setOnClickListener(this);
        }
    }

    /**
     * Method responsible for managing the click event of the back button on the device.
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        mensagem.setTitle("Confirmação");
        mensagem.setIcon(null);
        mensagem.setMessage("Deseja sair do jogo?");

        mensagem.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast = Toast.makeText(HomeActivity.this, "Até a próxima!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                tts.stopTts();
                finish();
            }
        });

        mensagem.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast = Toast.makeText(HomeActivity.this, "Continuando...", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        });
        mensagem.show();
    }

}
