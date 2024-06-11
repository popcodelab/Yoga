package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
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
@DisplayName("UserMapperImpl unit tests")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserMapperImplTest {
    private static Instant startedAt;
    private final String firstName = "John";
    private final String lastName = "Doe";
    private final String email = "john.doe@mail.com";
    private final String password = "password";
    private final Boolean isAdmin = true;


    @InjectMocks
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

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
    @DisplayName("ToEntity should convert a UserDto to a User entity")
    public void toEntity_ShouldReturnAnEntity() {
        // Arrange
        UserDto userDto = new UserDto();
        Long id = 1L;
        userDto.setId(id);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setAdmin(isAdmin);

        // Act
        User user = userMapper.toEntity(userDto);

        // Assert
        assertNotNull(user);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPassword(), userDto.getPassword());
        assertEquals(user.isAdmin(), userDto.isAdmin());

    }

    @Test
    @Order(2)
    @Tag("ToEntity")
    @DisplayName("ToEntity should return a List of User entities")
    public void toEntity_ShouldReturnList() {
        // Arrange
        UserDto userDto = new UserDto();
        Long id = 1L;
        userDto.setId(id);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setAdmin(isAdmin);
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(userDto);

        // Act
        List<User> userList = userMapper.toEntity(userDtoList);

        // Assert
        assertNotNull(userList);
        assertEquals(userList.get(0).getId(), userDtoList.get(0).getId());
    }


    @Test
    @Order(3)
    @Tag("ToEntity")
    @DisplayName("toEntity should return null User entity when convert a null User Dto")
    public void toEntity_ShouldReturnNull() {
        // Act
        User user = userMapper.toEntity((UserDto) null);

        // Assert
        assertNull(user);
    }

    @Test
    @Order(4)
    @Tag("ToDto")
    @DisplayName("ToDto convert a User entity to a User Dto")
    public void toDto_ShouldReturnDto() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .admin(isAdmin)
                .build();

        // Act
        UserDto userDto = userMapper.toDto(user);

        // Assert
        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPassword(), userDto.getPassword());
        assertEquals(user.isAdmin(), userDto.isAdmin());
    }


    @Test
    @Order(5)
    @Tag("ToDto")
    @DisplayName("toDto should return null User Dto when convert a null User entity")
    public void toDto_ShouldReturnNull() {
        // Act
        UserDto userDto = userMapper.toDto((User) null);

        // Assert
        assertNull(userDto);
    }

    @Test
    @Order(6)
    @Tag("ToDto")
    @DisplayName("ToDto should return to a List of User Dtos")
    public void toDto_ShouldReturnList() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .admin(isAdmin)
                .build();

        List<User> userList = new ArrayList<>();
        userList.add(user);

        // Act
        List<UserDto> userDtoList = userMapper.toDto(userList);

        // Assert
        assertNotNull(userDtoList);
        assertEquals(userList.get(0).getId(), userDtoList.get(0).getId());
        }


}
