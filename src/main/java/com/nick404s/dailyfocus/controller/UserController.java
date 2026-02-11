package com.nick404s.dailyfocus.controller;

import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "REST API User Endpoints", description = "Current user info operations.") // the swagger docs
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public UserResponse getUserInfo(){
        return userService.getUserInfo();
    }
}
