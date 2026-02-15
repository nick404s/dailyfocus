package com.nick404s.dailyfocus.dto.response;

public class PlanTaskResponse {
    // the task part
    private long id;

    private String text;

    private int priority;

    private boolean done;

    public PlanTaskResponse(long id, String text, int priority, boolean done) {
        this.id = id;
        this.text = text;
        this.priority = priority;
        this.done = done;
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
}
