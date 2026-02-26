package com.nick404s.dailyfocus.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    void newUserIsActiveAndEnabledByDefault() {
        User user = new User();
        assertTrue(user.isActive());
        assertTrue(user.isEnabled());
    }

    @Test
    void toggleActiveChangesEnabledStatus() {
        User user = new User();

        user.deactivate();
        assertFalse(user.isEnabled());

        user.activate();
        assertTrue(user.isEnabled());
    }

    @Test
    void usernameIsEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getUsername());
    }

    @Test
    void passwordGetterReturnsPassword() {
        User user = new User();
        user.setPassword("secret");
        assertEquals("secret", user.getPassword());
    }
}


