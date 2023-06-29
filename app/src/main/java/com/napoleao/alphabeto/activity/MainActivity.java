package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentsUtil;
import com.napoleao.alphabeto.activity.util.ImageLoadUtil;
import com.napoleao.alphabeto.adapter.ThemeAdapter;
import com.napoleao.alphabeto.controller.ManageChallenges;
import com.napoleao.alphabeto.controller.RecyclerItemClickListener;
import com.napoleao.alphabeto.helper.ApiConfig;
import com.napoleao.alphabeto.helper.dao.TemasDAO;
import com.napoleao.alphabeto.model.Tema;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for managing the home main theme selection screen.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int THEME_COLORS = 0;
    private static final int THEME_OBJECTS = 1;
    private static final int THEME_ANIMALS = 2;
    private static final int THEME_FRUITS = 3;
    private static final int THEME_TOYS = 4;
    private static final int THEME_HUMAN_BODY_PARTS = 5;
    private static final int THEME_COUNTRIES = 6;

    private int selectedTheme;
    int[] buttons = {R.id.txtThemes, R.id.txtColors, R.id.txtObjects, R.id.txtFruits, R.id.txtAnimals,
            R.id.txtToys, R.id.txtPartsBodyHuman, R.id.txtCountries};
    private ManageChallenges manageChallenges;
    private ComponentsUtil componentsUtil;
    private RecyclerView recyclerView;
    private List<Tema> themeImporteds = new ArrayList<>();

    /**
     * Method responsible for initializing the screen components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allocTextButtons();

        // Init ApiConfig
        ApiConfig.getInstance(getApplicationContext());

        manageChallenges = new ManageChallenges();
        componentsUtil = new ComponentsUtil();
        recyclerView = findViewById(R.id.recyclerTemas);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        componentsUtil.disableTouchInWindow(MainActivity.this);
                        manageChallenges.editWord(themeImporteds.get(position).getNomeImagem());
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(MainActivity.this, LevelsActivity.class);
                            intent.putExtra("idTema", themeImporteds.get(position).getId());
                            startActivity(intent);
                            finish();
                        }, 1000);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Tema themeSelect = themeImporteds.get(position);

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertDialog.show();
                        Window win = alertDialog.getWindow();
                        win.setContentView(R.layout.alerta_tema_options);

                        win.setBackgroundDrawableResource(R.drawable.background_gradient);

                        ImageView imageTemaView = (ImageView) win.findViewById(R.id.imgTema);
                        ImageLoadUtil.getInstance().loadImage(themeSelect.getImageUrl(), imageTemaView, getApplicationContext());

                        TextView nomeTemaView = (TextView) win.findViewById(R.id.nomeTema);
                        nomeTemaView.setText(themeSelect.getNomeImagem());

                        View edit_btn = (View) win.findViewById(R.id.areaEdit);
                        edit_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editSelectedTheme(themeSelect);
                            }
                        });

                        View del_btn = (View) win.findViewById(R.id.areaDel);
                        del_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteSelectedTheme(themeSelect, alertDialog);
                            }
                        });

                        View close_btn = (View) win.findViewById(R.id.areaClose);
                        close_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                    }

                    private void deleteSelectedTheme(Tema temaSelecionado, AlertDialog alertDialog) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
                        dialog.setTitle("Remover");
                        dialog.setMessage("Você deseja remover o tema " + temaSelecionado.getNomeImagem() + "?");
                        dialog.setPositiveButton("Sim", (dialog1, which) -> {
                            TemasDAO temasDAO = TemasDAO.getInstance(getApplicationContext());
                            if (temasDAO.delete(temaSelecionado)) {
                                alertDialog.dismiss();
                                configureRecyclerView();
                                Toast.makeText(getApplicationContext(), "Tema removido com sucesso!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Erro ao remover tema!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        dialog.setNegativeButton("Não", null);

                        dialog.create();
                        dialog.show();
                    }

                    private void editSelectedTheme(Tema editTema) {
                        Intent intent = new Intent(getApplicationContext(), CreateThemeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("editMode", true);
                        intent.putExtra("themeID", editTema.getId());
                        getApplicationContext().startActivity(intent);
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                })
        );

        configureRecyclerView();
    }

    /**
     * Method responsible for configuring the RecyclerView.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtColors:
            case R.id.btnCores:
                componentsUtil.disableTouchInWindow(this);
                manageChallenges.editWord("Cores");
                selectedTheme = THEME_COLORS;
                openIntent();
                break;
            case R.id.txtObjects:
            case R.id.btnObjetos:
                componentsUtil.disableTouchInWindow(this);
                manageChallenges.editWord("Objetos");
                selectedTheme = THEME_OBJECTS;
                openIntent();
                break;
            case R.id.txtAnimals:
            case R.id.btnAnimais:
                componentsUtil.disableTouchInWindow(this);
                manageChallenges.editWord("Animais");
                selectedTheme = THEME_ANIMALS;
                openIntent();
                break;
            case R.id.txtFruits:
            case R.id.btnFrutas:
                componentsUtil.disableTouchInWindow(this);
                manageChallenges.editWord("Frutas");
                selectedTheme = THEME_FRUITS;
                openIntent();
                break;
            case R.id.txtCountries:
            case R.id.btnPaises:
                componentsUtil.disableTouchInWindow(this);
                manageChallenges.editWord("Países");
                selectedTheme = THEME_COUNTRIES;
                openIntent();
                break;
            case R.id.txtToys:
            case R.id.btnBrinquedos:
                componentsUtil.disableTouchInWindow(this);
                manageChallenges.editWord("Brinquedos");
                selectedTheme = THEME_TOYS;
                openIntent();
                break;
            case R.id.txtPartsBodyHuman:
            case R.id.btnPartesDoCorpo:
                componentsUtil.disableTouchInWindow(this);
                manageChallenges.editWord("Partes do Corpo");
                selectedTheme = THEME_HUMAN_BODY_PARTS;
                openIntent();
                break;
            case R.id.btnImport:
                Intent it = new Intent(MainActivity.this, ImportOptionsActivity.class);
                startActivity(it);
                finish();
            case R.id.btnMenuInicial:
                onBackPressed();
        }
    }

    /**
     * Recorvey the themes saved in the database.
     */
    private void recoverSavedTheme() {
        TemasDAO temasDAO = new TemasDAO(getApplicationContext());
        themeImporteds = temasDAO.findAll();
    }

    /**
     * Configure o RecyclerView.
     */
    private void configureRecyclerView() {
        recoverSavedTheme();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        ThemeAdapter themeAdapter = new ThemeAdapter(MainActivity.this, themeImporteds);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(themeAdapter);
    }

    /**
     * Open the LevelsActivity passing the selected theme.
     */
    private void openIntent() {
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            Intent it = new Intent(MainActivity.this, LevelsActivity.class);
            it.putExtra("tema", selectedTheme);
            startActivity(it);
            finish();
        }, 1300);
    }

    /**
     * Define of the TextView's ID's and define the font of the TextView.
     */
    private void allocTextButtons() {
        for (int buttons : buttons) {
            TextView btn = findViewById(buttons);
            ComponentsUtil.setFont(this, btn);
        }
    }

    /**
     * Map the native back button of Android, to close the current Activity and return to the
     * previous Activity.
     */
    @Override
    public void onBackPressed() {
        finish();
    }

}
