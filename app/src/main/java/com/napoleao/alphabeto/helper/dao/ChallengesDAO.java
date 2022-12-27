package com.napoleao.alphabeto.helper.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.napoleao.alphabeto.model.Challenge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChallengesDAO implements IChallengesDAO {

    private SQLiteDatabase write;
    private SQLiteDatabase read;
    private Context context;

    public ChallengesDAO(Context context){
        this.context = context;
        DBHelper dbHelper = new DBHelper(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    @Override
    public Challenge get(Long idGet) {
        Long id = null, idTema = null;
        String word = null, imageUrl = null;

        String sql = "SELECT * FROM " + DBHelper.TABLE_DESAFIOS + " WHERE idDesafio = ?";

        Cursor cursor = read.rawQuery(sql, new String[]{Long.toString(idGet)});

        if (cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndex("idDesafio"));
            word = cursor.getString(cursor.getColumnIndex("word"));
            imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
            idTema = cursor.getLong(cursor.getColumnIndex("idTema"));
        }
        cursor.close();

        if(id != null) {
            Challenge challenge = new Challenge();
            challenge.setId(id);
            challenge.setWord(word);
            challenge.setImageUrl(imageUrl);
            challenge.setIdTema(idTema);
            return challenge;
        }

        return null;
    }

    @Override
    public boolean save(Challenge challenge) {
        ContentValues cv = new ContentValues();
        if(challenge.getId() != null) {
            cv.put("idDesafio", challenge.getId());
        }
        cv.put("word", challenge.getWord());
        cv.put("imageUrl", challenge.getImageUrl());
        cv.put("idTema", challenge.getIdTema());
        try {
            Long id = write.insertOrThrow(DBHelper.TABLE_DESAFIOS, null, cv);
            challenge.setId(id);
            Log.d("SAVE", "Desafio salvo com sucesso!");
        }catch (Exception e){
            Log.d("SAVE", "Erro ao salvar!");
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Challenge challenge) {
        try {
            return deleteById(challenge.getId());
        }catch (Exception e){
            Log.d("SAVE", "Erro ao remover!");
            return false;
        }
    }

    @Override
    public boolean deleteById(Long challengeId) {
        try {

            // delete saved local image file
            Challenge palavra = get(challengeId);
            if(palavra!=null && palavra.getImageUrl()!=null) {
                String imageSt = palavra.getImageUrl();
                if(imageSt.startsWith("/")) {
                    File imageFile = new File(imageSt);
                    imageFile.delete();
                }
            }

            String[] args = {challengeId.toString()};
            write.delete(DBHelper.TABLE_DESAFIOS, "idDesafio=?", args);
            Log.d("SAVE", "Desafio removido com sucesso!");
        }catch (Exception e){
            Log.d("SAVE", "Erro ao remover!");
            return false;
        }
        return true;
    }

    @Override
    public boolean update(Challenge challenge) {
        try {
            ContentValues cv = new ContentValues();

            cv.put("word", challenge.getWord());
            cv.put("imageUrl", challenge.getImageUrl());
            cv.put("idTema", challenge.getIdTema());

            write.update(DBHelper.TABLE_DESAFIOS, cv, "id="+challenge.getId(), null);
            cv.clear();
        }catch (Exception e){
            Toast.makeText(context, "Erro ao atualizar tema!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public List<Challenge> findAll() {
        List<Challenge> challenges = new ArrayList<>();

        String sql = "SELECT * FROM " + DBHelper.TABLE_DESAFIOS + " ;";
        Cursor cursor = read.rawQuery(sql, null);

        cursor.move(0);
        while (cursor.moveToNext()){
            Challenge challenge = new Challenge();
            Long idDesafio = cursor.getLong(cursor.getColumnIndex("idDesafio"));
            String word = cursor.getString(cursor.getColumnIndex("nomeImagem"));
            String imageUrl = cursor.getString(cursor.getColumnIndex("imagemUrl"));
            Long idTema = cursor.getLong(cursor.getColumnIndex("idTema"));

            challenge.setId(idDesafio);
            challenge.setWord(word);
            challenge.setImageUrl(imageUrl);
            challenge.setIdTema(idTema);

            challenges.add(challenge);
            Log.i("Size: ", "List Size " + challenges.size());
        }
        cursor.close();

        return challenges;
    }

    @Override
    public List<Challenge> findById(Long id) {
        List<Challenge> challenges = new ArrayList<>();

        String sql = "SELECT *" +
                " FROM " + DBHelper.TABLE_DESAFIOS +
                " WHERE idTema = " + id;
        Cursor cursor = read.rawQuery(sql, null);

        while (cursor.moveToNext()){
            Challenge challenge = new Challenge();
            Long idDesafio = cursor.getLong(cursor.getColumnIndex("idDesafio"));
            String word = cursor.getString(cursor.getColumnIndex("word"));
            String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
            Long idTema = cursor.getLong(cursor.getColumnIndex("idTema"));

            challenge.setId(idDesafio);
            challenge.setWord(word);
            challenge.setImageUrl(imageUrl);
            challenge.setIdTema(idTema);

            challenges.add(challenge);
            Log.i("Size: ", "List Size " + challenges.size());
        }
        cursor.close();

        return challenges;
    }
}
