package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.User;
import java.util.List;
import java.util.stream.Collectors;

public class CourseModel {
  private int courseId;
  private String name;
  private String description;
  private List<CategoryModel> categories;
  private List<UserModel> attendees;

  protected CourseModel() {}

  public CourseModel(Course course, User forUser) {
    this.courseId = course.getCourseId();
    this.name = course.getName();
    this.description = course.getDescription();
    this.categories =
        course.getCategories().stream().map(category -> new CategoryModel(category, forUser)).collect(Collectors.toList());
    this.attendees =
        course.getAttendees().stream().map(UserModel::new).collect(Collectors.toList());
  }

  public int getCourseId() {
    return courseId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<CategoryModel> getCategories() {
    return categories;
  }

  public List<UserModel> getAttendees() {
    return attendees;
  }
}
