package com.nick404s.dailyfocus.controller;

import com.nick404s.dailyfocus.dto.request.UserPasswordUpdateRequest;
import com.nick404s.dailyfocus.dto.request.UpdateProfileRequest;
import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Daily Focus REST API User Endpoints", description = "Current user info operations.") // the swagger docs
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user information", description = "Get the current user info.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/info")
    public UserResponse getUserInfo(){
        return userService.getUserInfo();
    }

    @Operation(summary = "Update user profile", description = "Change the current user profile info after verification.") // the swagger docs
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/me")
    public void updateUserProfile(@Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
        userService.updateUserInfo(updateProfileRequest);
    }

    @Operation(summary = "Update password", description = "Change the current user password after verification.") // the swagger docs
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/password")
    public void updatePassword(@Valid @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest) {
        userService.updatePassword(userPasswordUpdateRequest);
    }
}
