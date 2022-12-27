package com.napoleao.alphabeto.helper.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.napoleao.alphabeto.model.Tema;

import java.io.File;
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
    public Tema get(Long idGet) {

        Long id = null;
        String nomeImegem = null, imagemUrl = null;

        String sql = "SELECT * FROM " + DBHelper.TABLE_TEMAS + " WHERE idTema = ?";

        Cursor cursor = read.rawQuery(sql, new String[]{Long.toString(idGet)});

        if (cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndex("idTema"));
            nomeImegem = cursor.getString(cursor.getColumnIndex("nomeImagem"));
            imagemUrl = cursor.getString(cursor.getColumnIndex("imagemUrl"));
        }
        cursor.close();

        if(id != null) {
            Tema tema = new Tema();
            tema.setId(id);
            tema.setNomeImagem(nomeImegem);
            tema.setImageUrl(imagemUrl);
            return tema;
        }

        return null;
    }

    @Override
    public boolean save(Tema tema) {
        ContentValues cv = new ContentValues();
        if(tema.getId() != null) {
            cv.put("idTema", tema.getId());
        }
        cv.put("nomeImagem", tema.getNomeImagem());
        cv.put("imagemUrl", tema.getImageUrl());
        try {
            Long id = write.insertOrThrow(DBHelper.TABLE_TEMAS, null, cv);
            tema.setId(id);
            cv.clear();
            Log.d("SAVE", "Tema salvo com sucesso!");
        }catch (Exception e){
            Log.d("SAVE", "Erro ao salvar tema!");
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Tema temaDel) {
        try {
            return deleteById(temaDel.getId());
        }catch (Exception e){
            Log.d("SAVE", "Erro ao remover!");
            return false;
        }
    }

    @Override
    public boolean deleteById(Long temaId) {
        try {

            // delete saved local image file
            Tema tema = get(temaId);
            if(tema!=null && tema.getImageUrl()!=null) {
                String imageSt = tema.getImageUrl();
                if(imageSt.startsWith("/")) {
                    File imageFile = new File(imageSt);
                    imageFile.delete();
                }
            }

            String[] args = {temaId.toString()};
            write.delete(DBHelper.TABLE_DESAFIOS, "idTema=?", args);
            write.delete(DBHelper.TABLE_TEMAS, "idTema=?", args);
            Toast.makeText(context, "Tema removido com sucesso!", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(context, "Erro ao tentar remover!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean update(Tema tema) {
        try {
            ContentValues cv = new ContentValues();

            cv.put("name", tema.getNomeImagem());
            cv.put("imageUrl", tema.getImageUrl());

            write.update(DBHelper.TABLE_TEMAS, cv, "id="+tema.getId(), null);
            cv.clear();
        }catch (Exception e){
            Toast.makeText(context, "Erro ao atualizar tema!", Toast.LENGTH_LONG).show();
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
