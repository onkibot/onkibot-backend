package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.ExternalResource;
import com.onkibot.backend.database.entities.Resource;
import com.onkibot.backend.database.entities.User;
import java.util.ArrayList;
import java.util.List;

public class ResourceModel {
  private int resourceId;
  private int categoryId;
  private String name;
  private String body;
  private UserModel publisherUser;
  private List<ExternalResourceModel> externalResources;

  protected ResourceModel() {}

  public ResourceModel(Resource resource) {
    this.resourceId = resource.getResourceId();
    this.categoryId = resource.getCategory().getCategoryId();
    this.name = resource.getName();
    this.body = resource.getBody();
    this.publisherUser = new UserModel(resource.getPublisherUser());
    this.externalResources = new ArrayList<>();

    for (ExternalResource externalResource : resource.getExternalResources()) {
      this.externalResources.add(new ExternalResourceModel(externalResource, false));
    }
  }

  public ResourceModel(Resource resource, User sessionUser) {
    this.resourceId = resource.getResourceId();
    this.categoryId = resource.getCategory().getCategoryId();
    this.name = resource.getName();
    this.body = resource.getBody();
    this.publisherUser = new UserModel(resource.getPublisherUser());
    this.externalResources = new ArrayList<>();

    for (ExternalResource externalResource : resource.getExternalResources()) {
      if (sessionUser.hasApprovedExternalResource(externalResource)) {
        this.externalResources.add(new ExternalResourceModel(externalResource, true));
      } else {
        this.externalResources.add(new ExternalResourceModel(externalResource, false));
      }
    }
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

  public List<ExternalResourceModel> getExternalResources() {
    return externalResources;
  }
}
