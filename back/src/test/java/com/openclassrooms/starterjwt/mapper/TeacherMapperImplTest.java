package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DisplayName("TeacherMapperImpl unit tests")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
    @Tag("ToEntity")
    @DisplayName("ToEntity should convert a Teacher Dto to a Teacher entity")
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
    @Order(2)
    @Tag("ToEntity")
    @DisplayName("ToEntity should return a List of Teacher entities")
    public void toEntity_ShouldReturnList() {
        // Arrange
        TeacherDto  teacherDto = new TeacherDto();
        teacherDto.setId(id);
        teacherDto.setFirstName(firstName);
        teacherDto.setLastName(lastName);
        List<TeacherDto> teacherDtoList = new ArrayList<>();
        teacherDtoList.add(teacherDto);

        // Act
        List<Teacher> teacherList = teacherMapper.toEntity(teacherDtoList);

        // Assert
        assertNotNull(teacherList);
        assertEquals(teacherList.get(0).getId(), teacherDtoList.get(0).getId());

    }

    @Test
    @Order(3)
    @Tag("ToEntity")
    @DisplayName("toEntity should return null Teacher entity when convert a null Teacher Dto")
    public void toEntity_ShouldReturnNull() {
        // Act
        Teacher teacher = teacherMapper.toEntity((TeacherDto) null);

        // Assert
        assertNull(teacher);
    }

    @Test
    @Order(4)
    @Tag("ToDto")
    @DisplayName("ToDto should convert a Teacher entity to a Teacher Dto")
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
    @Order(5)
    @Tag("ToDto")
    @DisplayName("ToDto should return to a List of Teacher Dtos")
    public void toDto_ShouldReturnList() {
        // Arrange
        Teacher teacher = Teacher.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        List<Teacher> teacherList = new ArrayList<>();
        teacherList.add(teacher);

        // Act
        List<TeacherDto> teacherDtoList = teacherMapper.toDto(teacherList);

        // Assert
        assertNotNull(teacherDtoList);
        assertEquals(teacherList.get(0).getId(), teacherDtoList.get(0).getId());
    }

    @Test
    @Order(6)
    @Tag("ToDto")
    @DisplayName("toDto should return null Teacher Dto when convert a null Teacher entity")
    public void toDto_ShouldReturnNull() {
        // Act
        TeacherDto teacherDto = teacherMapper.toDto((Teacher) null);

        // Assert
        assertNull(teacherDto);
    }
}
