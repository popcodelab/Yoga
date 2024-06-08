package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static Instant startedAt;

    @Mock
    private UserRepository userRepository;

    private UserService userService;

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
        userService = new UserService(userRepository);
    }

    @AfterEach
    void tearDown() {
        userService = null;
    }

    @Tag("CRUD")
    @Test
    @DisplayName("Delete user")
    public void delete_ShouldDeleteUser() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        // Act
        userService.delete(userId);

        // Assert
        verify(userRepository).deleteById(userId);
        assertThat(userRepository.existsById(1L)).isFalse();
    }

    @Tag("CRUD")
    @Test
    @DisplayName("Get user by its ID")
    public void findById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.findById(userId);

        // Assert
        assertEquals(user, foundUser);
        verify(userRepository).findById(userId);
    }

    @Tag("CRUD")
    @Test
    @DisplayName("Get user by its ID should return null if not found")
    public void findById_WhenUserDoesNotExist_ShouldReturnNull() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        User foundUser = userService.findById(userId);

        // Assert
        verify(userRepository).findById(userId);
        assertNull(foundUser);
    }

}
