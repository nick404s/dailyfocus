package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.DailyPlanRequest;
import com.nick404s.dailyfocus.dto.request.PlanTaskRequest;
import com.nick404s.dailyfocus.dto.response.DailyPlanResponse;
import com.nick404s.dailyfocus.model.DailyPlan;
import com.nick404s.dailyfocus.model.PlanTask;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.DailyPlanRepository;
import com.nick404s.dailyfocus.util.AuthenticatedUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyPlanServiceImplTest {

    @Mock
    private DailyPlanRepository dailyPlanRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private DailyPlanServiceImpl dailyPlanService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void getAllDailyPlansReturnsMappedResponses() {
        DailyPlan plan = new DailyPlan(user, LocalDate.now());
        plan.setId(10L);

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByUser(user)).thenReturn(List.of(plan));

        List<DailyPlanResponse> responses = dailyPlanService.getAllDailyPlans();

        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
        verify(dailyPlanRepository).findByUser(user);
    }


    @Test
    void getOrCreatePlanReturnsExistingPlan() {
        LocalDate date = LocalDate.now();
        DailyPlan existing = new DailyPlan(user, date);
        existing.setId(5L);

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByUserAndDate(user,
                date)).thenReturn(Optional.of(existing));

        DailyPlanResponse response = dailyPlanService.getOrCreatePlan(date);

        assertEquals(5L, response.getId());
        verify(dailyPlanRepository, never()).save(any());
    }

    @Test
    void getOrCreatePlanCreatesNewPlanIfNotFound() {
        LocalDate date = LocalDate.now();
        DailyPlan created = new DailyPlan(user, date);
        created.setId(7L);

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByUserAndDate(user,
                date)).thenReturn(Optional.empty());
        when(dailyPlanRepository.save(any(DailyPlan.class))).thenReturn(created);

        DailyPlanResponse response = dailyPlanService.getOrCreatePlan(date);

        assertEquals(7L, response.getId());
        verify(dailyPlanRepository).save(any(DailyPlan.class));
    }


    @Test
    void updateDailyPlanUpdatesIntent() {
        DailyPlan plan = new DailyPlan(user, LocalDate.now());
        plan.setId(3L);

        DailyPlanRequest request = new DailyPlanRequest("New intent");

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByIdAndUser(3L,
                user)).thenReturn(Optional.of(plan));
        when(dailyPlanRepository.save(plan)).thenReturn(plan);

        DailyPlanResponse response =
                dailyPlanService.updateDailyPlan(3L, request);

        assertEquals("New intent", response.getIntent());
        verify(dailyPlanRepository).save(plan);
    }

    @Test
    void updateDailyPlanThrowsIfNotFound() {
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByIdAndUser(99L,
                user)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> dailyPlanService.updateDailyPlan(99L, new
                        DailyPlanRequest("abc")));
    }

    @Test
    void deleteDailyPlanDeletesOwnedPlan() {
        DailyPlan plan = new DailyPlan(user, LocalDate.now());
        plan.setId(4L);

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByIdAndUser(4L,
                user)).thenReturn(Optional.of(plan));

        dailyPlanService.deleteDailyPlan(4L);

        verify(dailyPlanRepository).delete(plan);
    }

    @Test
    void addTaskCreatesAndAddsTask() {
        DailyPlan plan = new DailyPlan(user, LocalDate.now());
        plan.setId(8L);

        PlanTaskRequest request = new PlanTaskRequest("Task text", 2);

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByIdAndUser(8L,
                user)).thenReturn(Optional.of(plan));
        when(dailyPlanRepository.save(plan)).thenReturn(plan);

        DailyPlanResponse response = dailyPlanService.addTask(8L, request);

        assertEquals(1, response.getPlanTaskResponses().size());
        assertEquals("Task text",
                response.getPlanTaskResponses().get(0).getText());
        verify(dailyPlanRepository).save(plan);
    }

    @Test
    void updateTaskUpdatesFields() {
        DailyPlan plan = new DailyPlan(user, LocalDate.now());
        plan.setId(9L);

        PlanTask task = new PlanTask("Old", 1, false, plan);
        task.setId(100L);
        plan.addPlanTask(task);

        PlanTaskRequest request = new PlanTaskRequest("Updated", 5);

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByIdAndUser(9L,
                user)).thenReturn(Optional.of(plan));
        when(dailyPlanRepository.save(plan)).thenReturn(plan);

        DailyPlanResponse response = dailyPlanService.updateTask(9L,
                100L, request);

        assertEquals("Updated",
                response.getPlanTaskResponses().get(0).getText());
        assertEquals(5, response.getPlanTaskResponses().get(0).getPriority());
    }

    @Test
    void updateTaskThrowsIfTaskNotFound() {
        DailyPlan plan = new DailyPlan(user, LocalDate.now());
        plan.setId(9L);

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByIdAndUser(9L,
                user)).thenReturn(Optional.of(plan));

        assertThrows(ResponseStatusException.class,
                () -> dailyPlanService.updateTask(9L, 999L, new
                        PlanTaskRequest("abc", 1)));
    }

    @Test
    void toggleTaskCompletionFlipsDoneFlag() {
        DailyPlan plan = new DailyPlan(user, LocalDate.now());
        plan.setId(11L);

        PlanTask task = new PlanTask("Test", 1, false, plan);
        task.setId(200L);
        plan.addPlanTask(task);

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByIdAndUser(11L,
                user)).thenReturn(Optional.of(plan));
        when(dailyPlanRepository.save(plan)).thenReturn(plan);

        DailyPlanResponse response =
                dailyPlanService.toggleTaskCompletion(11L, 200L);

        assertTrue(response.getPlanTaskResponses().get(0).isDone());
    }

    @Test
    void deleteTaskRemovesTask() {
        DailyPlan plan = new DailyPlan(user, LocalDate.now());
        plan.setId(12L);

        PlanTask task = new PlanTask("Test", 1, false, plan);
        task.setId(300L);
        plan.addPlanTask(task);

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(dailyPlanRepository.findByIdAndUser(12L,
                user)).thenReturn(Optional.of(plan));

        dailyPlanService.deleteTask(12L, 300L);

        assertTrue(plan.getPlanTasks().isEmpty());
        verify(dailyPlanRepository).save(plan);
    }
}
