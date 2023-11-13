package com.lomasz.spring.boot.template.handler;

import com.lomasz.spring.boot.template.exception.BusinessException;
import com.lomasz.spring.boot.template.exception.TechnicalException;
import com.lomasz.spring.boot.template.model.dto.ErrorDto;
import java.util.Collections;
import java.util.List;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@CommonsLog
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorDto>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        List<ErrorDto> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorDto("Wrong value in the field: " + error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest()
                .body(errors);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<List<ErrorDto>> handleBusinessException(BusinessException exception) {
        log.error("Business Exception: " + exception.getMessage(), exception);
        return ResponseEntity.badRequest()
                .body(Collections.singletonList(new ErrorDto(exception.getMessage())));
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<List<ErrorDto>> handleTechnicalException(TechnicalException exception) {
        log.error("Technical Exception: " + exception.getMessage(), exception);
        return ResponseEntity.internalServerError()
                .body(Collections.singletonList(new ErrorDto(exception.getMessage())));
    }
}
