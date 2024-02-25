package com.github.lomasz.spring.boot.template.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class RequestIdFilterTest {

    private static final String X_REQUEST_ID = "X-Request-ID";

    @Mock
    private FilterChain filterChain;

    private final RequestIdFilter sut = new RequestIdFilter();

    @Test
    @DisplayName("should: generate request id, when: absent")
    void shouldGenerateRequestIdIfAbsent() throws IOException, ServletException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        sut.doFilterInternal(request, response, filterChain);

        // then
        assertNotNull(response.getHeader(X_REQUEST_ID));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("should: you existing request id, when: provided")
    void shouldUseExistingRequestId() throws IOException, ServletException {
        // given
        String existingRequestId = UUID.randomUUID().toString();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(X_REQUEST_ID, existingRequestId);

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        sut.doFilterInternal(request, response, filterChain);

        // then
        assertEquals(existingRequestId, response.getHeader(X_REQUEST_ID));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("should: set request id in MDC")
    void shouldSetRequestIdInMDC() throws IOException, ServletException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        sut.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        assertNotNull(response.getHeader(X_REQUEST_ID));
        assertTrue(MDC.getCopyOfContextMap().isEmpty() || !MDC.getCopyOfContextMap().containsKey(X_REQUEST_ID));

        // Clean up
        MDC.clear();
    }
}
