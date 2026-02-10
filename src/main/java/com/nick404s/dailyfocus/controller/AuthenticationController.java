package com.nick404s.dailyfocus.controller;

import com.nick404s.dailyfocus.dto.request.RegisterRequest;
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
    public void register(@Valid @RequestBody RegisterRequest registerRequest) throws Exception{

        authenticationService.register(registerRequest);
    }
}
