package com.nick404s.dailyfocus.controller;

import com.nick404s.dailyfocus.dto.request.AuthenticationRequest;
import com.nick404s.dailyfocus.dto.request.RegisterRequest;
import com.nick404s.dailyfocus.dto.response.AuthenticationResponse;
import com.nick404s.dailyfocus.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@Tag(name = "REST API Authentication Endpoints", description = "Register and Login operations.") // the swagger docs
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Register a user", description = "Creates a new user in the db.") // the swagger docs
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest registerRequest) {

        authenticationService.register(registerRequest);
    }

    @Operation(summary = "Login a user", description = "Submits email and password for user authentication.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody AuthenticationRequest authenticationRequest){
        return authenticationService.login(authenticationRequest);
    }
}
