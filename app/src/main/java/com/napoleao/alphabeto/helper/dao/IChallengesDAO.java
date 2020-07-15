package com.napoleao.alphabeto.helper.dao;

import com.napoleao.alphabeto.model.Challenge;

import java.util.List;

public interface IChallengesDAO {

    boolean save(Challenge challenge);
    boolean delete(Challenge challenge);
    List<Challenge> findAll();
    List<Challenge> findById(Long idTema);
}
