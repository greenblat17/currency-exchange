package com.greenblat.servlet.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenblat.dto.CurrencyExchangeResponse;
import com.greenblat.dto.ExchangeRateResponse;
import com.greenblat.exception.ErrorResponseHandler;
import com.greenblat.exception.ResourceNotFoundException;
import com.greenblat.service.ExchangeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ObjectMapper objectMapper;
    private final ExchangeService exchangeService;

    public ExchangeServlet() {
        this.objectMapper = new ObjectMapper();
        this.exchangeService = new ExchangeService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = (String) req.getAttribute("from");
        String targetCurrencyCode = (String) req.getAttribute("to");
        BigDecimal amount = (BigDecimal) req.getAttribute("amount");

        try {
            CurrencyExchangeResponse currencyExchange = exchangeService.getCurrencyExchange(baseCurrencyCode, targetCurrencyCode, amount);

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    resp.getWriter(),
                    currencyExchange
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
