package com.onkibot.backend.database.entities;

import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.exceptions.CategoryNotFoundException;
import com.onkibot.backend.exceptions.CourseNotFoundException;

import java.util.*;
import javax.persistence.*;

/**
 * The Course Entity contains everything related to a Course.
 */
@Entity
public class Course {
  @Id @GeneratedValue private Integer courseId;

  @Column(nullable = false, length = 50)
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

  public static Course assertCourse(CourseRepository courseRepository, int courseId) {
    return assertCourse(courseRepository, courseId, null);
  }

  /**
   * Assert that the {@link Course} with the ID <code>courseId</code> exists.
   * <p>
   * This method will also check if a {@link User} attends the course if <code>user</code> is set and not null.
   *
   * @param courseRepository The Repository service for the {@link Course} entity.
   * @param courseId The ID of the {@link Course} entity we want to assert.
   * @param user The {@link User} entity we want to check if attends the {@link Course} with ID <code>courseId</code>.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not found,
   *                                 or if the <code>user</code> argument is not attending the
   *                                 {@link Course} with ID <code>courseId</code> is set and not null.
   * @return The {@link Course} entity if it exists.
   */
  public static Course assertCourse(CourseRepository courseRepository, int courseId, User user) {
    Course course =
            courseRepository
                    .findByCourseId(courseId)
                    .orElseThrow(() -> new CourseNotFoundException(courseId));
    if (user != null && !course.getAttendees().contains(user)) {
      throw new CourseNotFoundException(courseId);
    }
    return course;
  }
}
