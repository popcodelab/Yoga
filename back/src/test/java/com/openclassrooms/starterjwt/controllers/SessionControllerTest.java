package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

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
    SessionMapper sessionMapper;
    @Mock
    SessionService sessionService;
    SessionController sessionController;

    @BeforeEach
    public void setup() {
        sessionController = new SessionController(sessionService, sessionMapper);
    }

    @Test
    @DisplayName("Find a session by ID should return response OK")
    public void findById_ShouldReturnResponseOk() {
        // Arrange
        String strId = "1";
        Session mockSession = mock(Session.class);
        when(sessionService.getById(Long.valueOf(strId))).thenReturn(mockSession);

        // Act
        ResponseEntity<?> responseEntity = sessionController.findById(strId);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(sessionMapper.toDto(mockSession));

        verify(sessionService).getById(Long.valueOf(strId));
        verify(sessionMapper, times(2)).toDto(mockSession);
    }

    @Test
    @DisplayName("If cannot retrieve the session by its ID should return http status NOT_FOUND")
    public void findById_SessionNotFound_ShouldReturnNotFound() {
        // Arrange
        String strId = "1";
        when(sessionService.getById(Long.valueOf(strId))).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = sessionController.findById(strId);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("If an invalid ID is provided should return a http status BAD_REQUEST")
    public void findById_WithInvalidId_ShouldReturnBadRequest() {
        // Arrange
        String strId = "invalid_id";

        // Act
        ResponseEntity<?> responseEntity = sessionController.findById(strId);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Find all sessions should return response with http status = OK")
    public void findAll_ShouldReturnResponseOk() {
        // Arrange
        Session mockSession = mock(Session.class);
        List<Session> mockSessions = Arrays.asList(mockSession);
        when(sessionService.findAll()).thenReturn(mockSessions);

        // Act
        ResponseEntity<?> responseEntity = sessionController.findAll();

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(sessionMapper.toDto(mockSessions));
        verify(sessionService).findAll();
    }

    @Test
    @DisplayName("Create session should return response with http status = OK")
    public void create_ShouldReturnResponseOk() {
        // Arrange
        SessionDto sessionDto = new SessionDto(
                1L,
                "Session test",
                new Date(),
                1L,
                "Description of the session",
                Arrays.asList(1L,2L),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Session mockSession = mock(Session.class);
        when(sessionService.create(sessionMapper.toEntity(sessionDto))).thenReturn(mockSession);

        // Act
        ResponseEntity<?> responseEntity = sessionController.create(sessionDto);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(sessionMapper.toDto(mockSession));

        verify(sessionService).create(sessionMapper.toEntity(sessionDto));
        verify(sessionMapper, times(2)).toDto(mockSession);
    }

    @Test
    @DisplayName("Update a session should return response OK")
    public void update_ShouldReturnResponseOk() {
        // Arrange
        String strId = "1";
        Session mockSession = mock(Session.class);
        SessionDto sessionDto = new SessionDto(
                1L,
                "Update session test",
                new Date(),
                1L,
                "Description of the updated session",
                Arrays.asList(1L, 2L),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(sessionService.update(Long.parseLong(strId), sessionMapper.toEntity(sessionDto))).thenReturn(mockSession);

        // Act
        ResponseEntity<?> responseEntity = sessionController.update(strId, sessionDto);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(sessionMapper.toDto(mockSession));
        verify(sessionService).update(Long.parseLong(strId), sessionMapper.toEntity(sessionDto));
    }



    @Test
    @DisplayName("Delete a session  should return response with http status = OK")
    public void delete_ShouldReturnResponseOk() {
        // Arrange
        String strId = "1";
        Session mockSession = mock(Session.class);
        when(sessionService.getById(Long.valueOf(strId))).thenReturn(mockSession);

        // Act
        ResponseEntity<?> responseEntity = sessionController.save(strId);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService).delete(Long.valueOf(strId));
    }

    @Test
    @DisplayName("Delete session with an invalid ID should return a response with http status = BAD_REQUEST")
    public void delete_ShouldReturnResponseBadRequest() {
        // Arrange
        String strId = "invalid_id";

        // Act
        ResponseEntity<?> responseEntity = sessionController.save(strId);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(sessionService);
    }

    @Test
    @DisplayName("Participate to a session should return response with a http status = OK")
    public void participate_ShouldReturnResponseOk() {
        // Act
        ResponseEntity<?> responseEntity = sessionController.participate(String.valueOf(1L), String.valueOf(1L));

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService).participate(Long.parseLong("1"), Long.parseLong("1"));
    }


    @Test
    @DisplayName("Unparticipate to a session should return response with http status = OK")
    public void noLongerParticipate_InSession_ShouldReturnResponseOk() {
        // Act
        ResponseEntity<?> responseEntity = sessionController.noLongerParticipate(String.valueOf(1L), String.valueOf(1L));

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService).noLongerParticipate(Long.parseLong("1"), Long.parseLong("1"));
    }


}
