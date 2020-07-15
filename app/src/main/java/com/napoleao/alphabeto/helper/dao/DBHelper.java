package com.napoleao.alphabeto.helper.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "ALFABETO_DB";
    public static String TABLE_TEMAS = "temas";
    public static String TABLE_DESAFIOS = "desafios";
    public static int VERSION = 1;

    public DBHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlTemas = "CREATE TABLE IF NOT EXISTS " + TABLE_TEMAS +
                " (idTema LONG PRIMARY KEY, nomeImagem TEXT," +
                " imagemUrl TEXT, UNIQUE(idTema, nomeImagem))";

        String sqlDesafios = "CREATE TABLE IF NOT EXISTS " + TABLE_DESAFIOS +
                " (idDesafio LONG PRIMARY KEY, word TEXT," +
                " imageUrl TEXT, idTema LONG, FOREIGN KEY (idTema) REFERENCES " + TABLE_TEMAS + "(idTema))";

        try {
            sqLiteDatabase.execSQL(sqlTemas);
            sqLiteDatabase.execSQL(sqlDesafios);
            Log.i("INFO DB", "Sucesso ao criar tabela");
        }catch (Exception e){
            Log.i("INFO DB", "Erro ao criar tabela " + e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
