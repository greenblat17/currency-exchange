package com.greenblat.service;

import com.greenblat.dao.CurrencyDAO;
import com.greenblat.dao.ExchangeRateDAO;
import com.greenblat.dao.impl.CurrencyDAOImpl;
import com.greenblat.dao.impl.ExchangeRateDAOImpl;
import com.greenblat.dto.CurrencyExchangeResponse;
import com.greenblat.entity.Currency;
import com.greenblat.entity.ExchangeRate;
import com.greenblat.exception.ResourceNotFoundException;
import com.greenblat.mapper.CurrencyMapper;
import com.greenblat.util.PropertiesUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeService {

    private final ExchangeRateService exchangeRateService;
    private final CurrencyMapper currencyMapper;
    private final ExchangeRateDAO exchangeRateDAO;
    private final CurrencyDAO currencyDAO;

    private static final String USD_CODE_KEY = "exchange_rate.code.usd";
    private static final String BIG_DECIMAL_SCALE_CODE = "exchange.big_decimal.scale";

    public ExchangeService() {
        this.exchangeRateService = new ExchangeRateService();
        this.currencyMapper = new CurrencyMapper();
        this.exchangeRateDAO = new ExchangeRateDAOImpl();
        this.currencyDAO = new CurrencyDAOImpl();
    }

    public CurrencyExchangeResponse getCurrencyExchange(String baseCurrencyCode,
                                                        String targetCurrencyCode,
                                                        BigDecimal amount) throws SQLException {
        Currency targetCurrency = exchangeRateService.findCurrency(targetCurrencyCode);
        Currency baseCurrency = exchangeRateService.findCurrency(baseCurrencyCode);

        Optional<ExchangeRate> currenctExchangeOptional = findDirectRate(baseCurrency, targetCurrency);
        if (currenctExchangeOptional.isEmpty()) currenctExchangeOptional = findReverseRate(baseCurrency, targetCurrency);
        if (currenctExchangeOptional.isEmpty()) currenctExchangeOptional =findCrossRate(baseCurrency, targetCurrency);

        ExchangeRate currencyExchange = currenctExchangeOptional
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exchange Rate for currencies [%s], [%s] not found"
                                .formatted(baseCurrency, targetCurrency)));


        return buildCurrencyExchangeResponse(amount, targetCurrency, baseCurrency, currencyExchange);
    }

    private Optional<ExchangeRate> findDirectRate(Currency baseCurrency, Currency targetCurrency) throws SQLException {
        return exchangeRateDAO.findByCurrencies(baseCurrency.getId(), targetCurrency.getId());
    }

    private Optional<ExchangeRate> findReverseRate(Currency baseCurrency, Currency targetCurrency) throws SQLException {
        Optional<ExchangeRate> exchangeOptional = findDirectRate(targetCurrency, baseCurrency);
        if (exchangeOptional.isPresent()) {
            ExchangeRate exchangeRate = exchangeOptional.get();

            BigDecimal rate = BigDecimal.ONE
                    .divide(
                            exchangeRate.getRate(),
                            Integer.parseInt(PropertiesUtil.get(BIG_DECIMAL_SCALE_CODE)),
                            RoundingMode.HALF_EVEN);
            exchangeRate.setRate(rate);

            return Optional.of(exchangeRate);
        }
        return exchangeOptional;
    }

    private Optional<ExchangeRate> findCrossRate(Currency baseCurrency, Currency targetCurrency) throws SQLException {
        Currency usdCurrency = exchangeRateService.findCurrency(USD_CODE_KEY);

        ExchangeRate exchangeRateUSDToBase = exchangeRateService.findExchangeRate(usdCurrency, baseCurrency);
        ExchangeRate exchangeRateUSDToTarget = exchangeRateService.findExchangeRate(usdCurrency, targetCurrency);

        BigDecimal rate = exchangeRateUSDToTarget.getRate()
                .divide(
                        exchangeRateUSDToBase.getRate(),
                        Integer.parseInt(PropertiesUtil.get(BIG_DECIMAL_SCALE_CODE)),
                        RoundingMode.HALF_EVEN);

        return Optional.of(
                buildExchangeRate(baseCurrency, targetCurrency, rate)
        );
    }

    private static ExchangeRate buildExchangeRate(Currency baseCurrency,
                                                  Currency targetCurrency,
                                                  BigDecimal rate) {
        return ExchangeRate.builder()
                .baseCurrencyId(baseCurrency.getId())
                .targetCurrencyId(targetCurrency.getId())
                .rate(rate)
                .build();
    }


    private CurrencyExchangeResponse buildCurrencyExchangeResponse(BigDecimal amount,
                                                                   Currency targetCurrency,
                                                                   Currency baseCurrency,
                                                                   ExchangeRate currencyExchange) {
        return new CurrencyExchangeResponse(
                currencyMapper.mapToDTO(baseCurrency),
                currencyMapper.mapToDTO(targetCurrency),
                currencyExchange.getRate(),
                amount,
                amount.multiply(currencyExchange.getRate())
        );
    }
}
