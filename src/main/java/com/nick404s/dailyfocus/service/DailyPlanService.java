package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.DailyPlanRequest;
import com.nick404s.dailyfocus.dto.request.PlanTaskRequest;
import com.nick404s.dailyfocus.dto.response.DailyPlanResponse;

import java.util.List;

public interface DailyPlanService {
    List<DailyPlanResponse> getAllDailyPlans();
    DailyPlanResponse getOrCreateTodayPlan();
    DailyPlanResponse updateDailyPlan(long planId, DailyPlanRequest dailyPlanRequest);
    void deleteDailyPlan(long planId);
    DailyPlanResponse addTask(long planId, PlanTaskRequest planTaskRequest);
    DailyPlanResponse updateTask(long planId, long taskId, PlanTaskRequest planTaskRequest);
    DailyPlanResponse toggleTaskCompletion(long planId, long taskId);
    void deleteTask(long planId, long taskId);
}
