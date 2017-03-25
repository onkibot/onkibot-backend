package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserDetailModel extends UserModel {
    private List<CourseModel> attending;
    private List<ResourceModel> resources;

    protected UserDetailModel() { }

    public UserDetailModel(User user) {
        super(user);
        this.attending = user.getAttending().stream().map(CourseModel::new).collect(Collectors.toList());
        this.resources = user.getResources().stream().map(ResourceModel::new).collect(Collectors.toList());
    }

    public List<CourseModel> getAttending() {
        return attending;
    }

    public List<ResourceModel> getResources() {
        return resources;
    }
}
