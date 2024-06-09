package com.openclassrooms.starterjwt.payload.request;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class SignupRequestTest {
    private static Instant startedAt;
    private static Validator VALIDATOR;
    private SignupRequest signupRequest;

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
        signupRequest = new SignupRequest();
    }



    @AfterEach
    public void tearDown() {
        signupRequest = null;
    }

    @Test
    @DisplayName("Test getters and setters")
    public void testGettersAndSetters() {
        String email = "joh.doe@mail.com";
        String firstName = "John";
        String lastName = "Doe";
        String password = "password";

        signupRequest.setEmail(email);
        signupRequest.setFirstName(firstName);
        signupRequest.setLastName(lastName);
        signupRequest.setPassword(password);

        assertEquals(email, signupRequest.getEmail());
        assertEquals(firstName, signupRequest.getFirstName());
        assertEquals(lastName, signupRequest.getLastName());
        assertEquals(password, signupRequest.getPassword());
    }

    @Test
    @DisplayName("Email length should not exceed 50 characters")
    public void testEmailNotEmptyConstraint() {
        signupRequest.setEmail("joh.doe1234567890123456789012345678901234567890123456789012345678901234567890123456" +
                "789012345678901234567890123456789012345678901234567890123456789012345678901234567890@mail.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("123456");

        Set<ConstraintViolation<SignupRequest>> violations = VALIDATOR.validate(signupRequest);
        assertThat(violations.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Email format should be valid")
    public void testEmailFormatConstraint() {
        signupRequest.setEmail("joh.doe_mail.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("123456");

        Set<ConstraintViolation<SignupRequest>> violations = VALIDATOR.validate(signupRequest);
        assertThat(violations.size()).isGreaterThan(0);
    }

    @Tag("Firstname")
    @Test
    @DisplayName("Firstname length should be greater than 2")
    public void firstNameLengthShouldBeGreaterThan2() {
        // Arrange & Act
        signupRequest.setEmail("joh.doe@mail.com");
        signupRequest.setFirstName("Jo");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("123456");

        // Assert
        Set<ConstraintViolation<SignupRequest>> violations = VALIDATOR.validate(signupRequest);
        assertThat(violations.size()).isGreaterThan(0);
    }
    @Tag("Firstname")
    @Test
    @DisplayName("Firstname length should be greater than 21")
    public void firstNameLengthShouldBeLessThan21() {
        // Arrange & Act
        signupRequest.setEmail("joh.doe@mail.com");
        signupRequest.setFirstName("123456789012345678901");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("123456");

        // Assert
        Set<ConstraintViolation<SignupRequest>> violations = VALIDATOR.validate(signupRequest);
        assertThat(violations.size()).isGreaterThan(0);
    }


    @Tag("Lastname")
    @Test
    @DisplayName("Lastname length should be greater than 2")
    public void testLastnameShouldBeGreaterThan2() {
        // Arrange & Act
        signupRequest.setEmail("joh.doe@mail.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Do");
        signupRequest.setPassword("123456");

        // Assert
        Set<ConstraintViolation<SignupRequest>> violations = VALIDATOR.validate(signupRequest);
        assertThat(violations.size()).isGreaterThan(0);
    }

    @Tag("Lastname")
    @Test
    @DisplayName("Lastname length should be less than 21")
    public void testLastnameShouldBeLessThan21() {
        // Arrange & Act
        signupRequest.setEmail("joh.doe@mail.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("123456789012345678901");
        signupRequest.setPassword("123456");

        // Assert
        Set<ConstraintViolation<SignupRequest>> violations = VALIDATOR.validate(signupRequest);
        assertThat(violations.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Password length should be greater than 5")
    public void testPasswordSizeConstraint() {
        // Arrange & Act
        signupRequest.setEmail("joh.doe@mail.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("12345");

        // Assert
        Set<ConstraintViolation<SignupRequest>> violations = VALIDATOR.validate(signupRequest);
        assertThat(violations.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("SignupRequest should be valid")
    public void testValidSignupRequest() {
        // Arrange & Act
        signupRequest.setEmail("joh.doe@mail.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password");

        // Assert
        Set<ConstraintViolation<SignupRequest>> violations = VALIDATOR.validate(signupRequest);
        assertThat(violations.size()).isEqualTo(0);
    }

}
