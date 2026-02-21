package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.util.AppRoles;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;

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
    @Transactional
    public UserResponse promoteToAdmin(long userId) {
        // find a user to promote
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist"));


        boolean isAdmin = user
                .getAuthorities()
                .stream()
                .anyMatch(authority -> AppRoles.ROLE_ADMIN.equals(authority.getAuthority()));

        // check if the user already an admin
        if (isAdmin){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already an admin");
        }

        // overwrite the user authorities
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(AppRoles.ROLE_USER));
        authorities.add(new Authority(AppRoles.ROLE_ADMIN));
        user.setAuthorities(authorities);

        // save to the db
        User updatedUser = userRepository.save(user);

        return convertToUserResponse(updatedUser);
    }

    private UserResponse convertToUserResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getAuthorities().stream().map(authority -> (Authority) authority).toList()
        );
    }
}
