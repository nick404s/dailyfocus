package com.nick404s.dailyfocus.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlanTaskTest {

    @Test
    void constructorSetsFieldsCorrectly() {
        DailyPlan plan = new DailyPlan(new User(), java.time.LocalDate.now());

        PlanTask task = new PlanTask("Write code", 2, false, plan);

        assertEquals("Write code", task.getText());
        assertEquals(2, task.getPriority());
        assertFalse(task.isDone());
        assertEquals(plan, task.getPlan());
    }

    @Test
    void settersUpdateFields() {
        PlanTask task = new PlanTask();
        task.setText("Task");
        task.setPriority(3);
        task.setDone(true);

        assertEquals("Task", task.getText());
        assertEquals(3, task.getPriority());
        assertTrue(task.isDone());
    }

    @Test
    void setPlanUpdatesBackReference() {
        DailyPlan plan = new DailyPlan(new User(), java.time.LocalDate.now());
        PlanTask task = new PlanTask();

        task.setPlan(plan);

        assertEquals(plan, task.getPlan());
    }
}
