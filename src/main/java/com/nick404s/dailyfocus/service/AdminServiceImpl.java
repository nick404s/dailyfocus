package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.DailyPlanRepository;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.util.AppRoles;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;
    private final DailyPlanRepository dailyPlanRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .map(this::convertToUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(long id) {

        User user = getUserFromDB(id);

        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse promoteToAdmin(long id) {
        // find a non admin user with the id to promote
        User user = getNonAdminUserFromDB(id);

        // overwrite the user authorities
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(AppRoles.ROLE_USER));
        authorities.add(new Authority(AppRoles.ROLE_ADMIN));
        user.setAuthorities(authorities);

        // save to the db
        User updatedUser = userRepository.save(user);

        return convertToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteNonAdminUser(long id) {
        // find the non admin user with the id to delete
        User user = getNonAdminUserFromDB(id);

        userRepository.delete(user);
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getAuthorities().stream().map(authority -> (Authority) authority).toList()
        );
    }

    private User getUserFromDB(long id){
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist"));
    }

    private User getNonAdminUserFromDB(long id) {
        // find a user in the db
        User user = getUserFromDB(id);

        boolean isAdmin = user
                .getAuthorities()
                .stream()
                .anyMatch(authority -> AppRoles.ROLE_ADMIN.equals(authority.getAuthority()));

        // check if the user is an admin
        if (isAdmin){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is an admin");
        }

        return user;
    }
}
