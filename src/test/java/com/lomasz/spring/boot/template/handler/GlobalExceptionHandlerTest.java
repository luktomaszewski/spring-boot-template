package com.lomasz.spring.boot.template.handler;

import com.lomasz.spring.boot.template.exception.BusinessException;
import com.lomasz.spring.boot.template.exception.TechnicalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
        ResponseEntity result = globalExceptionHandler.handleBusinessException(businessException);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void handleTechnicalException() {
        // given
        String exceptionMessage = "message";
        TechnicalException technicalException = new TechnicalException(exceptionMessage);

        // when
        ResponseEntity result = globalExceptionHandler.handleTechnicalException(technicalException);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
