package com.greenblat.dao.impl;

import com.greenblat.config.db.ConnectionManager;
import com.greenblat.dao.CurrencyDAO;
import com.greenblat.entity.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAOImpl implements CurrencyDAO {


    @Override
    public List<Currency> findAll() throws SQLException {
        var sql = """
                SELECT id, code, full_name, sign
                FROM currency
                """;

        List<Currency> currencies = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }

            resultSet.close();

            return currencies;
        }

    }

    @Override
    public Optional<Currency> findByCode(String code) throws SQLException {
        var sql = """
                SELECT id, code, full_name, sign
                FROM currency
                WHERE code = ?
                """;

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            while (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }

            resultSet.close();

            return Optional.ofNullable(currency);
        }
    }

    @Override
    public Currency save(Currency currency) throws SQLException {
        var sql = """
                INSERT INTO currency (code, full_name, sign) 
                VALUES (?, ?, ?) 
                """;

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            while (generatedKeys.next()) {
                currency.setId(generatedKeys.getInt("id"));
            }

            generatedKeys.close();

            return currency;
        }

    }

    @Override
    public Optional<Currency> findById(Integer id) throws SQLException {
        var sql = """
                SELECT id, code, full_name, sign
                FROM currency
                WHERE id = ?
                """;

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            while (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }

            resultSet.close();

            return Optional.ofNullable(currency);
        }
    }

    private Currency buildCurrency(ResultSet rs) throws SQLException {
        return Currency.builder()
                .id(rs.getInt("id"))
                .code(rs.getString("code"))
                .fullName(rs.getString("full_name"))
                .sign(rs.getString("sign"))
                .build();
    }
}
