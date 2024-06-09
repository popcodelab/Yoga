package com.openclassrooms.starterjwt.payload.request;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class LoginRequestTest {
    private static Instant startedAt;
    private static Validator VALIDATOR;
    private LoginRequest loginRequest;

    private static Validator createValidatorFactory() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeAll
    public static void initializeTestStartTime() {
        startedAt = Instant.now();
        VALIDATOR = createValidatorFactory();
        log.info("Starts tests at {}", startedAt);
    }

    @AfterAll
    public static void displayTestDuration() {
        Instant endedAt = Instant.now();
        log.info("Test duration : {} ms", Duration.between(startedAt, endedAt).toMillis());
    }

    @BeforeEach
    public void setUp() {
        loginRequest = new LoginRequest();
    }



    @AfterEach
    public void tearDown() {
        loginRequest = null;
    }

    @Test
    @DisplayName("Getters and setters test")
    public void testGettersAndSetters() {
       // Arrange
        String email = "john.doe@mail.com";
        String password = "password";

        // Act
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        // Assert
        assertEquals(email, loginRequest.getEmail());
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    @DisplayName("Email should not  be empty")
    public void testEmailNotEmptyConstraint() {
       // Arrange & Act
        loginRequest.setEmail("");
        loginRequest.setPassword("password");

        // Assert
        Set<ConstraintViolation<LoginRequest>> violations = VALIDATOR.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Password should not be empty")
    public void testPasswordNotEmptyConstraint() {
        // Arrange & Act
        loginRequest.setEmail("john.doe@mail.com");
        loginRequest.setPassword("");

        // Assert
        Set<ConstraintViolation<LoginRequest>> violations = VALIDATOR.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

}
