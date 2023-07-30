package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentsUtil;

/**
 * Class responsible for managing the screen of information about the application.
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Initializes the screen components.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        TextView txtSobre, txtDevby, txtProjeto, txtCreditos;
        txtSobre = findViewById(R.id.txtSobre);
        txtDevby = findViewById(R.id.txtDev);
        txtProjeto = findViewById(R.id.txtProjeto);
        txtCreditos = findViewById(R.id.txtCredit);

        ComponentsUtil.setFont(this, txtSobre);
        ComponentsUtil.setFont(this, txtDevby);
        ComponentsUtil.setFont(this, txtProjeto);
        ComponentsUtil.setFont(this, txtCreditos);
    }

    /**
     * Button click event.
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnMenuInicialSobre:
                finish();
                break;
            case R.id.btnPixabay:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pixabay.com/pt/"));
                startActivity(intent);
                break;
        }
    }
}
