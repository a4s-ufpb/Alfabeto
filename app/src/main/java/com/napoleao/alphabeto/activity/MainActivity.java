package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ComponentesAuxiliares;
import com.napoleao.alphabeto.adapter.TemasAdapter;
import com.napoleao.alphabeto.controller.GerenteDeDesafios;
import com.napoleao.alphabeto.controller.RecyclerItemClickListener;
import com.napoleao.alphabeto.helper.dao.TemasDAO;
import com.napoleao.alphabeto.model.Tema;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int TEMA_CORES = 0;
    private static final int TEMA_OBJETOS = 1;
    private static final int TEMA_ANIMAIS = 2;
    private static final int TEMA_FRUTAS = 3;
    private static final int TEMA_BRINQUEDOS = 4;
    private static final int TEMA_PARTES_DO_CORPO = 5;
    private static final int TEMA_PAISES = 6;

    private int temaSelecionado;
    int[] botoes = {R.id.txtTemas, R.id.txtCores, R.id.txtObjetos, R.id.txtFrutas, R.id.txtAnimais,
            R.id.txtBrinquedos, R.id.txtPartesDoCorpo, R.id.txtPaises};
    private GerenteDeDesafios gerenteDeDesafios;
    private ComponentesAuxiliares componentesAuxiliares;
    private RecyclerView recyclerView;
    private List<Tema> temasImportados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instanciarTextButtons();

        gerenteDeDesafios = new GerenteDeDesafios();
        componentesAuxiliares = new ComponentesAuxiliares();
        recyclerView = findViewById(R.id.recyclerTemas);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        componentesAuxiliares.impedirDuploClique(MainActivity.this);
                        gerenteDeDesafios.ditarPalavra(temasImportados.get(position).getNomeImagem());
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(MainActivity.this, NiveisActivity.class);
                            intent.putExtra("idTema", temasImportados.get(position).getId());
                            startActivity(intent);
                            finish();
                        },1000);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Tema temaSelecionado = temasImportados.get(position);

                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
                        dialog.setTitle("Remover");
                        dialog.setMessage("Você deseja remover o tema " + temaSelecionado.getNomeImagem() + "?");
                        dialog.setPositiveButton("Sim", (dialog1, which) -> {
                            TemasDAO temasDAO = new TemasDAO(getApplicationContext());
                            if (temasDAO.delete(temaSelecionado)){
                                configureRecyclerView();
                                Toast.makeText(getApplicationContext(), "Tema removido com sucesso!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Erro ao remover tema!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        dialog.setNegativeButton("Não", null);

                        dialog.create();
                        dialog.show();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                })
        );

        configureRecyclerView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtCores:
            case R.id.btnCores:
                componentesAuxiliares.impedirDuploClique(this);
                gerenteDeDesafios.ditarPalavra("Cores");
                temaSelecionado = TEMA_CORES;
                invocarIntent();
                break;
            case R.id.txtObjetos:
            case R.id.btnObjetos:
                componentesAuxiliares.impedirDuploClique(this);
                gerenteDeDesafios.ditarPalavra("Objetos");
                temaSelecionado = TEMA_OBJETOS;
                invocarIntent();
                break;
            case R.id.txtAnimais:
            case R.id.btnAnimais:
                componentesAuxiliares.impedirDuploClique(this);
                gerenteDeDesafios.ditarPalavra("Animais");
                temaSelecionado = TEMA_ANIMAIS;
                invocarIntent();
                break;
            case R.id.txtFrutas:
            case R.id.btnFrutas:
                componentesAuxiliares.impedirDuploClique(this);
                gerenteDeDesafios.ditarPalavra("Frutas");
                temaSelecionado = TEMA_FRUTAS;
                invocarIntent();
                break;
            case R.id.txtPaises:
            case R.id.btnPaises:
                componentesAuxiliares.impedirDuploClique(this);
                gerenteDeDesafios.ditarPalavra("Países");
                temaSelecionado = TEMA_PAISES;
                invocarIntent();
                break;
            case R.id.txtBrinquedos:
            case R.id.btnBrinquedos:
                componentesAuxiliares.impedirDuploClique(this);
                gerenteDeDesafios.ditarPalavra("Brinquedos");
                temaSelecionado = TEMA_BRINQUEDOS;
                invocarIntent();
                break;
            case R.id.txtPartesDoCorpo:
            case R.id.btnPartesDoCorpo:
                componentesAuxiliares.impedirDuploClique(this);
                gerenteDeDesafios.ditarPalavra("Partes do Corpo");
                temaSelecionado = TEMA_PARTES_DO_CORPO;
                invocarIntent();
                break;
            case R.id.btnImport:
                Intent it = new Intent(MainActivity.this, ImportTemasActivity.class);
                startActivity(it);
                finish();
            case R.id.btnMenuInicial:
                onBackPressed();
        }
    }

    private void recuperarTemasSalvos(){
        TemasDAO temasDAO = new TemasDAO(getApplicationContext());
        temasImportados = temasDAO.findAll();
    }

    /**
     * Configura o RecyclerView.
     */
    private void configureRecyclerView(){
        recuperarTemasSalvos();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        TemasAdapter temasAdapter = new TemasAdapter(MainActivity.this, temasImportados);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(temasAdapter);
    }

    /**
     * Abre a Activity de Níveis passando o tema selecionado.
     */
    private void invocarIntent(){
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            Intent it = new Intent(MainActivity.this, NiveisActivity.class);
            it.putExtra("tema", temaSelecionado);
            startActivity(it);
            finish();
        }, 1300);
    }

    /**
     * Define os ID's dos TextView's e define a fonte do TextView.
     */
    private void instanciarTextButtons(){
        for (int buttons : botoes) {
            TextView btn = findViewById(buttons);
            ComponentesAuxiliares.definirFonte(this, btn);
        }
    }

    /**
     * Mapeia o botão de voltar nativo do Android, para que feche a Activity atual e retorne à
     * Activity anterior.
     */
    @Override
    public void onBackPressed() {
        finish();
    }

}
