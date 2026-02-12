package com.nick404s.dailyfocus.util;

import com.nick404s.dailyfocus.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class AuthenticatedUserProviderImpl implements AuthenticatedUserProvider{
    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // handle anonymous user
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")){
            throw new AccessDeniedException("Authentication required");
        }
        // cast to User
        return  (User) authentication.getPrincipal();
    }
}
