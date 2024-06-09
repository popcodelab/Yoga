package com.openclassrooms.starterjwt.payload.request;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;

@Log4j2
public class LoginRequestTest {
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

    private Validator validator;

   @BeforeEach
   public void setup(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Getters and setters test")
    public void testGettersAndSetters() {
       // Arrange
        String email = "john.doe@mail.com";
        String password = "password";

        // Act
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        // Assert
        assertEquals(email, loginRequest.getEmail());
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    @DisplayName("Email cannot be empty")
    public void testEmailNotEmptyConstraint() {
       // Arrange & Act
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("");
        loginRequest.setPassword("password");

        // Assert
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Password cannot be empty")
    public void testPasswordNotEmptyConstraint() {
        // Arrange & Act
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@mail.com");
        loginRequest.setPassword("");

        // Assert
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

}
