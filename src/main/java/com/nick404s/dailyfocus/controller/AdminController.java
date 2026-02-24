package com.nick404s.dailyfocus.controller;


import com.nick404s.dailyfocus.dto.request.AdminPasswordUpdateRequest;
import com.nick404s.dailyfocus.dto.response.AdminUserResponse;
import com.nick404s.dailyfocus.dto.response.SystemStatsResponse;
import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "REST API Admin Endpoints", description = "Admin related operations.") // the swagger docs
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Fetch all users", description = "Fetches all users in the system.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<AdminUserResponse> getAllUsers(){
        return adminService.getAllUsers();
    }

    @Operation(summary = "Fetch a user", description = "Fetches user data in the system.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public AdminUserResponse getUserById(@PathVariable @Min(1) long userId) {
        return adminService.getUserById(userId);
    }

    @Operation(summary = "Promote a user to admin", description = "Promotes a non-admin user to admin role.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/role")
    public AdminUserResponse promoteToAdmin(@PathVariable @Min(1) long userId){
        return adminService.promoteToAdmin(userId);
    }

    @Operation(summary = "Activate user", description = "Activates the selected user account.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/activate")
    public AdminUserResponse activateUser(@PathVariable @Min(1) long userId){
        return adminService.activateUser(userId);
    }

    @Operation(summary = "Deactivate user", description = "Deactivates the selected user account.")  // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/deactivate")
    public AdminUserResponse deactivateUser(@PathVariable @Min(1) long userId){
        return adminService.deactivateUser(userId);
    }

    @Operation(summary = "Delete a user", description = "Deletes a non-admin user from the system.") // the swagger docs
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Min(1) long userId) {
        adminService.deleteNonAdminUser(userId);
    }

    @Operation(summary = "Reset user password", description = "Resets user password by an admin.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}/password")
    public void resetUserPassword(@PathVariable @Min(1) long userId,
                                  @Valid @RequestBody AdminPasswordUpdateRequest adminPasswordUpdateRequest) {
        adminService.resetUserPassword(userId, adminPasswordUpdateRequest);
    }

    @Operation(summary = "Fetch system stats", description = "Fetches overall stats in the system.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stats")
    public SystemStatsResponse getSystemStats(){
        return adminService.getSystemStats();
    }
}
