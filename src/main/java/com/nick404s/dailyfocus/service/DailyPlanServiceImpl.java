package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.DailyPlanRequest;
import com.nick404s.dailyfocus.dto.response.DailyPlanResponse;
import com.nick404s.dailyfocus.dto.response.PlanTaskResponse;
import com.nick404s.dailyfocus.model.DailyPlan;
import com.nick404s.dailyfocus.model.PlanTask;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.DailyPlanRepository;
import com.nick404s.dailyfocus.util.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DailyPlanServiceImpl implements DailyPlanService {

    private final DailyPlanRepository dailyPlanRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public DailyPlanServiceImpl(DailyPlanRepository dailyPlanRepository, AuthenticatedUserProvider authenticatedUserProvider) {
        this.dailyPlanRepository = dailyPlanRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyPlanResponse> getAllDailyPlans() {

        // authenticate the user
        User currentUser = authenticatedUserProvider.getAuthenticatedUser();

        // get all daily plans of the user and map them to the list of responses
        return dailyPlanRepository.findByUser(currentUser)
                .stream()
                .map(this::convertToDailyPlanResponse)
                .toList();
    }

    @Override
    @Transactional
    public DailyPlanResponse createDailyPlan(DailyPlanRequest dailyPlanRequest) {

        // authenticate the user
        User currentUser = authenticatedUserProvider.getAuthenticatedUser();

        // set a daily plan
        DailyPlan dailyPlan = new DailyPlan(
                dailyPlanRequest.getIntent(),
                currentUser
        );

        // check if there are tasks
        if (dailyPlanRequest.getPlanTaskRequests() != null){

            // add the tasks to the plan
            dailyPlanRequest.getPlanTaskRequests().forEach(planTaskRequest -> {
                // create a task from the request
                PlanTask planTask = new PlanTask(
                        planTaskRequest.getText(),
                        planTaskRequest.getPriority(),
                        false, // the new task is not done at the beginning
                        dailyPlan
                );
                // add the task to the plan
                dailyPlan.addPlanTask(planTask);
            });
        }

        // save the plan to the db
        DailyPlan savedDailyPlan = dailyPlanRepository.save(dailyPlan);

        return convertToDailyPlanResponse(savedDailyPlan);
    }

    private DailyPlanResponse convertToDailyPlanResponse(DailyPlan dailyPlan){
        // convert the tasks to dto
        List<PlanTaskResponse> planTaskResponses = dailyPlan
                .getPlanTasks()
                .stream()
                .map(planTask -> new PlanTaskResponse(
                        planTask.getId(),
                        planTask.getText(),
                        planTask.getPriority(),
                        planTask.isDone()
                ))
                .toList();

        return new DailyPlanResponse(
                dailyPlan.getId(),
                dailyPlan.getIntent(),
                planTaskResponses

        );
    }
}
