package com.greenblat.dto;

import java.math.BigDecimal;

public record InsertExchangeRateRequest(String baseCurrencyCode,
                                       String targetCurrencyCode,
                                       BigDecimal rate) {
}
