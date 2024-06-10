package com.openclassrooms.starterjwt.controllers;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@DisplayName("TeacherController Integration Tests")
public class TeacherControllerIT {
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

    @Tag("FindById")
    @Test
    @WithMockUser(username = "yoga@studio.com")
    @DisplayName("FindById should find the teacher and return response with http status = OK")
    public void findById_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Tag("FindById")
    @Test
    @WithMockUser(username = "yoga@studio.com")
    @DisplayName("FindById should not find the teacher when Id is unknown and return a response " +
            "with http status NOT_FOUND")
    public void findById_WhenUnknownId_ShouldReturnResponseNotFound() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", 666L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Tag("FindById")
    @Test
    @WithMockUser(username = "yoga@studio.com")
    @DisplayName("FindById should not find the teacher when the provided Id is invalid and returns a response " +
            "with http status = BAD_REQUEST")
    public void findById_WhenInvalidId_ShouldReturnResponseNotFound() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", "invalid_id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Tag("FindById")
    @Test
    @DisplayName("If user is not authenticated, FindById should return a response " +
            "with http status = UNAUTHORIZED")
    public void findById_WhenUserNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Tag("FindAll")
    @Test
    @DisplayName("FindAll should return all the teachers and response with http status = OK")
    @WithMockUser(username = "yoga@studio.com")
    public void findAll_ShouldReturnAllTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    };

    @Test
    @Tag("FindAll")
    @DisplayName("If user is not authenticated, FindAll should return a response with http status = UNAUTHORIZED")
    public void findAll_WhenUserNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/teacher")
                        .header("Authorization", "Bearer not_a_jwt"))
                .andExpect(status().isUnauthorized());
    }


}
