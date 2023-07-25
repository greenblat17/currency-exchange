package com.greenblat.validation;

import com.greenblat.dto.InsertExchangeRateRequest;
import com.greenblat.dto.UpdateExchangeRateRequest;

import static com.greenblat.validation.CurrencyValidator.validateCurrencyCode;

public class ExchangeRateValidator {

    public static boolean validateExchangeRateCodes(String currencyPair) {
        if (currencyPair.isEmpty() || currencyPair.charAt(0) != '/' || currencyPair.length() != 7) {
            return false;
        }

        String baseCurrency = currencyPair.substring(0, currencyPair.length() / 2);
        String targetCurrency = currencyPair.substring(currencyPair.length() / 2);

        return validateCurrencyCode(baseCurrency) && validateCurrencyCode(targetCurrency);
    }

    public static boolean validateRate(UpdateExchangeRateRequest rateRequest) {
        return rateRequest != null && rateRequest.rate() != null;
    }

    public static boolean validateExchangeRateEntity(InsertExchangeRateRequest exchangeRate) {
        return exchangeRate.rate() != null &&
               exchangeRate.baseCurrencyCode() != null &&
               exchangeRate.targetCurrencyCode() != null;
    }
}
