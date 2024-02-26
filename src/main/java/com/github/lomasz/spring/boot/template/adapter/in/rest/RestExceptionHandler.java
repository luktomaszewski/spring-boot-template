package com.github.lomasz.spring.boot.template.adapter.in.rest;

import com.github.lomasz.spring.boot.template.application.domain.exception.BusinessException;
import com.github.lomasz.spring.boot.template.application.domain.exception.NotFoundException;
import com.github.lomasz.spring.boot.template.application.domain.exception.TechnicalException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
class RestExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String ERRORS = "errors";

    @ExceptionHandler(NotFoundException.class)
    ErrorResponse handleNotFoundException(NotFoundException ex) {
        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .title("Not Found")
                .property(TIMESTAMP, Instant.now())
                .build();
    }

    @ExceptionHandler(BusinessException.class)
    ErrorResponse handleBusinessException(BusinessException ex) {
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .title("Business Exception")
                .property(TIMESTAMP, Instant.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = Arrays.stream(ex.getDetailMessageArguments())
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(Strings::isNotBlank)
                .toList();

        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Invalid request content")
                .title("Business Exception")
                .property(TIMESTAMP, Instant.now())
                .property(ERRORS, errors)
                .build();
    }

    @ExceptionHandler(TechnicalException.class)
    ErrorResponse handleTechnicalException(TechnicalException ex) {
        log.error("Technical Exception: {}", ex.getMessage(), ex);
        return ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")
                .title("Internal Server Error")
                .property(TIMESTAMP, Instant.now())
                .build();
    }
}