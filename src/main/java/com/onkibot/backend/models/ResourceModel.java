package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Resource;

public class ResourceModel {
    private final int resourceId;
    private final int categoryId;
    private final String name;
    private final String body;
    private final UserModel publisherUser;

    public ResourceModel(Resource resource) {
        this.resourceId = resource.getResourceId();
        this.categoryId = resource.getCategory().getCategoryId();
        this.name = resource.getName();
        this.body = resource.getBody();
        this.publisherUser = new UserModel(resource.getPublisherUser());
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public UserModel getPublisherUser() {
        return publisherUser;
    }
}