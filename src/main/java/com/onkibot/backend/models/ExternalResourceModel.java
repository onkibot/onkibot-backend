package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.ExternalResource;

public class ExternalResourceModel {
    private int externalResourceId;
    private int resourceId;
    private String url;
    private UserModel publisherUser;


    protected ExternalResourceModel() {}

    public ExternalResourceModel(ExternalResource externalResource) {
        this.externalResourceId = externalResource.getExternalResourceId();
        this.resourceId = externalResource.getResource().getResourceId();
        this.url = externalResource.getUrl();
        this.publisherUser = new UserModel(externalResource.getPublisherUser());
    }

    public int getExternalResourceId() {
        return externalResourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getUrl() {
        return url;
    }

    public UserModel getPublisherUser() {
        return publisherUser;
    }
}
