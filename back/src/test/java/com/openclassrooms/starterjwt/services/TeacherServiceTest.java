package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {
    private static Instant startedAt;

    @Mock
    private TeacherRepository teacherRepository;

    private TeacherService teacherService;

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
        teacherService = new TeacherService(teacherRepository);
    }

    @AfterEach
    void tearDown() {
        teacherService = null;
    }

    @Test
    @DisplayName("Find all teachers")
    public void findAll_ShouldReturnAllTeachers() {
        // Arrange
        List<Teacher> teachers = Arrays.asList(new Teacher(), new Teacher());
        when(teacherRepository.findAll()).thenReturn(teachers);

        // Act
        List<Teacher> result = teacherService.findAll();

        // Assert
        assertEquals(teachers, result);
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("Get teacher by its ID")
    public void findById_WhenTeacherExists_ShouldReturnTeacher() {
        // Arrange
        Long teacherId = 1L;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        // Act
        Teacher foundTeacher = teacherService.findById(teacherId);

        // Assert
        assertEquals(teacher, foundTeacher);
        verify(teacherRepository).findById(teacherId);
    }

    @Test
    @DisplayName("Get teacher by its ID should return null if not found")
    public void findById_WhenTeacherDoesNotExist_ShouldReturnNull() {
        // Arrange
        Long teacherId = 1L;
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        // Act
        Teacher foundTeacher = teacherService.findById(teacherId);

        // Assert
        assertNull(foundTeacher);
        verify(teacherRepository).findById(teacherId);
    }
}
