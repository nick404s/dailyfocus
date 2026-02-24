package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.AdminPasswordUpdateRequest;
import com.nick404s.dailyfocus.dto.response.AdminUserResponse;
import com.nick404s.dailyfocus.dto.response.SystemStatsResponse;
import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.DailyPlanRepository;
import com.nick404s.dailyfocus.repository.TaskRepository;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.util.AppRoles;
import com.nick404s.dailyfocus.util.AuthenticatedUserProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public AdminServiceImpl(UserRepository userRepository, DailyPlanRepository dailyPlanRepository, TaskRepository taskRepository, PasswordEncoder passwordEncoder, AuthenticatedUserProvider authenticatedUserProvider) {
        this.userRepository = userRepository;
        this.dailyPlanRepository = dailyPlanRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {

        return StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .map(this::convertToAdminUserResponse)
                .toList();
    }

    @Override
    public AdminUserResponse getUserById(long id) {

        User user = getUserFromDB(id);

        return convertToAdminUserResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse promoteToAdmin(long id) {
        // find a non admin user with the id to promote
        User user = getNonAdminUserFromDB(id);

        // overwrite the user authorities
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(AppRoles.ROLE_USER));
        authorities.add(new Authority(AppRoles.ROLE_ADMIN));
        user.setAuthorities(authorities);

        // save to the db
        User updatedUser = userRepository.save(user);

        return convertToAdminUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public AdminUserResponse activateUser(long id) {


        User user = getUserFromDB(id);

        user.activate();

        // save to the db
        User updatedUser = userRepository.save(user);

        return convertToAdminUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public AdminUserResponse deactivateUser(long id) {

        User user = getUserFromDB(id);

        // get current admin user
        User currentAdminUser = authenticatedUserProvider.getAuthenticatedUser();

        // check if the admin user tries to deactivate themselves
        if (user.getId() == currentAdminUser.getId()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admins cannot deactivate themselves");
        }

        user.deactivate();

        // save to the db
        User updatedUser = userRepository.save(user);

        return convertToAdminUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteNonAdminUser(long id) {
        // find the non admin user with the id to delete
        User user = getNonAdminUserFromDB(id);

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void resetUserPassword(long id, AdminPasswordUpdateRequest adminPasswordUpdateRequest) {

        // find the user
        User user = getUserFromDB(id);

        // update the password
        user.setPassword(passwordEncoder.encode(adminPasswordUpdateRequest.getNewPassword()));

        // save to the db
        userRepository.save(user);
    }

    @Override
    public SystemStatsResponse getSystemStats() {
        return new SystemStatsResponse(
                userRepository.count(),
                dailyPlanRepository.count(),
                taskRepository.count()
        );
    }

    private AdminUserResponse convertToAdminUserResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.isActive(),
                user.getAuthorities().stream().map(authority -> (Authority) authority).toList(),
                user.getCreatedAt()
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
