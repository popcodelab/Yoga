package com.openclassrooms.starterjwt.security.jwt;


import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class AuthTokenFilterTest {

    private static Instant startedAt;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter = new AuthTokenFilter();


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
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("If the token is valid, should authenticate the user")
    public void doFilterInternal_ValidToken_ShouldAuthenticateUser() throws Exception {
        // Arrange
        String dummyJwtToken = "DummyToken";
        String username = "userName";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + dummyJwtToken);
        when(jwtUtils.validateJwtToken(dummyJwtToken)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(dummyJwtToken)).thenReturn(username);

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(mockUserDetails);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken).isTrue();
        assertThat(mockUserDetails).isEqualTo(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    @DisplayName("If the token is invalid, it should not authenticate the user")
    public void doFilterInternal_InvalidToken_ShouldNotAuthenticateUser() throws Exception {
        // Arrange
        String dummyJwtToken = "DummyToken";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + dummyJwtToken);
        when(jwtUtils.validateJwtToken(dummyJwtToken)).thenReturn(false);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils, never()).getUserNameFromJwtToken(dummyJwtToken);
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

}
