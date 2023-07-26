package com.greenblat.dao.impl;

import com.github.javafaker.Faker;
import com.greenblat.dao.CurrencyDAO;
import com.greenblat.dao.ExchangeRateDAO;
import com.greenblat.entity.Currency;
import com.greenblat.entity.ExchangeRate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeRateDAOImplTest {

    private final Faker faker;
    private final CurrencyDAO currencyDAO;
    private final ExchangeRateDAO underTest;

    public ExchangeRateDAOImplTest() {
        faker = new Faker();
        currencyDAO = new CurrencyDAOImpl();
        underTest = new ExchangeRateDAOImpl();
    }

    @Test
    void findAll() throws SQLException {
        // Given
        Currency currency1 = currencyDAO.save(getCurrency());
        Currency currency2 = currencyDAO.save(getCurrency());

        ExchangeRate exchangeRate = getExchangeRate(currency1.getId(), currency2.getId());
        underTest.save(exchangeRate);

        // When
        List<ExchangeRate> actual = underTest.findAll();

        // Then
        assertThat(actual).isNotEmpty();

    }

    @Test
    void findById() throws SQLException {
        // Given
        Currency currency1 = currencyDAO.save(getCurrency());
        Currency currency2 = currencyDAO.save(getCurrency());

        ExchangeRate exchangeRate = getExchangeRate(currency1.getId(), currency2.getId());

        ExchangeRate expected = underTest.save(exchangeRate);

        // When
        Optional<ExchangeRate> actual = underTest.findById(expected.getId());

        // Then
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(rate -> {
                    assertThat(rate.getId()).isEqualTo(expected.getId());
                    assertThat(rate.getRate()).isEqualTo(expected.getRate());
                    assertThat(rate.getTargetCurrencyId()).isEqualTo(expected.getTargetCurrencyId());
                    assertThat(rate.getBaseCurrencyId()).isEqualTo(expected.getBaseCurrencyId());
                });
    }

    @Test
    void willReturnEmptyWhenFindByIdNotExists() throws SQLException {
        // Given
        int id = -1;

        // When
        Optional<ExchangeRate> actual = underTest.findById(id);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void findByCurrencies() throws SQLException {
        // Given
        Currency currency1 = currencyDAO.save(getCurrency());
        Currency currency2 = currencyDAO.save(getCurrency());

        ExchangeRate exchangeRate = getExchangeRate(currency1.getId(), currency2.getId());
        underTest.save(exchangeRate);

        // When
        Optional<ExchangeRate> actual = underTest.findByCurrencies(
                exchangeRate.getBaseCurrencyId(),
                exchangeRate.getTargetCurrencyId()
        );

        // Then
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(rate -> {
                    assertThat(rate.getId()).isEqualTo(exchangeRate.getId());
                    assertThat(rate.getRate()).isEqualTo(exchangeRate.getRate());
                    assertThat(rate.getBaseCurrencyId()).isEqualTo(exchangeRate.getBaseCurrencyId());
                    assertThat(rate.getTargetCurrencyId()).isEqualTo(exchangeRate.getTargetCurrencyId());
                });
    }

    @Test
    void willReturnEmptyWhenFindByCurrenciesNotExists() throws SQLException {
        // Given
        int baseCurrencyId = -1;
        int targetCurrencyId = -2;

        // When
        Optional<ExchangeRate> actual = underTest.findByCurrencies(
                baseCurrencyId,
                targetCurrencyId
        );

        // Then
        assertThat(actual).isEmpty();

    }

    @Test
    void save() throws SQLException {
        // Given
        Currency currency1 = currencyDAO.save(getCurrency());
        Currency currency2 = currencyDAO.save(getCurrency());

        ExchangeRate exchangeRate = getExchangeRate(currency1.getId(), currency2.getId());
        underTest.save(exchangeRate);

        // When
        ExchangeRate actual = underTest.save(exchangeRate);

        // Then
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(exchangeRate);
    }

    private Currency getCurrency() {
        return Currency.builder()
                .fullName(faker.name().prefix())
                .sign("s")
                .code(faker.code().isbn10())
                .build();
    }

    private ExchangeRate getExchangeRate(int baseCurrencyId, int targetCurrencyId) {
        return ExchangeRate.builder()
                .baseCurrencyId(baseCurrencyId)
                .targetCurrencyId(targetCurrencyId)
                .rate(new BigDecimal("1.000000"))
                .build();
    }
}