package org.agency.repository;

import org.agency.entity.Ticket;

import java.util.List;

public interface BaseRepository<T> {

    boolean existsById(Long id);

    List<Ticket> findAll();

    Ticket findById(Long id);

    void create(T t);

    void update(T t);

    void delete(Long id);

}