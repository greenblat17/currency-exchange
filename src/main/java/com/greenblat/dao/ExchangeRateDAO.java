package com.greenblat.dao;

import com.greenblat.entity.ExchangeRate;

import java.sql.SQLException;
import java.util.Optional;

public interface ExchangeRateDAO extends JdbcDAO<ExchangeRate> {

    Optional<ExchangeRate> findByCurrencies(Integer baseCurrencyId,
                                            Integer targetCurrencyId) throws SQLException;
}