package com.onkibot.backend.database.entities;

import java.util.*;
import javax.persistence.*;

@Entity
public class Course {
  @Id @GeneratedValue private Integer courseId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @OneToMany(mappedBy = "course")
  @OrderBy("category_id")
  private Set<Category> categories;

  @ManyToMany(mappedBy = "attending")
  @OrderBy("user_id")
  private Set<User> attendees;

  protected Course() {}

  public Course(String name, String description) {
    this.name = name;
    this.description = description;
    this.categories = new LinkedHashSet<>();
    this.attendees = new LinkedHashSet<>();
  }

  public Integer getCourseId() {
    return courseId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Set<Category> getCategories() {
    return categories;
  }

  public Set<User> getAttendees() {
    return attendees;
  }
}
