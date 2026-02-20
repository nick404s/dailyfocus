package com.nick404s.dailyfocus.dto.response;

import java.time.LocalDate;
import java.util.List;

public class DailyPlanResponse {

    private long id;

    private String intent;

    private LocalDate date;

    private List<PlanTaskResponse> planTaskResponses;

    public DailyPlanResponse(long id, String intent, LocalDate date, List<PlanTaskResponse> planTaskResponses) {
        this.id = id;
        this.intent = intent;
        this.date = date;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<PlanTaskResponse> getPlanTaskResponses() {
        return planTaskResponses;
    }

    public void setPlanTaskResponses(List<PlanTaskResponse> planTaskResponses) {
        this.planTaskResponses = planTaskResponses;
    }
}
