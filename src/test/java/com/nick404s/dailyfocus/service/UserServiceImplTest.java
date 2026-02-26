package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.UpdateProfileRequest;
import com.nick404s.dailyfocus.dto.request.UserPasswordUpdateRequest;
import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.util.AuthenticatedUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedOldPassword");
        user.setAuthorities(List.of(new Authority("ROLE_USER")));
    }

    // ---------------------------------------------------------
    // getUserInfo
    // ---------------------------------------------------------
    @Test
    void getUserInfoReturnsCorrectResponse() {
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);

        UserResponse response = userService.getUserInfo();

        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getFullName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(1, response.getAuthorities().size());
        assertEquals("ROLE_USER",
                response.getAuthorities().get(0).getAuthority());
    }

    // ---------------------------------------------------------
    // updateUserInfo
    // ---------------------------------------------------------
    @Test
    void updateUserInfoUpdatesNamesAndSaves() {
        UpdateProfileRequest request = new
                UpdateProfileRequest("Alice", "Smith", "ignored");

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);

        userService.updateUserInfo(request);

        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        verify(userRepository).save(user);
    }

    // ---------------------------------------------------------
    // updatePassword
    // ---------------------------------------------------------

    @Test
    void updatePasswordSuccessfullyUpdatesPassword() {
        UserPasswordUpdateRequest request =
                new UserPasswordUpdateRequest("oldPass", "newPass", "newPass");

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(passwordEncoder.matches("oldPass",
                "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        userService.updatePassword(request);

        assertEquals("encodedNewPass", user.getPassword());

        // NEW: verify encoding happened
        verify(passwordEncoder).encode("newPass");

        // NEW: verify save was called exactly once
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void updatePasswordThrowsIfOldPasswordInvalid() {
        UserPasswordUpdateRequest request =
                new UserPasswordUpdateRequest("wrongOld", "newPass", "newPass");

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(passwordEncoder.matches("wrongOld",
                "encodedOldPassword")).thenReturn(false);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.updatePassword(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assert ex.getReason() != null;
        assertTrue(ex.getReason().contains("Current password is incorrect"));
    }

    @Test
    void updatePasswordThrowsIfNewPasswordsDoNotMatch() {
        UserPasswordUpdateRequest request =
                new UserPasswordUpdateRequest("oldPass", "newPass",
                        "differentPass");

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(passwordEncoder.matches("oldPass",
                "encodedOldPassword")).thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.updatePassword(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assert ex.getReason() != null;
        assertTrue(ex.getReason().contains("New passwords do not match"));
    }

    @Test
    void updatePasswordThrowsIfNewPasswordSameAsOld() {
        UserPasswordUpdateRequest request =
                new UserPasswordUpdateRequest("oldPass", "oldPass", "oldPass");

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(passwordEncoder.matches("oldPass",
                "encodedOldPassword")).thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.updatePassword(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assert ex.getReason() != null;
        assertTrue(ex.getReason().contains("New password must be different"));
    }
}
