package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Log4j2

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    private static Instant startedAt;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl userDetailsServiceImpl;

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
    @DisplayName("Calling loadUserByUsername should return user details when user has been found")
    public void loadUserByUsername_ShouldReturnUserDetails() {
        // Arrange
        String username = "john.doe@mail.com";
        User dummyUser = mock(User.class);

        when(dummyUser.getId()).thenReturn(1L);
        when(dummyUser.getEmail()).thenReturn(username);
        when(dummyUser.getLastName()).thenReturn("Doe");
        when(dummyUser.getFirstName()).thenReturn("John");
        when(dummyUser.getPassword()).thenReturn("password");
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(dummyUser));

        // Act
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo("password");
    }

    @Test
    @DisplayName("Calling loadUserByUsername should throw an exception when user has not been found")
    public void loadUserByUsername_UserNameNotFound_ShouldThrowException() {
        // Arrange
        String invalidUsername = "invalid@mail.com";

        // Act
        when(userRepository.findByEmail(invalidUsername)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> {
            userDetailsServiceImpl.loadUserByUsername(invalidUsername);
        }).isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User Not Found with email: " + invalidUsername);
    }

}
