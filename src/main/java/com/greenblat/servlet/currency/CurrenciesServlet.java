package com.greenblat.servlet.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenblat.dto.CurrencyDTO;
import com.greenblat.exception.ErrorResponseHandler;
import com.greenblat.exception.ResourceAlreadyExistsException;
import com.greenblat.service.CurrencyService;
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

import static com.greenblat.validation.CurrencyValidator.validateCurrencyEntity;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService;
    private final ObjectMapper objectMapper;

    public CurrenciesServlet() {
        this.objectMapper = new ObjectMapper();
        this.currencyService = new CurrencyService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    resp.getWriter(),
                    currencies
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
        CurrencyDTO currencyDTO = objectMapper.readValue(
                JsonUtil.getJsonObject(req.getReader()),
                CurrencyDTO.class);

        if (!validateCurrencyEntity(currencyDTO)) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Required form field is missing",
                    resp
            );
            return;
        }

        try {
            CurrencyDTO savedCurrency = currencyService.saveCurrency(currencyDTO);

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    resp.getWriter(),
                    savedCurrency
            );
        } catch (ResourceAlreadyExistsException e) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_CONFLICT,
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
