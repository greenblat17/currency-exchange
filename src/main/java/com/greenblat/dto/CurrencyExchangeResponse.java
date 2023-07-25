package com.greenblat.dto;

import java.math.BigDecimal;

public record CurrencyExchangeResponse(CurrencyDTO baseCurrency,
                                       CurrencyDTO targetCurrency,
                                       BigDecimal rate,
                                       BigDecimal amount,
                                       BigDecimal convertedAmount) {
}
