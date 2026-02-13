package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.PasswordUpdateRequest;
import com.nick404s.dailyfocus.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserInfo();
    void deleteUser();
    void updatePassword(PasswordUpdateRequest passwordUpdateRequest);
}
