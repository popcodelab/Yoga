package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

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
    AuthenticationManager authenticationManager;
    @Mock
    JwtUtils jwtUtils;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    AuthController authController;

    @Test
    @DisplayName("Register user with valid credentials should succeed")
    public void registerUser_withValidCredentials_ShouldSucceed() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("john.doe@mail.com");
        signupRequest.setPassword("123456");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        User dummyUser = mock(User.class);

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("passwordEncode");
        when(userRepository.save(any())).thenReturn(dummyUser);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(messageResponse.getMessage()).isEqualTo("User registered successfully!");

        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("Authenticate user with valid credentials should succeed")
    public void authenticateUser_withValidCredentials_ShouldSucceed() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@mail.com");
        loginRequest.setPassword("123456");
        Authentication mockAuth = mock(Authentication.class);
        UserDetailsImpl mockUserDetails = mock(UserDetailsImpl.class);
        User dummyUser = mock(User.class);

        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        when(jwtUtils.generateJwtToken(any())).thenReturn("mockToken");
        when(mockAuth.getPrincipal()).thenReturn(mockUserDetails);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(dummyUser));
        when(dummyUser.isAdmin()).thenReturn(false);
        when(mockUserDetails.getUsername()).thenReturn("john.doe@mail.com");
        when(mockUserDetails.getId()).thenReturn(1L);

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse.getToken()).isNotNull();
        assertThat(jwtResponse.getUsername()).isEqualTo(loginRequest.getEmail());
        assertThat(jwtResponse.getId()).isEqualTo(mockUserDetails.getId());
        assertThat(jwtResponse.getLastName()).isEqualTo(mockUserDetails.getLastName());
        assertThat(jwtResponse.getFirstName()).isEqualTo(mockUserDetails.getFirstName());

        // Verify isAdmin
        verify(userRepository).findByEmail("john.doe@mail.com");
        assertFalse(dummyUser.isAdmin());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail(anyString());
        verify(jwtUtils).generateJwtToken(any());
    }

    @Test
    @DisplayName("Authenticate user with wrong credentials should throw BadCredentialsException")
    public void authenticateUser_withInvalidCredentials_ShouldThrowException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrongemailgmail.com");
        loginRequest.setPassword("wrongpassword");

        // Act
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Assert
        Exception exception = assertThrows(BadCredentialsException.class, () ->  {
            authController.authenticateUser(loginRequest);
        });
        assertThat(exception).isInstanceOf(BadCredentialsException.class);
        assertThat(exception.getMessage()).isEqualTo("Invalid credentials");
        verifyNoInteractions(jwtUtils, userRepository);
    }


    @Test
    @DisplayName("Register user with existing email should fail")
    public void registerUser_withExistingEmail_ShouldFail() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("john.doe@mail.com");
        signupRequest.setPassword("123456");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(messageResponse.getMessage()).isEqualTo("Error: Email is already taken!");
    }
}
