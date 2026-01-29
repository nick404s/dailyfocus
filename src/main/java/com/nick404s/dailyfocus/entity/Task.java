package com.nick404s.dailyfocus.entity;

import jakarta.persistence.*;

@Table(name = "tasks")
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private int priority;

    @Column(nullable = false)
    private boolean done;

    @ManyToOne(fetch = FetchType.LAZY) // load the data on demand
    @JoinColumn(name = "plan_id", nullable = false) // every task must have a plan
    private DailyPlan plan;

    // default constructor required by JPA
    public Task() {
    }

    public Task(String text, int priority, boolean done, DailyPlan plan) {
        this.text = text;
        this.priority = priority;
        this.done = done;
        this.plan = plan;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public DailyPlan getPlan() {
        return plan;
    }

    public void setPlan(DailyPlan plan) {
        this.plan = plan;
    }
}
