package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.PasswordUpdateRequest;
import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.util.AppRoles;
import com.nick404s.dailyfocus.util.AuthenticatedUserProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, AuthenticatedUserProvider authenticatedUserProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    @Transactional
    public void updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
        // try to find the authenticated user
        User user = authenticatedUserProvider.getAuthenticatedUser();

        // validate the old password
        if(!isOldPasswordValid(user.getPassword(), passwordUpdateRequest.getOldPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        // check the new password confirmation
        if (!isNewPasswordConfirmed(passwordUpdateRequest.getNewPassword(),
                passwordUpdateRequest.getNewPasswordConfirmation())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords do not match");
        }

        // check if the new password is the same as the old one
        if (!isNewPasswordDifferent(passwordUpdateRequest.getOldPassword(),
                passwordUpdateRequest.getNewPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from the old one");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        // save to the db
        userRepository.save(user);
    }

    private boolean isOldPasswordValid(String currentPassword, String oldPassword){
        return passwordEncoder.matches(oldPassword, currentPassword); // compare the bcrypt values of the passwords
    }

    private boolean isNewPasswordConfirmed(String newPassword, String newPasswordConfirmation){
        return newPassword.equals(newPasswordConfirmation);
    }

    private boolean isNewPasswordDifferent(String oldPassword, String newPassword){
        return !oldPassword.equals(newPassword);
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
