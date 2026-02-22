package com.nick404s.dailyfocus.controller;

import com.nick404s.dailyfocus.dto.request.DailyPlanRequest;
import com.nick404s.dailyfocus.dto.request.PlanTaskRequest;
import com.nick404s.dailyfocus.dto.response.DailyPlanResponse;
import com.nick404s.dailyfocus.service.DailyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "REST API Daily Plans Endpoints", description = "Managing current user daily plan operations.") // the swagger docs
@RestController
@RequestMapping("/api/dailyplans")
public class DailyPlanController {

    private final DailyPlanService dailyPlanService;

    public DailyPlanController(DailyPlanService dailyPlanService) {
        this.dailyPlanService = dailyPlanService;
    }

    @Operation(summary = "Fetch all daily plans for a user", description = "Fetches all daily plans for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<DailyPlanResponse> getAllDailyPlans(){
        return dailyPlanService.getAllDailyPlans();
    }

    @Operation(summary = "Get a today plan for a user", description = "Gets today's plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/plan")
    public DailyPlanResponse getDailyPlan(@RequestParam LocalDate date){
        return dailyPlanService.getOrCreatePlan(date);
    }

    @Operation(summary = "Update daily plan for a user", description = "Updates daily plan fields for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{planId}")
    public DailyPlanResponse updateDailyPlan(@PathVariable @Min(1) long planId, @Valid @RequestBody DailyPlanRequest dailyPlanRequest){
        return dailyPlanService.updateDailyPlan(planId, dailyPlanRequest);
    }

    @Operation(summary = "Delete daily plan for a user", description = "Deletes a daily plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{planId}")
    public void deleteDailyPlan(@PathVariable @Min(1) long planId) {
        dailyPlanService.deleteDailyPlan(planId);
    }

    @Operation(summary = "Add a task to daily plan", description = "Adds a new task to the existing daily plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{planId}/tasks")
    public DailyPlanResponse addTaskToDailyPlan(@PathVariable @Min(1) long planId, @Valid @RequestBody PlanTaskRequest planTaskRequest){
        return dailyPlanService.addTask(planId, planTaskRequest);
    }

    @Operation(summary ="Update a task for daily plan", description = "Toggles task completion in daily plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{planId}/tasks/{taskId}")
    public DailyPlanResponse updateDailyPlanTask(@PathVariable @Min(1) long planId,
                                                 @PathVariable @Min(1) long taskId,
                                                 @Valid @RequestBody PlanTaskRequest planTaskRequest){
        return dailyPlanService.updateTask(planId, taskId, planTaskRequest);
    }

    @Operation(summary = "Toggle task completion for daily plan", description = "Updates fields in a task of daily plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{planId}/tasks/{taskId}/toggle")
    public DailyPlanResponse toggleTaskCompletion(@PathVariable @Min(1) long planId, @PathVariable @Min(1) long taskId){
        return dailyPlanService.toggleTaskCompletion(planId, taskId);
    }

    @Operation(summary = "Delete a task from daily plan", description = "Deletes a task of daily plan for a signed in user.") // the swagger docs
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{planId}/tasks/{taskId}")
    public void deleteTask(@PathVariable @Min(1) long planId, @PathVariable @Min(1) long taskId){
        dailyPlanService.deleteTask(planId, taskId);
    }
}
