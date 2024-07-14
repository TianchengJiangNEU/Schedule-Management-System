package com.jtc.model;

import java.util.Date;

public class Schedule {
    private int id;
    private int userId;
    private Date startTime;
    private Date endTime;
    private String title;
    private String description;

    // Constructors, getters, and setters
    public Schedule() {}

    public Schedule(int userId, Date startTime, Date endTime, String title, String description) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}