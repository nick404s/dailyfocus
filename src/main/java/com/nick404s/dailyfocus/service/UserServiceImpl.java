package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.util.AppRoles;
import com.nick404s.dailyfocus.util.AuthenticatedUserProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public UserServiceImpl(UserRepository userRepository, AuthenticatedUserProvider authenticatedUserProvider) {
        this.userRepository = userRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }


    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserInfo() {

        // try to find the authenticated user
        User user = authenticatedUserProvider.getAuthenticatedUser();

        return new UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getAuthorities().stream().map(auth -> (Authority) auth).toList()
                );
    }

    @Override
    public void deleteUser() {
        // try to find the authenticated user
        User user = authenticatedUserProvider.getAuthenticatedUser();
        // check the user is not the last admin
        if (isLastAdmin(user)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin cannot delete itself");
        }

        // delete the user
        userRepository.delete(user);
    }

    private boolean isLastAdmin(User user){
        // check if the user is admin
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
