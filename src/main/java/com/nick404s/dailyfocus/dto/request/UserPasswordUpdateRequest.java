package com.nick404s.dailyfocus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserPasswordUpdateRequest {

    @NotBlank(message = "Old password is required")
    @Size(min = 6, max = 30, message = "Password must be 6-30 characters long")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 30, message = "Password must be 6-30 characters long")
    private String newPassword;

    @NotBlank(message = "Confirmed password is required")
    @Size(min = 6, max = 30, message = "Password must be 6-30 characters long")
    private String newPasswordConfirmation;

    public UserPasswordUpdateRequest(String oldPassword, String newPassword, String newPasswordConfirmation) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirmation = newPasswordConfirmation;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }

    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
