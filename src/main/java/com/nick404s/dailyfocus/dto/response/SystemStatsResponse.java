package com.nick404s.dailyfocus.dto.response;

public class SystemStatsResponse {

    private long users;
    private long plans;
    private long tasks;

    public SystemStatsResponse(long users, long plans, long tasks) {
        this.users = users;
        this.plans = plans;
        this.tasks = tasks;
    }

    public long getUsers() {
        return users;
    }

    public void setUsers(long users) {
        this.users = users;
    }

    public long getPlans() {
        return plans;
    }

    public void setPlans(long plans) {
        this.plans = plans;
    }

    public long getTasks() {
        return tasks;
    }

    public void setTasks(long tasks) {
        this.tasks = tasks;
    }
}
