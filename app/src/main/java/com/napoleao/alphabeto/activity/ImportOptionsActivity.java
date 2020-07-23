package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.napoleao.alphabeto.R;

public class ImportOptionsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_options);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnImportByMail:
                invocarIntent(ImportByMailActivity.class);
                break;
            case R.id.btnImportById:
                invocarIntent(ImportTemasByIdActivity.class);
                break;
        }
    }

    private void invocarIntent(Class activity){
        Intent it = new Intent(ImportOptionsActivity.this, activity);
        startActivity(it);
        finish();
    }

    public void voltar(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        invocarIntent(MainActivity.class);
    }
}