package com.greenblat.validation;

import com.greenblat.dto.CurrencyDTO;

public class CurrencyValidator {

    public static boolean validateCurrencyCode(String code) {
        if (code.isEmpty() || code.charAt(0) != '/' || code.length() != 4) {
            return false;
        }

        return code.matches("^[A-Z]{3}$");
    }

    public static boolean validateCurrencyEntity(CurrencyDTO currency) {
        return currency.code() != null && currency.sign() != null && currency.name() != null;
    }
}
