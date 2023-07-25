package com.greenblat.dao;

import com.greenblat.entity.Currency;

import java.sql.SQLException;
import java.util.Optional;

public interface CurrencyDAO extends JdbcDAO<Currency> {

    Optional<Currency> findByCode(String code) throws SQLException;

}
