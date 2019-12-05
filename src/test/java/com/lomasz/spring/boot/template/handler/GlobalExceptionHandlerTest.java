package com.lomasz.spring.boot.template.handler;

import com.lomasz.spring.boot.template.exception.BusinessException;
import com.lomasz.spring.boot.template.exception.TechnicalException;
import com.lomasz.spring.boot.template.model.dto.ErrorDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleBusinessException() {
        // given
        String exceptionMessage = "message";
        BusinessException businessException = new BusinessException(exceptionMessage);

        // when
        ResponseEntity<ErrorDto> result = globalExceptionHandler.handleBusinessException(businessException);

        // then
        assertThat(result.getStatusCode()).isEqualTo((HttpStatus.BAD_REQUEST));
        assertThat(Objects.requireNonNull(result.getBody()).getMessage()).isEqualTo(exceptionMessage);
    }

    @Test
    void handleTechnicalException() {
        // given
        String exceptionMessage = "message";
        TechnicalException technicalException = new TechnicalException(exceptionMessage);

        // when
        ResponseEntity<ErrorDto> result = globalExceptionHandler.handleTechnicalException(technicalException);

        // then
        assertThat(result.getStatusCode()).isEqualTo((HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(Objects.requireNonNull(result.getBody()).getMessage()).isEqualTo(exceptionMessage);
    }

}
