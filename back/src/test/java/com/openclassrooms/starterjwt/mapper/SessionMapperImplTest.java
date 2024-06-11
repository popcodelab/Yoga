package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
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
@DisplayName("SessionMapperImpl unit tests")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SessionMapperImplTest {
    private static Instant startedAt;
    private Teacher teacher;
    private User user;
    @Mock
    private UserService userService;
    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private SessionMapper sessionMapper = Mappers.getMapper(SessionMapper.class);

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
    public void setup() throws ParseException {
        teacher = Teacher.builder()
                .id(1L)
                .build();

        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@mail.com")
                .password("password")
                .build();
    }

    @Test
    @Tag("ToEntity")
    @DisplayName("The mapper should convert a Session Dto to a Session entity")
    public void toEntity_ShouldReturnAnEntity() {
        // Arrange
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Description session");
        sessionDto.setTeacher_id(teacher.getId());
        sessionDto.setUsers(Arrays.asList(user.getId()));

        when(teacherService.findById(teacher.getId())).thenReturn(teacher);
        when(userService.findById(user.getId())).thenReturn(user);

        // Act
        Session session = sessionMapper.toEntity(sessionDto);

        // Assert
        assertNotNull(session);
        assertEquals(sessionDto.getId(), session.getId());
        assertEquals(sessionDto.getDate(), session.getDate());
        assertEquals(sessionDto.getName(), session.getName());
        assertEquals(sessionDto.getDescription(), session.getDescription());
        assertEquals(sessionDto.getTeacher_id(), session.getTeacher().getId());
        assertEquals(sessionDto.getUsers().size(), session.getUsers().size());
    }

    @Test
    @Tag("ToEntity")
    @DisplayName("toEntity should return null Session entity when convert a null Session Dto")
    public void toEntity_ShouldReturnNull() {
        // Arrange
        SessionDto sessionDto = null;

        // Act
        Session session = sessionMapper.toEntity(sessionDto);

        // Assert
        assertNull(session);
    }



    @Test
    @Tag("ToDto")
    @DisplayName("The mapper should convert a Session entity to a Session Dto")
    public void toDto_ShouldReturnDto() {
        // Arrange
        Session session = Session.builder()
                .id(1L)
                .date(new Date())
                .description("Session description")
                .name("Session")
                .teacher(teacher)
                .users(Arrays.asList(user))
                .build();

        // Act
        SessionDto dto = sessionMapper.toDto(session);

        // Assert
        assertNotNull(dto);
        assertEquals(session.getId(), dto.getId());
        assertEquals(session.getDescription(), dto.getDescription());
        assertEquals(session.getTeacher().getId(), dto.getId());
        assertEquals(session.getUsers().size(), dto.getUsers().size());
    }

    @Test
    @Tag("ToDto")
    @DisplayName("toDto should return null Session Dto when convert a null Session entity")
    public void toDto_ShouldReturnNull() {
        // Arrange
        Session session = null;

        // Act
        SessionDto testSessionDto = sessionMapper.toDto(session);

        // Assert
        assertNull(testSessionDto);
    }


}
