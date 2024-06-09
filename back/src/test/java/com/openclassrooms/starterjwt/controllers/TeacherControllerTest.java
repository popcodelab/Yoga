package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {
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
    TeacherMapper teacherMapper;
    @Mock
    TeacherService teacherService;
    TeacherController teacherController;

    @BeforeEach
    public void setup() {
        this.teacherController = new TeacherController(teacherService, teacherMapper);
    }

    @AfterEach
    public void tearDown() {
        this.teacherController = null;
    }

    @Test
    @DisplayName("Find teacher by its ID should return response with http status = OK")
    public void findById_WithValidId_ShouldReturnResponseOkWithTeacher() {
        // Arrange
        String strId = "1";
        Teacher mockTeacher = mock(Teacher.class);
        when(teacherService.findById(Long.valueOf(strId))).thenReturn(mockTeacher);

        // Act
        ResponseEntity<?> response = teacherController.findById(strId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(teacherMapper.toDto(mockTeacher));

        verify(teacherService).findById(Long.valueOf(strId));
        verify(teacherMapper, times(2)).toDto(mockTeacher);
    }

    @Test
    @DisplayName("Find teacher by its ID, if not found, it should return response with http status = NOT_FOUND")
    public void findById_WithUnknownId_ShouldReturnNotFound() {
        // Arrange
        String strId = "1";
        when(teacherService.findById(Long.valueOf(strId))).thenReturn(null);

        // Act
        ResponseEntity<?> response = teacherController.findById(strId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verifyNoInteractions(teacherMapper);
    }

    @Test
    @DisplayName("Find teacher by its ID, if invalid ID , it should return response with http status = BAD_REQUEST")
    void findById_WithInvalidId_ShouldReturnBadRequest() {
        // Act
        ResponseEntity<?> response = teacherController.findById("invalid_id");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(teacherMapper, teacherService);
    }


    @Test
    @DisplayName("Get all teachers should return response with http status = OK")
    public void findAll_ShouldReturnResponseOk() {
        // Arrange
        Teacher mockTeacher = mock(Teacher.class);
        List<Teacher> teachers = Collections.singletonList(mockTeacher);
        when(teacherService.findAll()).thenReturn(teachers);

        // Act
        ResponseEntity<?> response = teacherController.findAll();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(teacherMapper.toDto(teachers));

        verify(teacherService).findAll();
        verify(teacherMapper, times(2)).toDto(teachers);
    }
}
