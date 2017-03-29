package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.User;
import java.util.Date;

public class UserModel {
  private int userId;
  private String email;
  private String name;
  private Date createdTime;
  private boolean isInstructor;

  protected UserModel() {}

  public UserModel(int userId, String email, String name, Date createdTime, boolean isInstructor) {
    this.userId = userId;
    this.email = email;
    this.name = name;
    this.createdTime = createdTime;
    this.isInstructor = isInstructor;
  }

  public UserModel(User entity) {
    this(
        entity.getUserId(),
        entity.getEmail(),
        entity.getName(),
        entity.getCreatedTime(),
        entity.getIsInstructor());
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
