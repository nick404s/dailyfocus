package com.nick404s.dailyfocus.dto.response;

import com.nick404s.dailyfocus.model.Authority;

import java.util.Date;
import java.util.List;

public class AdminUserResponse {

    private long id;

    private String fullName;

    private String email;

    private boolean active;

    private List<Authority> authorities;

    private Date createdAt;

    public AdminUserResponse(long id, String fullName, String email, boolean active, List<Authority> authorities, Date createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.active = active;
        this.authorities = authorities;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
