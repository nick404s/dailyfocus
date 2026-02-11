package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.util.AppRoles;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Override
    public void deleteUser() {
        // find the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // handle anonymous user
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")){
            throw new AccessDeniedException("Authentication required");
        }
        // cast to User
        User user = (User) authentication.getPrincipal();
        // check the user is not the last admin
        if (isLastAdmin(user)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin cannot delete itself");
        }

        // delete the user
        userRepository.delete(user);
    }

    private boolean isLastAdmin(User user){
        // check if the user admin
        boolean isAdmin = user.getAuthorities()
                .stream()
                .anyMatch(authority -> AppRoles.ROLE_ADMIN.equals(authority.getAuthority()));
        if (isAdmin){
            // check for the last admin
            long adminCount = userRepository.countAdminUsers();
            return adminCount <= 1;
        }

        return false;
    }
}
