package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.User;

import java.util.Date;

public class UserModel {
    private final int userId;
    private final String email;
    private final String name;
    private final Date createdTime;
    private final boolean isInstructor;

    public UserModel(int userId, String email, String name, Date createdTime, boolean isInstructor) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.createdTime = createdTime;
        this.isInstructor = isInstructor;
    }

    public UserModel(User entity) {
        this(entity.getUserId(), entity.getEmail(), entity.getName(), entity.getCreatedTime(), entity.getIsInstructor());
        System.out.println(entity.getResources());
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public boolean getIsInstructor() {
        return isInstructor;
    }
}
