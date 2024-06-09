package com.openclassrooms.starterjwt.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    private static Instant startedAt;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    private JwtUtils jwtUtils;
    private final String jwtSecretKey = "jwtSecretKey";

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

    @BeforeEach
    public void setup() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecretKey);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000);
    }

    @Test
    @DisplayName("The generated JWT token should be validated")
    public void validateJwtToken_ShouldReturnValidToken() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("john.doe@mail.com");
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String generatedJwtToken = jwtUtils.generateJwtToken(authentication);

        // Act
        boolean isJwtTokenValid = jwtUtils.validateJwtToken(generatedJwtToken);

        // Assert
        assertNotNull(isJwtTokenValid);
        assertTrue(isJwtTokenValid);
    }

    @Test
    @DisplayName("Should return the username using the Jwt Token")
    public void getUserNameFromJwtToken_ShouldReturnUserName(){
        // Arrange
        when(userDetails.getUsername()).thenReturn("john.doe@mail.com");
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String generatedJwtToken = jwtUtils.generateJwtToken(authentication);

        // Act
        String username = jwtUtils.getUserNameFromJwtToken(generatedJwtToken);

        // Assert
        assertEquals("john.doe@mail.com", username);
    }

    @Test
    @DisplayName("An expired JWT token should return an invalid token")
    public void validateJwtToken_ExpiredToken_ShouldReturnInvalidToken() {
        String jwt = Jwts.builder()
                .setSubject("john.doe@mail.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .setExpiration(new Date(System.currentTimeMillis() -60000)) // 1 minute ago
                .signWith(SignatureAlgorithm.HS512, "TEST_JWT_TOKEN")
                .compact();

        assertFalse(jwtUtils.validateJwtToken(jwt));
    }

    @Test
    @DisplayName("JWT token with a wrong signature should return an invalid token")
    public void validateJwtToken_InvalidTokenSignature_ShouldReturnInvalidToken() {
        String jwt = Jwts.builder()
                .setSubject("john.doe@mail.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +60000)) // 1 minute after
                .signWith(SignatureAlgorithm.HS512,"wrongJwtSecretKey")
                .compact();

        assertFalse(jwtUtils.validateJwtToken(jwt));
    }

    @Tag("Exceptions")
    @Test
    @DisplayName("An invalid JWT token should throw a MalformedJwtException")
    public void validateJwtToken_InvalidToken_ShouldThrowMalformedJwtException() {
        // Arrange
        String invalidToken = "invalidToken";
        JwtUtils jwtUtilsMock = Mockito.mock(JwtUtils.class);

        // Act
        doThrow(new MalformedJwtException("Invalid JWT token")).when(jwtUtilsMock).validateJwtToken(invalidToken);

        // Assert
        MalformedJwtException malformedJwtException = assertThrows(MalformedJwtException.class, () ->
                jwtUtilsMock.validateJwtToken(invalidToken));
        assertTrue(malformedJwtException.getMessage().contains("Invalid JWT token"));
    }

    @Tag("Exceptions")
    @Test
    @DisplayName("A wrong token should throw an UnsupportedJwtException")
    public void validateJwtToken_InvalidToken_ShouldThrowUnsupportedJwtException()  {
        // Arrange
        JwtUtils mockedJwtUtils = Mockito.mock(JwtUtils.class);
        String dummyJwtToken = "dummyJwtToken";

        // Act
        doThrow(new UnsupportedJwtException("Unsupported JWT token"))
                .when(mockedJwtUtils).validateJwtToken(dummyJwtToken);

        // Assert
        UnsupportedJwtException unsupportedJwtException = assertThrows(
                UnsupportedJwtException.class,
                () -> mockedJwtUtils.validateJwtToken(dummyJwtToken)
        );

        assertTrue(unsupportedJwtException.getMessage().contains("Unsupported JWT token"));
    }

    @Tag("Exceptions")
    @Test
    @DisplayName("Using an empty JWT token should throw an IllegalArgumentException")
    public void validateJwtToken_EmptyToken_ShouldThrowIllegalArgumentException() {
        // Arrange
        JwtUtils mockedJwtUtils = Mockito.mock(JwtUtils.class);
        String nullToken = null;

        // Act
        doThrow(new IllegalArgumentException("Empty token"))
                .when(mockedJwtUtils).validateJwtToken(nullToken);

        // Assert
        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> mockedJwtUtils.validateJwtToken(nullToken));

        assertTrue(illegalArgumentException.getMessage().contains("Empty token"));
    }

    @Test
    @DisplayName("An expired JWT token should throw an ExpiredJwtException")
    public void validateJwtToken_ExpiredToken_ShouldThrowExpiredJwtException() {
        // Arrange
        JwtUtils mockedJwtUtils = Mockito.mock(JwtUtils.class);
        String expiredToken = Jwts.builder()
                .setSubject("john.doe@mail.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, "jwtSecretKey")
                .compact();

        // Act

        doThrow(new ExpiredJwtException(null, null,"Jwt token has expired"))
                .when(mockedJwtUtils).validateJwtToken(expiredToken);

        // Assert
        ExpiredJwtException expiredJwtException = assertThrows(
                ExpiredJwtException.class,
                () -> mockedJwtUtils.validateJwtToken(expiredToken));

        assertTrue(expiredJwtException.getMessage().contains("Jwt token has expired"));
    }

}


