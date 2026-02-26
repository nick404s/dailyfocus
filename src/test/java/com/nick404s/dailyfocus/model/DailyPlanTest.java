package com.nick404s.dailyfocus.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DailyPlanTest {

    @Test
    void constructorSetsUserAndDateAndDefaultIntent() {
        User user = new User();
        LocalDate date = LocalDate.of(2024, 1, 1);

        DailyPlan plan = new DailyPlan(user, date);

        assertEquals(user, plan.getUser());
        assertEquals(date, plan.getDate());
        assertEquals("", plan.getIntent());
    }

    @Test
    void addPlanTaskAddsTaskAndSetsBackReference() {
        User user = new User();
        DailyPlan plan = new DailyPlan(user, LocalDate.now());

        PlanTask task = new PlanTask();
        task.setText("Test task");
        task.setPriority(1);
        task.setDone(false);

        plan.addPlanTask(task);

        assertEquals(1, plan.getPlanTasks().size());
        assertEquals(task, plan.getPlanTasks().get(0));
        assertEquals(plan, task.getPlan());
    }

}
