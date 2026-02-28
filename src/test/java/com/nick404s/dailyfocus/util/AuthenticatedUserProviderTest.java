package com.nick404s.dailyfocus.util;

import com.nick404s.dailyfocus.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticatedUserProviderImplTest {

    private final AuthenticatedUserProviderImpl provider = new AuthenticatedUserProviderImpl();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // ---------------------------------------------------------
    // Case 1: No authentication in context
    // ---------------------------------------------------------
    @Test
    void getAuthenticatedUserNoAuthenticationThrowsAccessDenied() {
        SecurityContextHolder.clearContext();

        assertThrows(AccessDeniedException.class, provider::getAuthenticatedUser);
    }

    // ---------------------------------------------------------
    // Case 2: Anonymous user
    // ---------------------------------------------------------
    @Test
    void getAuthenticatedUserAnonymousUserThrowsAccessDenied() {
        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("anonymousUser", "");
        auth.setAuthenticated(false);

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(AccessDeniedException.class, provider::getAuthenticatedUser);
    }

    // ---------------------------------------------------------
    // Case 3: Valid authenticated user
    // ---------------------------------------------------------
    @Test
    void getAuthenticatedUserValidUserReturnsUser() {
        User user = new User();
        user.setEmail("john@example.com");

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(user, "");
        auth.setAuthenticated(true);

        SecurityContextHolder.getContext().setAuthentication(auth);

        User result = provider.getAuthenticatedUser();

        assertEquals("john@example.com", result.getEmail());
    }


    @Test
    void getAuthenticatedUserAuthenticatedButAnonymousStringThrowsAccessDenied() {
        // principal is the string, but technically "authenticated"
        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("anonymousUser", "");
        auth.setAuthenticated(true);

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(AccessDeniedException.class, provider::getAuthenticatedUser);
    }
}

