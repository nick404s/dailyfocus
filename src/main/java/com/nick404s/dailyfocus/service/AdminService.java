package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.AdminPasswordUpdateRequest;
import com.nick404s.dailyfocus.dto.response.AdminUserResponse;
import com.nick404s.dailyfocus.dto.response.SystemStatsResponse;
import com.nick404s.dailyfocus.dto.response.UserResponse;

import java.util.List;

public interface AdminService {
    List<AdminUserResponse> getAllUsers();
    AdminUserResponse getUserById(long id);
    AdminUserResponse promoteToAdmin(long id);
    AdminUserResponse activateUser(long id);
    AdminUserResponse deactivateUser(long id);
    void deleteNonAdminUser(long id);
    void resetUserPassword(long id, AdminPasswordUpdateRequest adminPasswordUpdateRequest);
    SystemStatsResponse getSystemStats();
}
