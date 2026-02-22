package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.UserPasswordUpdateRequest;
import com.nick404s.dailyfocus.dto.request.UpdateProfileRequest;
import com.nick404s.dailyfocus.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserInfo();
    void updateUserInfo(UpdateProfileRequest updateProfileRequest);
    void deleteUser();
    void updatePassword(UserPasswordUpdateRequest userPasswordUpdateRequest);
}
