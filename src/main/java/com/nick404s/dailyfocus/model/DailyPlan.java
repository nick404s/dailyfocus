package com.nick404s.dailyfocus.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "plans")
@Entity
public class DailyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false)
    private String intent;

    @ManyToOne(fetch = FetchType.LAZY) // load the data on demand
    @JoinColumn(name = "user_id", nullable = false) // every plan must have a user
    private User user;

    @Column(nullable = false) // actual local date of the plan
    private LocalDate date;

    @CreationTimestamp // automatically creates a time stamp in the db
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    // one daily plan has many tasks. the owner is the plan object in each task.
    // cascade crud operations to all the tasks.
    // remove the deleted(orphan) user tasks from the db
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PlanTask> planTasks = new ArrayList<>();


    public DailyPlan() {
    }

    public DailyPlan(User user, LocalDate date) {
        this.intent = ""; // default
        this.user = user;
        this.date = date;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<PlanTask> getPlanTasks() {
        return planTasks;
    }

    public void addPlanTask(PlanTask planTask){
        planTasks.add(planTask);
        planTask.setPlan(this); // set the back reference
    }
}
