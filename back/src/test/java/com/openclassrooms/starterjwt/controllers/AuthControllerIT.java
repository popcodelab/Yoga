package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@DisplayName("AuthController Integration Tests")
public class AuthControllerIT {

    private static Instant startedAt;

    @Autowired
    private MockMvc mockMvc;

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

    @Tag("Register")
    @Test
    @DisplayName("Should register a new user and return a success message ")
    public void registerUser_shouldReturnSuccessMessage() throws Exception {
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail("john.doe@mail.com");
        signUpRequest.setPassword("password");
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");

        String signUpRequestJson = new ObjectMapper().writeValueAsString(signUpRequest);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Tag("Register")
    @Test
    @DisplayName("Should not register a user with an existing email a return an error message")
    public void registerUser_WithExistingEmail_ShouldReturnErrorMessage() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("yoga@studio.com");
        signupRequest.setFirstName("FirstName");
        signupRequest.setLastName("LastName");
        signupRequest.setPassword("password");

        String signUpRequestJson = new ObjectMapper().writeValueAsString(signupRequest);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));

    }


    @Tag("Login")
    @Test
    @DisplayName("Should authenticate the user if credentials are correct")
    public void authenticateUser_WhenCorrectCredentials_ShouldAuthenticate() throws Exception {
        // Given
        LoginRequest validLogin = new LoginRequest();
        validLogin.setEmail("yoga@studio.com");
        validLogin.setPassword("test!1234");
        String loginRequestJson = new ObjectMapper().writeValueAsString(validLogin);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("yoga@studio.com"))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Tag("Login")
    @Test
    @DisplayName("Should not authenticate the user if credentials are wrong")
    public void authenticateUser_WhenIncorrectCredentials_ShouldNotAuthenticate() throws Exception {
        LoginRequest invalidLogin = new LoginRequest();
        invalidLogin.setEmail("yoda@studio.com");
        invalidLogin.setPassword("test1234");
        String loginRequestJson = new ObjectMapper().writeValueAsString(invalidLogin);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isUnauthorized());
    }

}
