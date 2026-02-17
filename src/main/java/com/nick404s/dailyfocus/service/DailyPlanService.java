package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.DailyPlanRequest;
import com.nick404s.dailyfocus.dto.response.DailyPlanResponse;
import com.nick404s.dailyfocus.dto.response.PlanTaskResponse;

import java.util.List;

public interface DailyPlanService {
    List<DailyPlanResponse> getAllDailyPlans();
    DailyPlanResponse createDailyPlan(DailyPlanRequest dailyPlanRequest);
    PlanTaskResponse createTask(long planId);
    PlanTaskResponse toggleTaskCompletion(long planId, long taskId);
    void deleteTask(long planId, long taskId);
}
