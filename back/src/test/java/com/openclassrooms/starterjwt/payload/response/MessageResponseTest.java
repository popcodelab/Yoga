package com.openclassrooms.starterjwt.payload.response;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Log4j2
public class MessageResponseTest {
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

    @Test
    @DisplayName("setMessage should set the message")
    public void setMessage_ShouldSetMessage() {
        String message = "this is a message";
        MessageResponse messageResponse = new MessageResponse("");
        messageResponse.setMessage(message);
        assertThat(messageResponse.getMessage()).isEqualTo(message);
    }
}
