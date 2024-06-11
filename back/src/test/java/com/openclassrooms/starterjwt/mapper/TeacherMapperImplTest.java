package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Log4j2
@DisplayName("TeacherMapperImpl unit tests")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TeacherMapperImplTest {
    private static Instant startedAt;
    private final Long id = 1L;
    private final String firstName = "John";
    private final String lastName = "Doe";

    @InjectMocks
    private TeacherMapper teacherMapper = Mappers.getMapper(TeacherMapper.class);

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
    @Tag("ToEntity")
    @DisplayName("The mapper should convert a Teacher Dto to a Teacher entity")
    public void toEntity_ShouldReturnAnEntity() {
        // Arrange
        TeacherDto  teacherDto = new TeacherDto();
        teacherDto.setId(id);
        teacherDto.setFirstName(firstName);
        teacherDto.setLastName(lastName);

        // Act
        Teacher teacher = teacherMapper.toEntity(teacherDto);

        // Assert
        assertNotNull(teacher);
        assertEquals(teacher.getId(), teacherDto.getId());
        assertEquals(teacher.getFirstName(), teacherDto.getFirstName());
        assertEquals(teacher.getLastName(), teacherDto.getLastName());
    }

    @Test
    @Tag("ToEntity")
    @DisplayName("toEntity should return null Teacher entity when convert a null Teacher Dto")
    public void toEntity_ShouldReturnNull() {
        // Arrange
        TeacherDto teacherDto = null;

        // Act
        Teacher teacher = teacherMapper.toEntity(teacherDto);

        // Assert
        assertNull(teacher);
    }



    @Test
    @Tag("ToDto")
    @DisplayName("The mapper should convert a Teacher entity to a Teacher Dto")
    public void toDto_ShouldReturnDto() {
        // Arrange
        Teacher teacher = Teacher.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        // Act
        TeacherDto teacherDto = teacherMapper.toDto(teacher);

        // Assert
        assertNotNull(teacherDto);
        assertEquals(teacher.getId(), teacherDto.getId());
        assertEquals(teacher.getFirstName(), teacherDto.getFirstName());
        assertEquals(teacher.getLastName(), teacherDto.getLastName());
    }

    @Test
    @Tag("ToDto")
    @DisplayName("toDto should return null Teacher Dto when convert a null Teacher entity")
    public void toDto_ShouldReturnNull() {
        // Arrange
        Teacher teacher = null;

        // Act
        TeacherDto teacherDto = teacherMapper.toDto(teacher);

        // Assert
        assertNull(teacherDto);
    }


}
