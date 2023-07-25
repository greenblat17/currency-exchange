package com.greenblat.servlet.exchangerate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenblat.dto.ExchangeRateResponse;
import com.greenblat.dto.InsertExchangeRateRequest;
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
import java.util.List;

import static com.greenblat.validation.ExchangeRateValidator.validateExchangeRateEntity;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ObjectMapper objectMapper;
    private final ExchangeRateService exchangeRateService;

    public ExchangeRatesServlet() {
        this.objectMapper = new ObjectMapper();
        this.exchangeRateService = new ExchangeRateService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<ExchangeRateResponse> exchangeRates = exchangeRateService.getAllExchangeRates();

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    resp.getWriter(),
                    exchangeRates
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InsertExchangeRateRequest exchangeRateRequest = objectMapper.readValue(
                JsonUtil.getJsonObject(req.getReader()),
                InsertExchangeRateRequest.class
        );

        if (!validateExchangeRateEntity(exchangeRateRequest)) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Required form field is missing",
                    resp
            );
            return;
        }

        try {
            ExchangeRateResponse savedExchangeRate = exchangeRateService.saveExchangeRate(exchangeRateRequest);

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    resp.getWriter(),
                    savedExchangeRate
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
