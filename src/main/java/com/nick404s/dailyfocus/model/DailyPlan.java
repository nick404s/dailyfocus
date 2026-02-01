package com.nick404s.dailyfocus.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp // automatically creates a time stamp in the db
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    // one daily plan has many tasks. the owner is the plan object in each task.
    // cascade crud operations to all the tasks.
    // remove the deleted(orphan) user tasks from the db
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;


    public DailyPlan() {
    }

    public DailyPlan(String intent, User user) {
        this.intent = intent;
        this.user = user;
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
}
