package com.greenblat.mapper;

import com.greenblat.dto.CurrencyDTO;
import com.greenblat.entity.Currency;

public class CurrencyMapper {

    public Currency mapToCurrency(CurrencyDTO dto) {
        return Currency.builder()
                .id(dto.id())
                .fullName(dto.name())
                .code(dto.code())
                .sign(dto.sign())
                .build();
    }

    public CurrencyDTO mapToDTO(Currency currency) {
        return new CurrencyDTO(
                currency.getId(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign()
        );
    }
}
