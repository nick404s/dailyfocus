package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.AdminPasswordUpdateRequest;
import com.nick404s.dailyfocus.dto.response.SystemStatsResponse;
import com.nick404s.dailyfocus.dto.response.UserResponse;

import java.util.List;

public interface AdminService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(long id);
    UserResponse promoteToAdmin(long id);
    void deleteNonAdminUser(long id);
    void resetUserPassword(long id, AdminPasswordUpdateRequest adminPasswordUpdateRequest);
    SystemStatsResponse getSystemStats();
}
