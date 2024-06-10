package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.SessionService;
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
import java.util.Date;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@DisplayName("SessionController Integration Tests")
public class SessionControllerIT {
    private static Instant startedAt;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionService sessionService;

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
    @DisplayName("FindById should find the session and return response with http status = OK")
    public void findById_ShouldReturnResponseOk() throws Exception {
        Long idToFind = 1L;
        mockMvc.perform(get("/api/session/{id}", idToFind)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idToFind))
                .andExpect(jsonPath("$.name").value("Hatha yoga course"));
    }

    @Tag("FindById")
    @Test
    @WithMockUser(username = "yoga@studio.com")
    @DisplayName("FindById should not find the session if the provided Id is unknown")
    public void findById_WhenUnknownId_ShouldReturnResponseNotFound() throws Exception {
        Long id = 666L;
        mockMvc.perform(get("/api/session/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    };

    @Tag("FindById")
    @Test
    @DisplayName("If user is not authenticated, FindById should return a response with http status = UNAUTHORIZED")
    public void findById_WhenUserNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/session/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Tag("FindAll")
    @Test
    @DisplayName("FindAll should return all the sessions and response with http status = OK")
    @WithMockUser(username = "yoga@studio.com")
    public void findAll_ShouldReturnAllSessions() throws Exception {
        mockMvc.perform(get("/api/session")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    };

    @Test
    @Tag("FindAll")
    @DisplayName("If user is not authenticated, FindAll should return a response with http status = UNAUTHORIZED")
    public void findAll_WhenUserNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/session/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should create a session and response with http status = OK")
    @WithMockUser(username = "yoga@studio.com")
    public void create_ShouldReturnOk() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("New session integration test");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setDescription("Description new session integration test");
        ObjectMapper objectMapper = new ObjectMapper();
        String sessionDtoToJson = objectMapper.writeValueAsString(sessionDto);

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessionDtoToJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New session integration test"))
                .andExpect(jsonPath("$.description").value("Description new session integration test"));
    }

    @Tag("Delete")
    @Test
    @WithMockUser(username = "yoga@studio.com")
    @DisplayName("DeleteById should delete the session and return response with http status = OK")
    public void deleteById_ShouldReturnResponseNoContent() throws Exception {
        Session session = Session.builder()
                .name("Session to be deleted")
                .date(new Date())
                .description("Session description")
                .teacher(Teacher.builder().id(1L).firstName("john").lastName("Doe").build())
                .build();
        session = sessionService.create(session);
        mockMvc.perform(delete("/api/session/{id}", session.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Tag("Delete")
    @Test
    @DisplayName("Should not delete an unknown sessions")
    @WithMockUser(username = "yoga@studio.com")
    public void delete_WithUnknownId_ShouldReturnNotFoundResponse() throws Exception {
        Long id = 666L;
        mockMvc.perform(delete("/api/session/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Tag("Update")
    @Test
    @DisplayName("Should update an existing session and return a response with http status = OK")
    @WithMockUser(username = "yoga@studio.com")
    public void update_ShouldReturnOk() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Updated session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setDescription("Updated session description");
        ObjectMapper objectMapper = new ObjectMapper();
        String sessionDtoToJson = objectMapper.writeValueAsString(sessionDto);

        mockMvc.perform(put("/api/session/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessionDtoToJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sessionDto.getName()))
                .andExpect(jsonPath("$.description").value(sessionDto.getDescription()));
    }

    @Tag("Update")
    @Test
    @DisplayName("Should not update a session if the id is malformed")
    @WithMockUser(username = "yoga@studio.com")
    public void update_ShouldReturnBadRequestResponse() throws Exception {
        String sessionId = "invalid_id";
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Session to update");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setDescription("Session update fails if invalid id is provided");

        mockMvc.perform(put("/api/session/{id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    @Tag("Participate")
    @Test
    @DisplayName("it should participate to a session and return a response with http status = OK")
    @WithMockUser
    public void participate_ShouldReturnOkResponse() throws Exception {
        mockMvc.perform(post("/api/session/1/participate/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Tag("Participate")
    @Test
    @DisplayName("Should not participate if user is unknown and returns a response with http status = NOT_FOUND")
    @WithMockUser
    public void participate_WithUnknownUser_ShouldReturnNotFoundResponse() throws Exception {
        Long userId = 666L;
        mockMvc.perform(post("/api/session/1/participate/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Tag("Participate")
    @DisplayName("Should not participate and returns a response with http status = BAD_REQUEST")
    @WithMockUser
    public void participate_WithUserAlreadyInvolving_ShouldReturnBadRequestResponse() throws Exception {
        mockMvc.perform(post("/api/session/1/participate/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}
