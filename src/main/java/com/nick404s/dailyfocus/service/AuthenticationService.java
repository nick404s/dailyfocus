package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.RegisterRequest;

public interface AuthenticationService {

    void register(RegisterRequest registerRequest) throws Exception;
}
