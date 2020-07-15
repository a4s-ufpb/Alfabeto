package com.napoleao.alphabeto.helper.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.napoleao.alphabeto.model.Tema;

import java.util.ArrayList;
import java.util.List;

public class TemasDAO implements ITemasDAO {

    private SQLiteDatabase write;
    private SQLiteDatabase read;
    private Context context;

    public TemasDAO(Context context){
        this.context = context;
        DBHelper dbHelper = new DBHelper(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }


    @Override
    public boolean save(Tema tema) {
        ContentValues cv = new ContentValues();
        cv.put("idTema", tema.getId());
        cv.put("nomeImagem", tema.getNomeImagem());
        cv.put("imagemUrl", tema.getImageUrl());
        try {
            write.insertOrThrow(DBHelper.TABLE_TEMAS, null, cv);
            Log.d("SAVE", "Tema salvo com sucesso!");
        }catch (Exception e){
            Log.d("SAVE", "Erro ao salvar tema!");
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Tema tema) {
        try {
            String[] args = {tema.getId().toString()};
            write.delete(DBHelper.TABLE_TEMAS, "idTema=?", args);
            write.delete(DBHelper.TABLE_DESAFIOS, "idTema=?", args);
            Toast.makeText(context, "Tema removido com sucesso!", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(context, "Erro ao tentar remover!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public List<Tema> findAll() {

        List<Tema> temas = new ArrayList<>();

        String sql = "SELECT * FROM " + DBHelper.TABLE_TEMAS + " ;";
        Cursor cursor = read.rawQuery(sql, null);

        cursor.move(0);
        while (cursor.moveToNext()){
            Tema tema = new Tema();
            Long id = cursor.getLong(cursor.getColumnIndex("idTema"));
            String nomeImegem = cursor.getString(cursor.getColumnIndex("nomeImagem"));
            String imagemUrl = cursor.getString(cursor.getColumnIndex("imagemUrl"));

            tema.setId(id);
            tema.setNomeImagem(nomeImegem);
            tema.setImageUrl(imagemUrl);

            temas.add(tema);
            Log.i("Size: ", "List Size " + temas.size());
        }
        cursor.close();

        return temas;
    }
}
