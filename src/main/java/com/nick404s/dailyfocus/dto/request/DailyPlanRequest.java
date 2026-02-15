package com.nick404s.dailyfocus.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class DailyPlanRequest {

    @NotEmpty(message = "Intent is required")
    @Size(min = 3, max = 30, message = "Intent must be 3-30 characters")
    private String intent;

    @Size(min = 1, message = "At least one task is required")
    List<PlanTaskRequest> planTaskRequests;

    public DailyPlanRequest(String intent, List<PlanTaskRequest> planTaskRequests) {
        this.intent = intent;
        this.planTaskRequests = planTaskRequests;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<PlanTaskRequest> getPlanTaskRequests() {
        return planTaskRequests;
    }

    public void setPlanTaskRequests(List<PlanTaskRequest> planTaskRequests) {
        this.planTaskRequests = planTaskRequests;
    }
}
