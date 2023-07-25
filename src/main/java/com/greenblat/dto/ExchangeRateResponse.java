package com.greenblat.dto;

import java.math.BigDecimal;

public record ExchangeRateResponse(
        Integer id,
        CurrencyDTO baseCurrency,
        CurrencyDTO targetCurrency,
        BigDecimal rate) {
}
