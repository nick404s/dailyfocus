package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.AdminPasswordUpdateRequest;
import com.nick404s.dailyfocus.dto.response.AdminUserResponse;
import com.nick404s.dailyfocus.dto.response.SystemStatsResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.DailyPlanRepository;
import com.nick404s.dailyfocus.repository.TaskRepository;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.util.AppRoles;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DailyPlanRepository dailyPlanRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User user;
    private User adminUser;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setAuthorities(List.of(new Authority(AppRoles.ROLE_USER)));
        user.setPassword("oldPass");
        user.activate();

        adminUser = new User();
        adminUser.setId(99L);
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEmail("admin@example.com");
        adminUser.setAuthorities(List.of(
                new Authority(AppRoles.ROLE_USER),
                new Authority(AppRoles.ROLE_ADMIN)
        ));
        adminUser.activate();
    }

    // ---------------------------------------------------------
    // getAllUsers
    // ---------------------------------------------------------
    @Test
    void getAllUsersReturnsMappedResponses() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<AdminUserResponse> responses = adminService.getAllUsers();

        assertEquals(1, responses.size());
        assertEquals("John Doe", responses.get(0).getFullName());
        verify(userRepository).findAll();
    }

    // ---------------------------------------------------------
    // getUserById
    // ---------------------------------------------------------
    @Test
    void getUserByIdReturnsCorrectResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AdminUserResponse response = adminService.getUserById(1L);

        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getFullName());
    }

    @Test
    void getUserByIdThrowsIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> adminService.getUserById(1L)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("User does not exist"));
    }

    // ---------------------------------------------------------
    // promoteToAdmin
    // ---------------------------------------------------------
    @Test
    void promoteToAdminAddsAdminRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        AdminUserResponse response = adminService.promoteToAdmin(1L);

        assertEquals(2, response.getAuthorities().size());
        assertTrue(response.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(AppRoles.ROLE_ADMIN)));
        verify(userRepository).save(user);
    }

    @Test
    void promoteToAdminThrowsIfUserIsAlreadyAdmin() {
        when(userRepository.findById(99L)).thenReturn(Optional.of(adminUser));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> adminService.promoteToAdmin(99L)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("User is an admin"));
    }

    // ---------------------------------------------------------
    // activateUser
    // ---------------------------------------------------------
    @Test
    void activateUserActivatesAndSaves() {
        user.deactivate();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        AdminUserResponse response = adminService.activateUser(1L);

        assertTrue(response.isActive());
        verify(userRepository).save(user);
    }

    // ---------------------------------------------------------
    // deactivateUser
    // ---------------------------------------------------------
    @Test
    void deactivateUserDeactivatesAndSaves() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(adminUser);
        when(userRepository.save(user)).thenReturn(user);

        AdminUserResponse response = adminService.deactivateUser(1L);

        assertFalse(response.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUserThrowsIfAdminDeactivatesSelf() {
        when(userRepository.findById(99L)).thenReturn(Optional.of(adminUser));
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(adminUser);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> adminService.deactivateUser(99L)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assert ex.getReason() != null;
        assertTrue(ex.getReason().contains("Admins cannot deactivate themselves"));
    }

    // ---------------------------------------------------------
    // deleteNonAdminUser
    // ---------------------------------------------------------
    @Test
    void deleteNonAdminUserDeletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.deleteNonAdminUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteNonAdminUserThrowsIfUserIsAdmin() {
        when(userRepository.findById(99L)).thenReturn(Optional.of(adminUser));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> adminService.deleteNonAdminUser(99L)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("User is an admin"));
    }

    // ---------------------------------------------------------
    // resetUserPassword
    // ---------------------------------------------------------
    @Test
    void resetUserPasswordEncodesAndSaves() {
        AdminPasswordUpdateRequest request = new
                AdminPasswordUpdateRequest("newPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        adminService.resetUserPassword(1L, request);

        assertEquals("encodedPass", user.getPassword());
        verify(passwordEncoder).encode("newPass");
        verify(userRepository).save(user);
    }

    // ---------------------------------------------------------
    // getSystemStats
    // ---------------------------------------------------------
    @Test
    void getSystemStatsReturnsCounts() {
        when(userRepository.count()).thenReturn(10L);
        when(dailyPlanRepository.count()).thenReturn(20L);
        when(taskRepository.count()).thenReturn(30L);

        SystemStatsResponse response = adminService.getSystemStats();

        assertEquals(10L, response.getUsers());
        assertEquals(20L, response.getPlans());
        assertEquals(30L, response.getTasks());
    }
}
