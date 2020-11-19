package com.lomasz.spring.boot.template.handler;

import com.lomasz.spring.boot.template.exception.BusinessException;
import com.lomasz.spring.boot.template.exception.TechnicalException;
import com.lomasz.spring.boot.template.model.dto.ErrorDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler sut;

    @Test
    void handleBusinessException() {
        // given
        String exceptionMessage = "message";
        BusinessException businessException = new BusinessException(exceptionMessage);

        // when
        ResponseEntity<List<ErrorDto>> result = sut.handleBusinessException(businessException);

        // then
        assertThat(result.getStatusCode()).isEqualTo((HttpStatus.BAD_REQUEST));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().get(0).getMessage()).isEqualTo(exceptionMessage);
    }

    @Test
    void handleTechnicalException() {
        // given
        String exceptionMessage = "message";
        TechnicalException technicalException = new TechnicalException(exceptionMessage);

        // when
        ResponseEntity<List<ErrorDto>> result = sut.handleTechnicalException(technicalException);

        // then
        assertThat(result.getStatusCode()).isEqualTo((HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().get(0).getMessage()).isEqualTo(exceptionMessage);
    }

    @Test
    void handleMethodArgumentNotValidException() {
        // given
        String objectName = "object";
        String fieldName = "fieldName";
        String message = "message";
        FieldError fieldError = new FieldError(objectName, fieldName, message);

        BindingResult bindingResult = mock(BindingResult.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        // when
        ResponseEntity<List<ErrorDto>> result = sut.handleMethodArgumentNotValidException(exception);

        // then
        assertThat(result.getStatusCode()).isEqualTo((HttpStatus.BAD_REQUEST));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().get(0).getMessage()).isEqualTo("Wrong value in the field: " + fieldName);
        assertThat(result.getBody().get(0).getDetails()).isEqualTo(message);
    }

}
