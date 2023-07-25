package com.greenblat.mapper;

import com.greenblat.dto.CurrencyDTO;
import com.greenblat.dto.ExchangeRateResponse;
import com.greenblat.entity.ExchangeRate;

public class ExchangeRateMapper {

    public ExchangeRateResponse mapToDTO(ExchangeRate exchangeRate,
                                         CurrencyDTO target,
                                         CurrencyDTO base) {
        return new ExchangeRateResponse(
                exchangeRate.getId(),
                base,
                target,
                exchangeRate.getRate()
        );
    }
}
