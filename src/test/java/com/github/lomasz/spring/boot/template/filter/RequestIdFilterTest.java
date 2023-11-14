package com.github.lomasz.spring.boot.template.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestIdFilterTest {

    private static final String X_REQUEST_ID = "X-Request-ID";

    @Mock
    private FilterChain filterChain;

    private RequestIdFilter sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new RequestIdFilter();
    }

    @Test
    void shouldGenerateRequestIdIfAbsent() throws IOException, ServletException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        sut.doFilterInternal(request, response, filterChain);

        // then
        assertNotNull(response.getHeader(X_REQUEST_ID));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
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
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldSetRequestIdInMDC() throws IOException, ServletException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        sut.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(response.getHeader(X_REQUEST_ID));
        assertTrue(MDC.getCopyOfContextMap().isEmpty() || !MDC.getCopyOfContextMap().containsKey(X_REQUEST_ID));

        // Clean up
        MDC.clear();
    }
}
