package com.nick404s.dailyfocus.controller;

import com.nick404s.dailyfocus.dto.request.DailyPlanRequest;
import com.nick404s.dailyfocus.dto.response.DailyPlanResponse;
import com.nick404s.dailyfocus.service.DailyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dailyplans")
@Tag(name = "REST API Daily Plans Endpoints", description = "Managing current user daily plan operations.") // the swagger docs
public class DailyPlanController {

    private final DailyPlanService dailyPlanService;

    public DailyPlanController(DailyPlanService dailyPlanService) {
        this.dailyPlanService = dailyPlanService;
    }

    @Operation(summary = "Create daily plan for a user", description = "Create a daily plan for the signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DailyPlanResponse createDailyPlan(@Valid @RequestBody DailyPlanRequest dailyPlanRequest){
        return dailyPlanService.createDailyPlan(dailyPlanRequest);
    }
}
