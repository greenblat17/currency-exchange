package com.greenblat.dao.impl;

import com.greenblat.config.db.ConnectionManager;
import com.greenblat.dao.ExchangeRateDAO;
import com.greenblat.entity.ExchangeRate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAOImpl implements ExchangeRateDAO {

    @Override
    public List<ExchangeRate> findAll() throws SQLException {
        var sql = """
                SELECT id, base_currency_id, target_currency_id, rate
                FROM exchange_rate
                """;

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }

            resultSet.close();

            return exchangeRates;
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Integer id) throws SQLException {
        var sql = """
                SELECT id, base_currency_id, target_currency_id, rate
                FROM exchange_rate
                WHERE id = ?
                """;

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;
            while (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }

            resultSet.close();

            return Optional.ofNullable(exchangeRate);
        }
    }

    @Override
    public Optional<ExchangeRate> findByCurrencies(Integer baseCurrencyId,
                                                   Integer targetCurrencyId) throws SQLException{
        var sql = """
                SELECT id, base_currency_id, target_currency_id, rate
                FROM exchange_rate
                WHERE base_currency_id = ? AND target_currency_id = ?
                """;

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);

            ResultSet resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;
            while (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }

            resultSet.close();

            return Optional.ofNullable(exchangeRate);
        }
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) throws SQLException {
        Integer id = exchangeRate.getId();
        return id == null ? saveExchangeRate(exchangeRate) : updateExchangeRate(exchangeRate);
    }

    private ExchangeRate saveExchangeRate(ExchangeRate exchangeRate) throws SQLException {
        var sql = """
                INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate) 
                VALUES (?, ?, ?)
                """;

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, exchangeRate.getBaseCurrencyId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrencyId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            while (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getInt("id"));
            }

            generatedKeys.close();

            return exchangeRate;
        }
    }

    private ExchangeRate updateExchangeRate(ExchangeRate exchangeRate) throws SQLException {
        var sql = """
                UPDATE exchange_rate
                SET rate = ?
                WHERE base_currency_id = ? AND target_currency_id = ?
                """;

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setBigDecimal(1, exchangeRate.getRate());
            preparedStatement.setInt(2, exchangeRate.getBaseCurrencyId());
            preparedStatement.setInt(3, exchangeRate.getTargetCurrencyId());

            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            while (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getInt("id"));
            }

            generatedKeys.close();

            return exchangeRate;
        }
    }

    public ExchangeRate buildExchangeRate(ResultSet rs) throws SQLException {
        return ExchangeRate.builder()
                .id(rs.getInt("id"))
                .baseCurrencyId(rs.getInt("base_currency_id"))
                .targetCurrencyId(rs.getInt("target_currency_id"))
                .rate(rs.getBigDecimal("rate"))
                .build();
    }

}
