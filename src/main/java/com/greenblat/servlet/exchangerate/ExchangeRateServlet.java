package com.greenblat.servlet.exchangerate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenblat.dto.ExchangeRateResponse;
import com.greenblat.dto.UpdateExchangeRateRequest;
import com.greenblat.exception.ErrorResponseHandler;
import com.greenblat.exception.ResourceNotFoundException;
import com.greenblat.service.ExchangeRateService;
import com.greenblat.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import static com.greenblat.validation.ExchangeRateValidator.validateExchangeRateCodes;
import static com.greenblat.validation.ExchangeRateValidator.validateRate;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ObjectMapper objectMapper;
    private final ExchangeRateService exchangeRateService;

    public ExchangeRateServlet() {
        this.objectMapper = new ObjectMapper();
        this.exchangeRateService = new ExchangeRateService();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (!method.equals("PATCH")) {
            super.service(req, resp);
        }

        this.doPatch(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        if (!validateExchangeRateCodes(path)) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "The pair's currency codes are missing from the address",
                    resp
            );
            return;
        }

        String currencyPair = path.substring(1);
        String baseCurrency = currencyPair.substring(0, currencyPair.length() / 2);
        String targetCurrency = currencyPair.substring(currencyPair.length() / 2);

        try {
            ExchangeRateResponse exchangeRateResponse = exchangeRateService.getExchangeRateByCurrencyPair(targetCurrency, baseCurrency);

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    resp.getWriter(),
                    exchangeRateResponse
            );
        } catch (ResourceNotFoundException e) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    e.getMessage(),
                    resp
            );
        } catch (SQLException e) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database failed connection",
                    resp
            );
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currencyPair = req.getPathInfo().substring(1);

        if (!validateExchangeRateCodes(currencyPair)) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "The pair's currency codes are missing from the address",
                    resp
            );
            return;
        }

        String baseCurrency = currencyPair.substring(0, currencyPair.length() / 2);
        String targetCurrency = currencyPair.substring(currencyPair.length() / 2);

        UpdateExchangeRateRequest rateRequest = objectMapper.readValue(
                JsonUtil.getJsonObject(req.getReader()),
                UpdateExchangeRateRequest.class
        );

        if (!validateRate(rateRequest)) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Required form field is missing",
                    resp
            );
            return;
        }

        try {
            ExchangeRateResponse updateExchangeRate = exchangeRateService.updateExchangeRate(targetCurrency, baseCurrency, rateRequest.rate());

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    resp.getWriter(),
                    updateExchangeRate
            );
        } catch (ResourceNotFoundException e) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    e.getMessage(),
                    resp
            );
        } catch (SQLException e) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database failed connection",
                    resp
            );
        }
    }
}
