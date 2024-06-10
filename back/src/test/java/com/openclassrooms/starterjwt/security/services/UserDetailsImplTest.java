package com.openclassrooms.starterjwt.security.services;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import java.util.HashSet;



@Log4j2
public class UserDetailsImplTest {
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

    @Tag("equals")
    @Test
    @DisplayName("Test if equals returns true when user ID are the same")
    public void equals_withSameId_shouldReturnTrue() {
        // Arrange
        UserDetailsImpl userDetailsImpl1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl userDetailsImpl2 = UserDetailsImpl.builder().id(1L).build();

        // Assert
        assertThat(userDetailsImpl1.equals(userDetailsImpl2)).isTrue();
    }

    @Tag("equals")
    @Test
    @DisplayName("Test if equals returns false  when user ID are not the same")
    public void equals_withDifferentId_shouldReturnFalse() {
        UserDetailsImpl userDetailsImpl1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl userDetailsImpl2 = UserDetailsImpl.builder().id(2L).build();

        assertThat(userDetailsImpl1.equals(userDetailsImpl2)).isFalse();
    }

}
