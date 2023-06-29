package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.napoleao.alphabeto.R;

/**
 * Class responsible for managing the theme import options screen.
 */
public class ImportOptionsActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Method responsible for initializing the screen components and listeners.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_options);
    }

    /**
     * Method responsible for calling the screen of the selected option.
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreate:
                callIntent(CreateThemeActivity.class);
                break;
            case R.id.btnImportByMail:
                callIntent(ImportByMailActivity.class);
                break;
            case R.id.btnImportById:
                callIntent(ImportThemeByIdActivity.class);
                break;
        }
    }

    /**
     * Method responsible for calling the screen of the selected option.
     *
     * @param activity
     */
    private void callIntent(Class activity) {
        Intent it = new Intent(ImportOptionsActivity.this, activity);
        startActivity(it);
        finish();
    }

    /**
     * Action button for returning to the previous screen.
     *
     * @param view
     */
    public void backAction(View view) {
        onBackPressed();
    }

    /**
     * Override Method responsible for returning to the previous screen.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callIntent(MainActivity.class);
    }
}