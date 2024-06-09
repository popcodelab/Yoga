package com.openclassrooms.starterjwt.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Log4j2

public class AuthEntryPointJwtTest {
    private static Instant startedAt;

    @BeforeAll
    public static void initializeTestStartTime() {
        startedAt = Instant.now();
        log.info("Starts tests at {}", startedAt);
    }

    @AfterAll
    public static void displayTestDuration() {
        Instant endedAt = Instant.now();
        log.info("Test duration : {} ms", Duration.between(startedAt, endedAt).toMillis());
    }

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private AuthenticationException authenticationException;

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    @DisplayName("Should set unauthorized response  when authentication fails")
    void commence_ShouldHandleAuthException() throws IOException, ServletException {

        // Arrange
        String errorMessage = "Unauthorized error";
        when(authenticationException.getMessage()).thenReturn(errorMessage);
        when(httpServletRequest.getServletPath()).thenReturn("/api/test");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        authEntryPointJwt.commence(httpServletRequest, response, authenticationException);

        // Assert
        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());

        Map<String, Object> expectedResponseBody = new HashMap<>();
        expectedResponseBody.put("status", 401);
        expectedResponseBody.put("error", "Unauthorized");
        expectedResponseBody.put("message", errorMessage);
        expectedResponseBody.put("path", "/api/test");

        String expectedJson = new ObjectMapper().writeValueAsString(expectedResponseBody);

        assertEquals(expectedJson, response.getContentAsString());
    }
}
