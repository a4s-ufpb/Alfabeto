package com.napoleao.alphabeto.helper.dao;

import com.napoleao.alphabeto.model.Tema;

import java.util.List;

public interface ITemasDAO {
    Tema get(Long id);
    boolean save(Tema tema);
    boolean update(Tema tema);
    boolean delete(Tema tema);
    boolean deleteById(Long temaId);
    List<Tema> findAll();
    Tema findByRemote(Long remoteId, String name);
}
