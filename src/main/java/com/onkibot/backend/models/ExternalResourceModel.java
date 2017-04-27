package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.ExternalResource;
import com.onkibot.backend.database.entities.User;

/** This class is used to serializer a {@link ExternalResource}. */
public class ExternalResourceModel {
  private int externalResourceId;
  private int resourceId;
  private String title;
  private String comment;
  private String url;
  private UserModel publisherUser;
  private int approvalCount;
  private boolean myApproval;

  protected ExternalResourceModel() {}

  public ExternalResourceModel(ExternalResource externalResource, User forUser) {
    this.externalResourceId = externalResource.getExternalResourceId();
    this.resourceId = externalResource.getResource().getResourceId();
    this.title = externalResource.getTitle();
    this.comment = externalResource.getComment();
    this.url = externalResource.getUrl();
    this.publisherUser = new UserModel(externalResource.getPublisherUser());
    this.approvalCount = externalResource.getUserApprovalsCount();
    this.myApproval = externalResource.hasUserApproved(forUser);
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

  public int getApprovalCount() {
    return approvalCount;
  }

  public boolean getMyApproval() {
    return myApproval;
  }
}
