package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.ExternalResource;

public class ExternalResourceModel {
  private int externalResourceId;
  private int resourceId;
  private String title;
  private String comment;
  private String url;
  private UserModel publisherUser;
  private boolean hasApproved;

  protected ExternalResourceModel() {}

  public ExternalResourceModel(ExternalResource externalResource, boolean hasApproved) {
    this.externalResourceId = externalResource.getExternalResourceId();
    this.resourceId = externalResource.getResource().getResourceId();
    this.title = externalResource.getTitle();
    this.comment = externalResource.getComment();
    this.url = externalResource.getUrl();
    this.publisherUser = new UserModel(externalResource.getPublisherUser());
    this.hasApproved = hasApproved;
  }

  public int getExternalResourceId() {
    return externalResourceId;
  }

  public int getResourceId() {
    return resourceId;
  }

  public String getTitle() {
    return title;
  }

  public String getComment() {
    return comment;
  }

  public String getUrl() {
    return url;
  }

  public UserModel getPublisherUser() {
    return publisherUser;
  }

  public boolean getHasApproved() {
    return hasApproved;
  }
}
