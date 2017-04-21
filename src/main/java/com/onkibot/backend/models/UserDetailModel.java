package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.User;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailModel extends UserModel {
  private String email;
  private List<CourseModel> attending;
  private List<ResourceModel> resources;
  private List<ExternalResourceModel> externalResources;

  protected UserDetailModel() {}

  public UserDetailModel(User user) {
    this(user, user);
  }

  public UserDetailModel(User user, User forUser) {
    super(user);
    this.email = user.getEmail();
    this.attending =
        user.getAttending()
            .stream()
            .map(course -> new CourseModel(course, forUser))
            .collect(Collectors.toList());
    this.resources =
        user.getResources()
            .stream()
            .map(resource -> new ResourceModel(resource, forUser))
            .collect(Collectors.toList());
    this.externalResources =
        user.getExternalResources()
            .stream()
            .map(externalResource -> new ExternalResourceModel(externalResource, user))
            .collect(Collectors.toList());
  }

  public String getEmail() {
    return email;
  }

  public List<CourseModel> getAttending() {
    return attending;
  }

  public List<ResourceModel> getResources() {
    return resources;
  }

  public List<ExternalResourceModel> getExternalResources() {
    return externalResources;
  }
}
