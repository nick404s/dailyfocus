package com.nick404s.dailyfocus.dto.response;

import jakarta.persistence.Column;

import java.util.List;

public class DailyPlanResponse {

    private long id;

    private String intent;

    private List<PlanTaskResponse> planTaskResponses;

    public DailyPlanResponse(long id, String intent, List<PlanTaskResponse> planTaskResponses) {
        this.id = id;
        this.intent = intent;
        this.planTaskResponses = planTaskResponses;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<PlanTaskResponse> getPlanTaskResponses() {
        return planTaskResponses;
    }

    public void setPlanTaskResponses(List<PlanTaskResponse> planTaskResponses) {
        this.planTaskResponses = planTaskResponses;
    }
}
