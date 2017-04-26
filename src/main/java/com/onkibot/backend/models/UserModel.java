package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.User;
import java.util.Date;

/**
 * This class is used to serializer a {@link User}.
 *
 * <p>If more information is needed (such as email, list of attending Courses, i.e.), use {@link
 * UserDetailModel}
 */
public class UserModel {
  private int userId;
  private String name;
  private Date createdTime;
  private boolean isInstructor;

  protected UserModel() {}

  public UserModel(int userId, String name, Date createdTime, boolean isInstructor) {
    this.userId = userId;
    this.name = name;
    this.createdTime = createdTime;
    this.isInstructor = isInstructor;
  }

  public UserModel(User entity) {
    this(entity.getUserId(), entity.getName(), entity.getCreatedTime(), entity.getIsInstructor());
  }

  public int getUserId() {
    return userId;
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
