package com.greenblat.exception;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

public class ErrorResponseHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void sendError(Integer statusCode,
                                   String message,
                                   HttpServletResponse response) {
        try {
            response.setStatus(statusCode);
            MAPPER.writeValue(
                    response.getWriter(),
                    buildErrorResponse(message)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ErrorDetails buildErrorResponse(String message) {
        return new ErrorDetails(
                message,
                String.valueOf(LocalDateTime.now())
        );
    }
}
