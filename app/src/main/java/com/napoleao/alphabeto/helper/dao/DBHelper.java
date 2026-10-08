package com.napoleao.alphabeto.helper.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class to manage database
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;
    public static String DB_NAME = "ALFABETO_DB_v1";
    public static String TABLE_TEMAS = "temas";
    public static String TABLE_DESAFIOS = "desafios";
    public static int VERSION = 2;

    /**
     * Get shared instance
     *
     * @param context
     * @return
     */
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    /**
     * Constructor for DBHelper.
     *
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * Create database
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlTemas = "CREATE TABLE IF NOT EXISTS " + TABLE_TEMAS +
                " (idTema INTEGER PRIMARY KEY AUTOINCREMENT, nomeImagem TEXT," +
                " imagemUrl TEXT, remoteId INTEGER)";

        String sqlDesafios = "CREATE TABLE IF NOT EXISTS " + TABLE_DESAFIOS +
                " (idDesafio INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT," +
                " imageUrl TEXT, idTema INTEGER, remoteId INTEGER, FOREIGN KEY (idTema) REFERENCES " + TABLE_TEMAS + "(idTema))";

        try {
            sqLiteDatabase.execSQL(sqlTemas);
            sqLiteDatabase.execSQL(sqlDesafios);
            //Log.i("INFO DB", "Sucesso ao criar tabela");
        } catch (Exception e) {
            //Log.i("INFO DB", "Erro ao criar tabela " + e.getMessage());
        }

    }

    /**
     * Upgrade database
     *
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // remoteId = id do registro no EducAPI (null para temas/desafios criados localmente)
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_TEMAS + " ADD COLUMN remoteId INTEGER");
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_DESAFIOS + " ADD COLUMN remoteId INTEGER");
        }
    }

    /**
     * Reads a nullable Long column from the cursor.
     */
    public static Long getLongOrNull(android.database.Cursor cursor, String column) {
        int idx = cursor.getColumnIndex(column);
        if (idx < 0 || cursor.isNull(idx)) {
            return null;
        }
        return cursor.getLong(idx);
    }
}
