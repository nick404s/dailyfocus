package com.nick404s.dailyfocus.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class AdminPasswordUpdateRequest {

    @NotEmpty(message = "New password is required")
    @Size(min = 6, max = 30, message = "Password must be 6-30 characters long")
    private String newPassword;

    public AdminPasswordUpdateRequest(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
