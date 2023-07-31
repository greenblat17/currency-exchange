package com.greenblat.service;

import com.greenblat.dao.CurrencyDAO;
import com.greenblat.dao.ExchangeRateDAO;
import com.greenblat.dao.impl.CurrencyDAOImpl;
import com.greenblat.dao.impl.ExchangeRateDAOImpl;
import com.greenblat.dto.ExchangeRateResponse;
import com.greenblat.dto.InsertExchangeRateRequest;
import com.greenblat.entity.Currency;
import com.greenblat.entity.ExchangeRate;
import com.greenblat.exception.ResourceNotFoundException;
import com.greenblat.mapper.CurrencyMapper;
import com.greenblat.mapper.ExchangeRateMapper;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRateService {

    private final CurrencyMapper currencyMapper;
    private final ExchangeRateMapper exchangeRateMapper;
    private final ExchangeRateDAO exchangeRateDAO;
    private final CurrencyDAO currencyDAO;

    public ExchangeRateService() {
        this.currencyMapper = new CurrencyMapper();
        this.exchangeRateMapper = new ExchangeRateMapper();
        this.exchangeRateDAO = ExchangeRateDAOImpl.getInstance();
        this.currencyDAO = CurrencyDAOImpl.getInstance();
    }

    public List<ExchangeRateResponse> getAllExchangeRates() throws SQLException {
        return exchangeRateDAO.findAll()
                .stream()
                .map(exchangeRate -> {
                    try {
                        return exchangeRateToDTO(exchangeRate);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public ExchangeRateResponse getExchangeRateByCurrencyPair(String targetCurrencyCode,
                                                              String baseCurrencyCode) throws SQLException {
        Currency targetCurrency = findCurrency(targetCurrencyCode);
        Currency baseCurrency = findCurrency(baseCurrencyCode);

        ExchangeRate exchangeRate = findExchangeRate(targetCurrency, baseCurrency);

        return exchangeRateMapper.mapToDTO(
                exchangeRate,
                currencyMapper.mapToDTO(targetCurrency),
                currencyMapper.mapToDTO(baseCurrency)
        );
    }

    public ExchangeRateResponse updateExchangeRate(String targetCurrencyCode,
                                                   String baseCurrencyCode,
                                                   BigDecimal rate) throws SQLException {
        Currency targetCurrency = findCurrency(targetCurrencyCode);
        Currency baseCurrency = findCurrency(baseCurrencyCode);

        ExchangeRate exchangeRate = findExchangeRate(targetCurrency, baseCurrency);
        exchangeRate.setRate(rate);

        ExchangeRate savedExchangeRate = exchangeRateDAO.save(exchangeRate);

        return exchangeRateMapper.mapToDTO(
                savedExchangeRate,
                currencyMapper.mapToDTO(targetCurrency),
                currencyMapper.mapToDTO(baseCurrency)
        );
    }

    public ExchangeRateResponse saveExchangeRate(InsertExchangeRateRequest request) throws SQLException {
        Currency targetCurrency = findCurrency(request.targetCurrencyCode());
        Currency baseCurrency = findCurrency(request.baseCurrencyCode());

        ExchangeRate exchangeRate = buildExchangeRate(
                targetCurrency.getId(),
                baseCurrency.getId(),
                request.rate()
        );

        ExchangeRate savedExchangeRate = exchangeRateDAO.save(exchangeRate);

        return exchangeRateMapper.mapToDTO(
                savedExchangeRate,
                currencyMapper.mapToDTO(targetCurrency),
                currencyMapper.mapToDTO(baseCurrency)
        );
    }

    protected ExchangeRate findExchangeRate(Currency targetCurrency, Currency baseCurrency) throws SQLException {
        return exchangeRateDAO.findByCurrencies(
                baseCurrency.getId(),
                targetCurrency.getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Exchange Rate with target currency code [%s] and base currency code [%s] not found"
                        .formatted(targetCurrency.getCode(), baseCurrency.getCode())));
    }

    protected Currency findCurrency(String code) throws SQLException {
        return currencyDAO.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency with code [%s] not found".formatted(code)));
    }

    private ExchangeRateResponse exchangeRateToDTO(ExchangeRate exchangeRate) throws SQLException {
        Currency baseCurrency = currencyDAO.findById(exchangeRate.getBaseCurrencyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exchange Rate with id [%s] not found"
                                .formatted(exchangeRate.getBaseCurrencyId())));
        Currency targetCurrency = currencyDAO.findById(exchangeRate.getTargetCurrencyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exchange Rate with id [%s] not found"
                                .formatted(exchangeRate.getTargetCurrencyId())));

        return exchangeRateMapper.mapToDTO(
                exchangeRate,
                currencyMapper.mapToDTO(targetCurrency),
                currencyMapper.mapToDTO(baseCurrency)
        );
    }

    private ExchangeRate buildExchangeRate(Integer targetCurrencyId,
                                           Integer baseCurrencyId,
                                           BigDecimal rate) {
        return ExchangeRate.builder()
                .baseCurrencyId(baseCurrencyId)
                .targetCurrencyId(targetCurrencyId)
                .rate(rate)
                .build();
    }
}
