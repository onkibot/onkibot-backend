package com.onkibot.backend.database.entities;

import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.exceptions.CategoryNotFoundException;
import com.onkibot.backend.exceptions.CourseNotFoundException;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;

/**
 * The Category Entity contains everything related to the Category of a {@link Course}.
 */
@Entity
public class Category implements Serializable {
  @Id @GeneratedValue private Integer categoryId;

  @ManyToOne()
  @JoinColumn(name = "course_id")
  private Course course;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @OneToMany(mappedBy = "category")
  @OrderBy("resource_id")
  private Set<Resource> resources;

  protected Category() {}

  public Category(Course course, String name, String description) {
    this.course = course;
    this.name = name;
    this.description = description;
    this.resources = new LinkedHashSet<>();
  }

  public Integer getCategoryId() {
    return categoryId;
  }

  public Course getCourse() {
    return course;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Set<Resource> getResources() {
    return resources;
  }

  /**
   * Assert that the {@link Category} with the ID <code>categoryId</code> exists.
   * <p>
   * The method also asserts that these entities exist:
   * <ul>
   * <li>Course</li>
   * </ul>
   *
   * @param courseRepository The Repository service for the {@link Course} entity.
   * @param courseId The ID of the {@link Course} entity we want to assert.
   * @param categoryRepository The Repository service for the {@link Category} entity.
   * @param categoryId The ID of the {@link Category} entity we want to assert.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not found.
   * @return The {@link Category} entity if it exists.
   */
  public static Category assertCourseCategory(CourseRepository courseRepository, int courseId, CategoryRepository categoryRepository, int categoryId) {
    Course course = Course.assertCourse(courseRepository, courseId);
    Category category =
            categoryRepository
                    .findByCategoryId(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    if (!category.getCourse().getCourseId().equals(course.getCourseId())) {
      throw new CategoryNotFoundException(categoryId);
    }
    return category;
  }
}
