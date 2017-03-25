package com.onkibot.backend.models;

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
