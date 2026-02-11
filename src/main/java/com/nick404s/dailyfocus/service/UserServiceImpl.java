package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // handle anonymous user
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")){
            throw new AccessDeniedException("Authentication required");
        }
        // cast to User
        User user = (User) authentication.getPrincipal();

        return new UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getAuthorities().stream().map(auth -> (Authority) auth).toList()
                );
    }
}
