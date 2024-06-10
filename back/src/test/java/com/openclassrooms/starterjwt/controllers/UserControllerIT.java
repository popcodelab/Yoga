package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@DisplayName("UserController Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIT {
    private static Instant startedAt;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

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

    @Test
    @Order(1)
    @Tag("FindById")
    @WithMockUser(username = "yoga@studio.com")
    @DisplayName("FindById should find the teacher and return response with http status = OK")
    public void findById_WithGoodId_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @Tag("FindById")
    @WithMockUser(username = "yoga@studio.com")
    @DisplayName("FindById should not find the user when Id is unknown and return a response " +
            "with http status NOT_FOUND")
    public void findById_WithUserNull_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 666L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    @Tag("FindById")
    @WithMockUser(username = "yoga@studio.com")
    @DisplayName("FindById should not find the user if the provided Id is invalid and return a response " +
            "with http status = BAD_REQUEST")
    public void findById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        String id = "invalid_id";
        mockMvc.perform(get("/api/user/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @Tag("FindById")
    @DisplayName("If user is not authenticated, FindById should return a response " +
            "with http status = UNAUTHORIZED")
    public void findById_WhenUserNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    @Tag("Delete")
    @DisplayName("Should not delete a user who does not exist and should return a response " +
            "with http status = NOT_FOUND")
    @WithMockUser(username = "yoga@studio.com")
    public void delete_ShouldReturnNotFoundResponse() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", 666L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    @Tag("Delete")
    @DisplayName("Delete should not find the user if the provided Id is invalid and return a response " +
            "with http status = BAD_REQUEST")
    @WithMockUser(username = "yoga@studio.com")
    public void delete_WhenInvalidId_ShouldReturnBadRequestResponse() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", "invalid_id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    @Tag("Delete")
    @DisplayName("If user is not authenticated, delete should return a response " +
            "with http status = UNAUTHORIZED")
    public void delete_WhenUserNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", 9L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    @Tag("Delete")
    @DisplayName("Should delete a user and return a response with http status = OK")
    @WithMockUser(username = "yoga@studio.com", roles = "ADMIN")
    public void delete_ShouldReturnOkResponse() throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("yoga@studio.com", "test!1234"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        mockMvc.perform(delete("/api/user/11")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
