package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.User;

/**
 * This class is used in the {@link com.onkibot.backend.api.CourseController}
 * when a {@link User} is attempting to create a new {@link Course}.
 */
public class CourseInputModel {
  private String name;
  private String description;

  protected CourseInputModel() {}

  public CourseInputModel(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
