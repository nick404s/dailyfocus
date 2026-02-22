package com.nick404s.dailyfocus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


public class DailyPlanRequest {

    @NotBlank(message = "Intent is required")
    @Size(min = 3, max = 30, message = "Intent must be 3-30 characters")
    private String intent;

    public DailyPlanRequest(String intent) {
        this.intent = intent;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }
}
