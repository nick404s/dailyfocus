package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.DailyPlanRequest;
import com.nick404s.dailyfocus.dto.response.DailyPlanResponse;

public interface DailyPlanService {
    DailyPlanResponse createDailyPlan(DailyPlanRequest dailyPlanRequest);
}
