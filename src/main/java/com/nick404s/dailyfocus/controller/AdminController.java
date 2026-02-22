package com.nick404s.dailyfocus.controller;


import com.nick404s.dailyfocus.dto.response.UserResponse;
import com.nick404s.dailyfocus.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public List<UserResponse> getAllUsers(){
        return adminService.getAllUsers();
    }

    @Operation(summary = "Promote a user to admin", description = "Promotes a non-admin user to admin role.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/role")
    public UserResponse promoteToAdmin(@PathVariable @Min(1) long userId){
        return adminService.promoteToAdmin(userId);
    }

    @Operation(summary = "Delete a user", description = "Deletes a non-admin user from the system.") // the swagger docs
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Min(1) long userId) {
        adminService.deleteNonAdminUser(userId);
    }
}
