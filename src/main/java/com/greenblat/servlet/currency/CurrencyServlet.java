package com.greenblat.servlet.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenblat.dto.CurrencyDTO;
import com.greenblat.exception.ErrorResponseHandler;
import com.greenblat.exception.ResourceNotFoundException;
import com.greenblat.service.CurrencyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static com.greenblat.validation.CurrencyValidator.validateCurrencyCode;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService;
    private final ObjectMapper objectMapper;

    public CurrencyServlet() {
        this.objectMapper = new ObjectMapper();
        this.currencyService = new CurrencyService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currencyCode = req.getPathInfo().substring(1);

        if (!validateCurrencyCode(currencyCode)) {
            ErrorResponseHandler.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "The currency code is not in the address",
                    resp
            );
            return;
        }

        try {
            CurrencyDTO currency = currencyService.getCurrencyByCode(currencyCode);

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    resp.getWriter(),
                    currency
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
