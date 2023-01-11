package com.napoleao.alphabeto.helper.dao;

import com.napoleao.alphabeto.model.Challenge;

import java.util.List;

public interface IChallengesDAO {
    Challenge get(Long id);
    boolean save(Challenge challenge);
    boolean update(Challenge challenge);
    boolean delete(Challenge challenge);
    boolean deleteById(Long challengeId);
    List<Challenge> findAll();
    List<Challenge> findById(Long idTema);
}
