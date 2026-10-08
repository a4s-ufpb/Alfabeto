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

/**
 * Class responsable for access database and manipulate Challenge.
 */
public class ChallengesDAO implements IChallengesDAO {

    private static ChallengesDAO instance;
    private SQLiteDatabase write;
    private SQLiteDatabase read;
    private Context context;

    /**
     * Method responsable for return instance of ChallengesDAO.
     *
     * @param context
     * @return instance
     */
    public static synchronized ChallengesDAO getInstance(Context context) {
        if (instance == null) {
            instance = new ChallengesDAO(context);
        }
        return instance;
    }

    /**
     * Constructor for ChallengesDAO.
     *
     * @param context
     */
    public ChallengesDAO(Context context) {
        this.context = context;
        DBHelper dbHelper = DBHelper.getInstance(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    /**
     * Method responsable for get a challenge.
     *
     * @param idGet id of challenge
     * @return Challenge
     */
    @Override
    public Challenge get(Long idGet) {
        Long id = null, idTema = null, remoteId = null;
        String word = null, imageUrl = null;

        String sql = "SELECT * FROM " + DBHelper.TABLE_DESAFIOS + " WHERE idDesafio = ?";

        Cursor cursor = read.rawQuery(sql, new String[]{Long.toString(idGet)});

        if (cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndex("idDesafio"));
            word = cursor.getString(cursor.getColumnIndex("word"));
            imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
            idTema = cursor.getLong(cursor.getColumnIndex("idTema"));
            remoteId = DBHelper.getLongOrNull(cursor, "remoteId");
        }
        cursor.close();

        if (id != null) {
            Challenge challenge = new Challenge();
            challenge.setId(id);
            challenge.setWord(word);
            challenge.setImageUrl(imageUrl);
            challenge.setIdTema(idTema);
            challenge.setRemoteId(remoteId);
            return challenge;
        }

        return null;
    }

    /**
     * Method responsable for save challenge.
     *
     * @return boolean
     */
    @Override
    public boolean save(Challenge challenge) {
        ContentValues cv = new ContentValues();
        if (challenge.getId() != null) {
            cv.put("idDesafio", challenge.getId());
        }
        cv.put("word", challenge.getWord());
        cv.put("imageUrl", challenge.getImageUrl());
        cv.put("idTema", challenge.getIdTema());
        if (challenge.getRemoteId() != null) {
            cv.put("remoteId", challenge.getRemoteId());
        }
        try {
            Long id = write.insertOrThrow(DBHelper.TABLE_DESAFIOS, null, cv);
            challenge.setId(id);
            cv.clear();
            Log.d("SAVE", "Desafio salvo com sucesso!");
        } catch (Exception e) {
            Log.d("SAVE", "Erro ao salvar!");
            return false;
        }
        return true;
    }

    /**
     * Method responsable for delete a challenge.
     *
     * @param challenge
     * @return boolean
     */
    @Override
    public boolean delete(Challenge challenge) {
        try {
            return deleteById(challenge.getId());
        } catch (Exception e) {
            Log.d("SAVE", "Erro ao remover!");
            return false;
        }
    }

    /**
     * Method responsable for delete a challenge by id.
     *
     * @param challengeId
     * @return boolean
     */
    @Override
    public boolean deleteById(Long challengeId) {
        try {

            // delete saved local image file
            Challenge palavra = get(challengeId);
            if (palavra != null && palavra.getImageUrl() != null) {
                String imageSt = palavra.getImageUrl();
                if (imageSt.startsWith("/")) {
                    File imageFile = new File(imageSt);
                    imageFile.delete();
                }
            }

            String[] args = {challengeId.toString()};
            write.delete(DBHelper.TABLE_DESAFIOS, "idDesafio=?", args);
            Log.d("SAVE", "Desafio removido com sucesso!");
        } catch (Exception e) {
            Log.d("SAVE", "Erro ao remover!");
            return false;
        }
        return true;
    }

    /**
     * Method responsable for update a challenge.
     *
     * @param challenge
     * @return boolean
     */
    @Override
    public boolean update(Challenge challenge) {
        try {
            ContentValues cv = new ContentValues();

            cv.put("word", challenge.getWord());
            cv.put("imageUrl", challenge.getImageUrl());
            cv.put("idTema", challenge.getIdTema());

            String[] args = {challenge.getId().toString()};
            write.update(DBHelper.TABLE_DESAFIOS, cv, "idDesafio=?", args);
            cv.clear();
        } catch (Exception e) {
            Toast.makeText(context, "Erro ao atualizar tema!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Method responsable for get all challenges.
     *
     * @return List<Challenge>
     */
    @Override
    public List<Challenge> findAll() {
        List<Challenge> challenges = new ArrayList<>();

        String sql = "SELECT * FROM " + DBHelper.TABLE_DESAFIOS + " ;";
        Cursor cursor = read.rawQuery(sql, null);

        cursor.move(0);
        while (cursor.moveToNext()) {
            Challenge challenge = new Challenge();
            Long idDesafio = cursor.getLong(cursor.getColumnIndex("idDesafio"));
            String word = cursor.getString(cursor.getColumnIndex("word"));
            String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
            Long idTema = cursor.getLong(cursor.getColumnIndex("idTema"));

            challenge.setId(idDesafio);
            challenge.setWord(word);
            challenge.setImageUrl(imageUrl);
            challenge.setIdTema(idTema);
            challenge.setRemoteId(DBHelper.getLongOrNull(cursor, "remoteId"));

            challenges.add(challenge);
            Log.i("Size: ", "List Size " + challenges.size());
        }
        cursor.close();

        return challenges;
    }

    /**
     * Method responsable for get all challenges by id.
     *
     * @param id
     * @return List<Challenge>
     */
    @Override
    public List<Challenge> findById(Long id) {
        List<Challenge> challenges = new ArrayList<>();

        String sql = "SELECT *" +
                " FROM " + DBHelper.TABLE_DESAFIOS +
                " WHERE idTema = " + id;
        Cursor cursor = read.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            Challenge challenge = new Challenge();
            Long idDesafio = cursor.getLong(cursor.getColumnIndex("idDesafio"));
            String word = cursor.getString(cursor.getColumnIndex("word"));
            String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
            Long idTema = cursor.getLong(cursor.getColumnIndex("idTema"));

            challenge.setId(idDesafio);
            challenge.setWord(word);
            challenge.setImageUrl(imageUrl);
            challenge.setIdTema(idTema);
            challenge.setRemoteId(DBHelper.getLongOrNull(cursor, "remoteId"));

            challenges.add(challenge);
            Log.i("Size: ", "List Size " + challenges.size());
        }
        cursor.close();

        return challenges;
    }
}
