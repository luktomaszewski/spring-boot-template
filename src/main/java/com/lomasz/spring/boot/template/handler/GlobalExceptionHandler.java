package com.lomasz.spring.boot.template.handler;

import com.lomasz.spring.boot.template.exception.BusinessException;
import com.lomasz.spring.boot.template.exception.TechnicalException;
import com.lomasz.spring.boot.template.model.dto.ErrorDto;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@CommonsLog
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDto> handleBusinessException(BusinessException exception) {
        log.error("Business Exception: " + exception.getMessage(), exception);
        return new ResponseEntity<>(new ErrorDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ErrorDto> handleTechnicalException(TechnicalException exception) {
        log.error("Technical Exception: " + exception.getMessage(), exception);
        return new ResponseEntity<>(new ErrorDto(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

