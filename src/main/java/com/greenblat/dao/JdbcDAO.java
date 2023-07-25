package com.greenblat.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface JdbcDAO<T> {

    List<T> findAll() throws SQLException;

    Optional<T> findById(Integer id) throws SQLException;

    T save(T entity) throws SQLException;
}
