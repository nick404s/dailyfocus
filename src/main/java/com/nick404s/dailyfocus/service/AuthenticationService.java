package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.AuthenticationRequest;
import com.nick404s.dailyfocus.dto.request.RegisterRequest;
import com.nick404s.dailyfocus.dto.response.AuthenticationResponse;

public interface AuthenticationService {

    void register(RegisterRequest registerRequest) throws Exception;
    AuthenticationResponse login(AuthenticationRequest authenticationRequest);
}
