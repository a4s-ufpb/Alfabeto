package com.napoleao.alphabeto.helper.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.napoleao.alphabeto.model.Challenge;

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
    public boolean save(Challenge challenge) {
        ContentValues cv = new ContentValues();
        cv.put("idDesafio", challenge.getId());
        cv.put("word", challenge.getWord());
        cv.put("imageUrl", challenge.getImageUrl());
        cv.put("idTema", challenge.getIdTema());
        try {
            write.insertOrThrow(DBHelper.TABLE_DESAFIOS, null, cv);
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
            String[] args = {challenge.getId().toString()};
            write.delete(DBHelper.TABLE_TEMAS, "id=?", args);
            Log.d("SAVE", "Desafio removido com sucesso!");
        }catch (Exception e){
            Log.d("SAVE", "Erro ao remover!");
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
