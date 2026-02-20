package com.nick404s.dailyfocus.controller;

import com.nick404s.dailyfocus.dto.request.DailyPlanRequest;
import com.nick404s.dailyfocus.dto.request.PlanTaskRequest;
import com.nick404s.dailyfocus.dto.response.DailyPlanResponse;
import com.nick404s.dailyfocus.dto.response.PlanTaskResponse;
import com.nick404s.dailyfocus.service.DailyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dailyplans")
@Tag(name = "REST API Daily Plans Endpoints", description = "Managing current user daily plan operations.") // the swagger docs
public class DailyPlanController {

    private final DailyPlanService dailyPlanService;

    public DailyPlanController(DailyPlanService dailyPlanService) {
        this.dailyPlanService = dailyPlanService;
    }

    @Operation(summary = "Fetch all daily plans of a user", description = "Fetch all daily plans for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<DailyPlanResponse> getAllDailyPlans(){
        return dailyPlanService.getAllDailyPlans();
    }

    @Operation(summary = "Get a today plan for a user", description = "Get today's plans for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/today")
    public DailyPlanResponse getDailyPlan(){
        return dailyPlanService.getOrCreateTodayPlan();
    }

    @Operation(summary = "Update daily plan of a user", description = "Toggles task completion in a daily plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{planId}")
    public DailyPlanResponse updateDailyPlan(@PathVariable @Min(1) long planId, @Valid @RequestBody DailyPlanRequest dailyPlanRequest){
        return dailyPlanService.updateDailyPlan(planId, dailyPlanRequest);
    }

    @Operation(summary = "Delete a daily plan", description = "Deletes a daily plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{planId}")
    public void deleteDailyPlan(@PathVariable @Min(1) long planId) {
        dailyPlanService.deleteDailyPlan(planId);
    }

    @Operation(summary = "Add a task to daily plan", description = "Add a new task to the existing daily plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{planId}/tasks")
    public DailyPlanResponse addTaskToDailyPlan(@PathVariable @Min(1) long planId, @Valid @RequestBody PlanTaskRequest planTaskRequest){
        return dailyPlanService.addTaskToDailyPlan(planId, planTaskRequest);
    }

    @Operation(summary = "Update task completion in a daily plan of a user", description = "Toggles task completion in a daily plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{planId}/tasks/{taskId}/toggle")
    public PlanTaskResponse toggleTaskCompletion(@PathVariable @Min(1) long planId, @PathVariable @Min(1) long taskId){
        return dailyPlanService.toggleTaskCompletion(planId, taskId);
    }

    @Operation(summary = "Delete a task", description = "Deletes a for a daily plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{planId}/tasks/{taskId}")
    public void deleteTask(@PathVariable @Min(1) long planId, @PathVariable @Min(1) long taskId){
        dailyPlanService.deleteTask(planId, taskId);
    }
}
