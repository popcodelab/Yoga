package com.openclassrooms.starterjwt.services;


import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    private static Instant startedAt;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

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

    @BeforeEach
    public void setup() {
        sessionService = new SessionService(sessionRepository, userRepository);
    }

    @AfterEach
    void tearDown() {
        sessionService = null;
    }

    @Tag("CRUD")
    @Test
    @DisplayName("Create session")
    public void create_ShouldSaveSession() {
        // Arrange
        Session session = new Session();
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // Act
        Session createdSession = sessionService.create(new Session());

        // Assert
        assertNotNull(createdSession);
        verify(sessionRepository).save(any(Session.class));
    }

    @Tag("CRUD")
    @Test
    @DisplayName("Delete session")
    public void delete_ShouldDeleteSession() {
        // Arrange
        Long sessionId = 1L;
        doNothing().when(sessionRepository).deleteById(sessionId);

        // Act
        sessionService.delete(sessionId);

        // Assert
        verify(sessionRepository).deleteById(sessionId);
        assertThat(sessionRepository.existsById(1L)).isFalse();
    }

    @Tag("CRUD")
    @Test
    @DisplayName("Find all sessions")
    public void findAll_ShouldReturnAllSessions() {
        // Arrange
        List<Session> sessions = Arrays.asList(new Session(), new Session());
        when(sessionRepository.findAll()).thenReturn(sessions);

        // Act
        List<Session> result = sessionService.findAll();

        // Assert
        assertEquals(sessions, result);
        verify(sessionRepository).findAll();
    }

    @Tag("CRUD")
    @Test
    @DisplayName("Find session by its ID")
    public void find_ShouldReturnSessionById() {
        // Arrange
        Session seekedSession = new Session();
        Optional<Session> sessionOptional = Optional.of(seekedSession);
        Long sessionId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(sessionOptional);

        // Act
        Session session = sessionService.getById(sessionId);

        // Assert
        verify(sessionRepository).findById(sessionId);
        assertEquals(seekedSession, session);
    }


    @Tag("CRUD")
    @Test
    @DisplayName("Update a session")
    void update_ShouldUpdateSession() {
        // Arrange
        Session session = new Session();
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // Act
        Session updatedSession = sessionService.update(1L, session);

        // Assert
        assertNotNull(updatedSession);
        assertEquals(1L, updatedSession.getId());
        verify(sessionRepository).save(session);
    }

    @Tag("Participation")
    @Test
    @DisplayName("If user and session are found, the user can be added to the session")
    public void participate_WhenSessionAndUserFound_ShouldAddUserToSession() {
        // Arrange
        Long sessionId = 1L;
        Long userId = 1L;
        Session session = new Session();
        session.setUsers(new ArrayList<>());
        User user = new User();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        sessionService.participate(sessionId, userId);

        // Assert
        assertTrue(session.getUsers().contains(user));
        verify(sessionRepository).save(session);
    }

    @Tag("Participation")
    @Test
    @DisplayName("If user and session are found, the user can be removed from the session")
    public void unParticipate_WhenSessionAndUserFound_ShouldRemoveUserFromSession() {
        // Arrange
        Long sessionId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Session session = new Session();
        session.setUsers(new ArrayList<>(Collections.singletonList(user)));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // Act
        sessionService.noLongerParticipate(sessionId, userId);

        // Assert
        assertFalse(session.getUsers().contains(user));
        verify(sessionRepository).save(session);
    }

    @Tag("Participation")
    @Test
    @DisplayName("Participate should return Bad Request Exception if the user already involves to the session")
    public void participate_WithAlreadyParticipate_ShouldReturnBadRequestException() {
        // Arrange
        Long id = 1L;
        Long userId = 1L;
        User mockUser = mock(User.class);
        Session mockSession = mock(Session.class);
        List<User> users = new ArrayList<>();
        users.add(mockUser);

        // Act
        when(mockUser.getId()).thenReturn(userId);
        when(sessionRepository.findById(id)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockSession.getUsers()).thenReturn(users);

        // Assert
        assertThatThrownBy(() -> {
            sessionService.participate(id, userId);
        }).isInstanceOf(BadRequestException.class);
        verify(sessionRepository).findById(id);
        verify(userRepository).findById(userId);
    }

    @Tag("Participation")
    @Test
    @DisplayName("If the user is not involving to the session, call unParticipate should throw BadRequestException")
    public void unParticipate_WithUserNotInSession_ShouldThrowBadRequestException() {
        // Arrange
        Long id = 1L;
        Long userId = 1L;
        Session mockSession = mock(Session.class);
        List<User> mockUsers = new ArrayList<>();

        // Act
        when(sessionRepository.findById(id)).thenReturn(Optional.of(mockSession));
        when(mockSession.getUsers()).thenReturn(mockUsers);

        // Assert
        assertThatThrownBy(() -> {
            sessionService.noLongerParticipate(id, userId);
        }).isInstanceOf(BadRequestException.class);
    }
}
