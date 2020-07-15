package com.napoleao.alphabeto.helper.dao;

import com.napoleao.alphabeto.model.Tema;

import java.util.List;

public interface ITemasDAO {

    boolean save(Tema tema);
    boolean delete(Tema tema);
    List<Tema> findAll();
}
