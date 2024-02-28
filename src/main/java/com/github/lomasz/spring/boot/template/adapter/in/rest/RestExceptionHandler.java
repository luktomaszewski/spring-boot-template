package com.github.lomasz.spring.boot.template.adapter.in.rest;

import com.github.lomasz.spring.boot.template.application.domain.exception.BusinessException;
import com.github.lomasz.spring.boot.template.application.domain.exception.NotFoundException;
import com.github.lomasz.spring.boot.template.application.domain.exception.TechnicalException;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
class RestExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String ERRORS = "errors";
    private static final String REQUEST_ID = "requestId";

    private static final String X_REQUEST_ID_HEADER = "X-Request-ID";

    @ExceptionHandler(NotFoundException.class)
    ErrorResponse handle(NotFoundException ex) {
        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .title("Not Found")
                .property(TIMESTAMP, Instant.now())
                .build();
    }

    @ExceptionHandler(BusinessException.class)
    ErrorResponse handle(BusinessException ex) {
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .title("Business Exception")
                .property(TIMESTAMP, Instant.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ErrorResponse handle(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getFieldErrors().stream()
                .map(x -> "%s: %s".formatted(x.getField(), x.getDefaultMessage()))
                .toList();

        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Invalid request content")
                .title("Business Exception")
                .property(TIMESTAMP, Instant.now())
                .property(ERRORS, errors)
                .build();
    }

    @ExceptionHandler(TechnicalException.class)
    ErrorResponse handle(TechnicalException ex, WebRequest req) {
        log.error("Technical Exception: {}", ex.getMessage(), ex);
        return ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")
                .title("Internal Server Error")
                .property(REQUEST_ID, req.getHeader(X_REQUEST_ID_HEADER))
                .property(TIMESTAMP, Instant.now())
                .build();
    }
}
