package com.openclassrooms.starterjwt.controllers;


import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private static Instant startedAt;

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserService userService;
    private UserController userController;

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
        this.userController = new UserController(userService, userMapper);
    }

    @AfterEach
    public void tearDown() {
        this.userController = null;
    }

    @Test
    @DisplayName("Find user by its ID should return response with user details and http status = OK")
    public void findById_ShouldReturnUserDetailsAndResponseOk() {
        // Arrange
        String strId = "1";
        User mockUser = mock(User.class);
        when(userService.findById(Long.valueOf(strId))).thenReturn(mockUser);

        // Act
        ResponseEntity<?> response = userController.findById(strId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userMapper.toDto(mockUser));

        verify(userService).findById(Long.valueOf(strId));
        verify(userMapper, times(2)).toDto(mockUser);
    }

    @Test
    @DisplayName("Find user by its ID, if not found, it should return response with http status = NOT_FOUND")
    public void findById_WithUnknownId_ShouldReturnNotFound() {
        // Arrange
        String strId = "1";
        when(userService.findById(Long.valueOf(strId))).thenReturn(null);

        // Act
        ResponseEntity<?> response = userController.findById(strId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(userService).findById(Long.valueOf(strId));
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Find user by its ID, if invalid ID , it should return response with http status = BAD_REQUEST")
    void findById_WithInvalidId_ShouldReturnBadRequest() {
        // Act
        ResponseEntity<?> response = userController.findById("invalid_id");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(userService, userMapper);
    }

    @Test
    @DisplayName("Delete user by its ID with valid user & ID should returns response with http status OK")
    public void save_ShouldReturnsOk() {
        // Arrange
        String strId = "1";
        User mockUser = mock(User.class);
        UserDetails mockUserDetails = mock(UserDetails.class);
        Authentication mockAuthentication = mock(Authentication.class);

        when(userService.findById(Long.valueOf(strId))).thenReturn(mockUser);
        when(mockUser.getEmail()).thenReturn("john.doe@mail.com");
        when(mockUserDetails.getUsername()).thenReturn("john.doe@mail.com");
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        // Act
        ResponseEntity<?> response = userController.save(strId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).delete(Long.parseLong(strId));
    }

    @Test
    @DisplayName("Delete user by its ID with invalid ID format returns response with http status BAD_REQUEST")
    public void save_WithInvalidIdFormat_ShouldReturnsBadRequest() {
        // Act
        ResponseEntity<?> response = userController.save("invalid_id");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Delete user by its ID when unknown ID should return response with http status NOT_FOUND")
    public void save_WithUnknownId_ShouldReturnsNotFound() {
        // Act
        String strId = "1";
        when(userService.findById(Long.valueOf(strId))).thenReturn(null);

        // Arrange
        ResponseEntity<?> response = userController.save(strId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Delete user by its ID with username different of email,  should return response with http status UNAUTHORIZED")
    public void save_DifferentUserEmailAndUsername_ShouldReturnUnauthorized() {
        // Arrange
        String strId = "1";
        User mockUser = mock(User.class);
        UserDetails mockUserDetails = mock(UserDetails.class);
        Authentication mockAuthentication = mock(Authentication.class);

        when(userService.findById(Long.valueOf(strId))).thenReturn(mockUser);
        when(mockUserDetails.getUsername()).thenReturn("username1");
        when(mockUser.getEmail()).thenReturn("username2");
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        // Act
        ResponseEntity<?> response = userController.save(strId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
