package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.User;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailModel extends UserModel {
  private String email;
  private List<CourseModel> attending;
  private List<ResourceModel> resources;

  protected UserDetailModel() {}

  public UserDetailModel(User user) {
    super(user);
    this.email = user.getEmail();
    this.attending =
        user.getAttending().stream().map(CourseModel::new).collect(Collectors.toList());
    this.resources =
        user.getResources().stream().map(ResourceModel::new).collect(Collectors.toList());
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
}
