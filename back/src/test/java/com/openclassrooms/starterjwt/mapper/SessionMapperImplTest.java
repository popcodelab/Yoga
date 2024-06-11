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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Log4j2
@DisplayName("SessionMapperImpl unit tests")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
    @Tag("ToEntity")
    @DisplayName("ToEntity should convert a Session Dto to a Session entity")
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
    @Order(2)
    @Tag("ToEntity")
    @DisplayName("ToEntity should return a List of Session entities")
    public void toEntity_ShouldReturnList() {
        // Arrange
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setDescription("Session de test");
        sessionDto.setTeacher_id(teacher.getId());
        sessionDto.setUsers(Arrays.asList(user.getId()));
        List<SessionDto> sessionDtoList = new ArrayList<SessionDto>();
        sessionDtoList.add(sessionDto);

        when(teacherService.findById(teacher.getId())).thenReturn(teacher);
        when(userService.findById(user.getId())).thenReturn(user);

        // Act
        List<Session> sessionList = sessionMapper.toEntity(sessionDtoList);

        // Assert
        assertNotNull(sessionList);
        assertEquals(sessionDtoList.get(0).getId(), sessionList.get(0).getId());
        assertEquals(sessionDtoList.get(0).getDescription(), sessionList.get(0).getDescription());
    }

    @Test
    @Order(3)
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
    @Order(4)
    @Tag("ToDto")
    @DisplayName("ToDto should convert a Session entity to a Session Dto")
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
    @Order(5)
    @Tag("ToDto")
    @DisplayName("ToDto should return to a List of Session Dtos")
    public void toDto_ShouldReturnList() {
        // Arrange
        Session session = Session.builder()
                .id(1L)
                .date(new Date())
                .description("Session description")
                .name("Session")
                .teacher(teacher)
                .users(Arrays.asList(user))
                .build();

        List<Session> sessionList = new ArrayList<>();
        sessionList.add(session);

        // Act
        List<SessionDto> sessionDtoList = sessionMapper.toDto(sessionList);

        // Assert
        assertNotNull(sessionDtoList);
        assertEquals(sessionDtoList.get(0).getId(), sessionList.get(0).getId());
        assertEquals(sessionDtoList.get(0).getDescription(), sessionList.get(0).getDescription());
    }

    @Test
    @Order(6)
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
