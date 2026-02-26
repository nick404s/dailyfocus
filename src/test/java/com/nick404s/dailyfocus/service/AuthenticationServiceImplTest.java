package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.AuthenticationRequest;
import com.nick404s.dailyfocus.dto.request.RegisterRequest;
import com.nick404s.dailyfocus.dto.response.AuthenticationResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.util.AppRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private User user;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest(
                "John",
                "Doe",
                "john@example.com",
                "password123"
        );

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("john@example.com");
        authenticationRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setAuthorities(java.util.List.of(new
                Authority(AppRoles.ROLE_USER)));
    }

    // ---------------------------------------------------------
    // register()
    // ---------------------------------------------------------
    @Test
    void registerCreatesNewUserWhenEmailNotPresent() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.count()).thenReturn(0L); // first user → admin

        authenticationService.register(registerRequest);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerThrowsConflictWhenEmailAlreadyExists() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authenticationService.register(registerRequest)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assert ex.getReason() != null;
        assertTrue(ex.getReason().contains("already registered"));
    }

    @Test
    void registerAssignsAdminRoleToFirstUser() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.count()).thenReturn(0L);

        authenticationService.register(registerRequest);

        verify(userRepository).save(argThat(savedUser ->
                savedUser.getAuthorities().stream()
                        .anyMatch(a ->
                                Objects.equals(a.getAuthority(), AppRoles.ROLE_ADMIN))
        ));
    }

    @Test
    void registerAssignsOnlyUserRoleToNonFirstUser() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.count()).thenReturn(5L);

        authenticationService.register(registerRequest);

        verify(userRepository).save(argThat(savedUser ->
                savedUser.getAuthorities().size() == 1
                        && savedUser
                                .getAuthorities()
                                .stream()
                                .anyMatch(authority -> Objects.equals(authority.getAuthority(), AppRoles.ROLE_USER))
        ));
    }

    // ---------------------------------------------------------
    // login()
    // ---------------------------------------------------------
    @Test
    void loginAuthenticatesAndReturnsToken() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(HashMap.class),
                eq(user))).thenReturn("jwt-token");

        AuthenticationResponse response =
                authenticationService.login(authenticationRequest);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("john@example.com", "password123")
        );

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginThrowsUnauthorizedIfUserNotFound() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authenticationService.login(authenticationRequest)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assert ex.getReason() != null;
        assertTrue(ex.getReason().contains("Invalid email or password"));
    }

}

