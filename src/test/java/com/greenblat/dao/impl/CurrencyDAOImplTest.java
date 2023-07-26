package com.greenblat.dao.impl;

import com.github.javafaker.Faker;
import com.greenblat.dao.CurrencyDAO;
import com.greenblat.entity.Currency;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class CurrencyDAOImplTest {

    private final Faker faker;
    private final CurrencyDAO underTest;

    public  CurrencyDAOImplTest() {
        faker = new Faker();
        underTest = new CurrencyDAOImpl();
    }

    @Test
    void findAll() throws SQLException {
        // Given
        Currency currency = getCurrency();
        underTest.save(currency);

        // When
        List<Currency> actual = underTest.findAll();

        // Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void findByCode() throws SQLException {
        // Given
        Currency currency = getCurrency();
        underTest.save(currency);

        // When
        Optional<Currency> actual = underTest.findByCode(currency.getCode());

        // Then
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(currency.getId());
                    assertThat(c.getCode()).isEqualTo(currency.getCode());
                    assertThat(c.getFullName()).isEqualTo(currency.getFullName());
                    assertThat(c.getSign()).isEqualTo(currency.getSign());
                });
    }

    @Test
    void willReturnEmptyWhenFindByCodeNotExists() throws SQLException {
        // Given
        String code = "";

        // When
        Optional<Currency> actual = underTest.findByCode(code);

        // Then
        assertThat(actual).isEmpty();

    }

    @Test
    void save() throws SQLException {
        // Given
        Currency currency = getCurrency();

        // When
        Currency actual = underTest.save(currency);

        // Then
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(currency);
    }

    @Test
    void findById() throws SQLException {
        // Given
        Currency currency = getCurrency();

        Currency expected = underTest.save(currency);

        // When
        Optional<Currency> actual = underTest.findById(expected.getId());

        // Then
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(expected.getId());
                    assertThat(c.getSign()).isEqualTo(expected.getSign());
                    assertThat(c.getCode()).isEqualTo(expected.getCode());
                    assertThat(c.getFullName()).isEqualTo(expected.getFullName());
                });
    }

    @Test
    void willReturnEmptyWhenFindByIdNotExists() throws SQLException {
        // Given
        int id = -1;

        // When
        Optional<Currency> actual = underTest.findById(id);

        // Then
        assertThat(actual).isEmpty();
    }

    private Currency getCurrency() {
        return Currency.builder()
                .fullName(faker.name().prefix())
                .sign("s")
                .code(faker.code().isbn10())
                .build();
    }
}