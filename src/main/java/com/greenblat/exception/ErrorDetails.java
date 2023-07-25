package com.greenblat.exception;

import java.time.LocalDateTime;
import java.util.Date;

public record ErrorDetails(String message,
                           String errorTime) {
}
