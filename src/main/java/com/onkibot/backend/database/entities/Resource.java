package com.onkibot.backend.database.entities;

import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.database.repositories.ResourceRepository;
import com.onkibot.backend.exceptions.CategoryNotFoundException;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.exceptions.ResourceNotFoundException;
import java.util.*;
import javax.persistence.*;

/** The Resource Entity contains everything related to the Resource of a {@link Category}. */
@Entity
public class Resource {
  @Id @GeneratedValue private Integer resourceId;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @Column(nullable = false, length = 50)
  private String name;

  @Lob
  @Column(nullable = false)
  private String body;

  @Lob @Column private String comment;

  @ManyToOne
  @JoinColumn(name = "publisher_user_id")
  private User publisherUser;

  @ManyToMany(mappedBy = "resource", cascade = CascadeType.REMOVE)
  @OrderBy("external_resource_id")
  private Set<ExternalResource> externalResources;

  @ManyToMany(mappedBy = "resource", cascade = CascadeType.REMOVE)
  @OrderBy("resource_feedback_id")
  private Set<ResourceFeedback> feedback;

  protected Resource() {}

  public Resource(Category category, String name, String body, String comment, User publisherUser) {
    this.category = category;
    this.name = name;
    this.body = body;
    this.comment = comment;
    this.publisherUser = publisherUser;
    this.externalResources = new LinkedHashSet<>();
    this.feedback = new LinkedHashSet<>();
  }

  public Integer getResourceId() {
    return resourceId;
  }

  public Category getCategory() {
    return category;
  }

  public String getName() {
    return name;
  }

  public String getBody() {
    return body;
  }

  public String getComment() {
    return comment;
  }

  public User getPublisherUser() {
    return publisherUser;
  }

  public Set<ExternalResource> getExternalResources() {
    return externalResources;
  }

  public Set<ResourceFeedback> getFeedback() {
    return feedback;
  }

  public Optional<ResourceFeedback> getFeedbackForUser(User user) {
    return getFeedback()
        .stream()
        .filter(feedback -> feedback.getFeedbackUser().getUserId().equals(user.getUserId()))
        .findFirst();
  }

  public int getAverageFeedbackDifficulty() {
    return (int)
        Math.round(
            getFeedback().stream().mapToInt(ResourceFeedback::getDifficulty).average().orElse(0));
  }

  /**
   * Assert that the {@link Resource} with the ID <code>resourceId</code> exists.
   *
   * <p>The method also asserts that these entities exist:
   *
   * <ul>
   *   <li>{@link Course}
   *   <li>{@link Category}
   * </ul>
   *
   * @param courseRepository The Repository service for the {@link Course} entity.
   * @param courseId The ID of the {@link Course} entity we want to assert.
   * @param categoryRepository The Repository service for the {@link Category} entity.
   * @param categoryId The ID of the {@link Category} entity we want to assert.
   * @param resourceRepository The Repository service for the {@link Resource} entity.
   * @param resourceId The ID of the {@link Resource} entity we want to assert.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not
   *     found.
   * @return The {@link Resource} entity if it exists.
   */
  public static Resource assertCourseCategoryResource(
      CourseRepository courseRepository,
      int courseId,
      CategoryRepository categoryRepository,
      int categoryId,
      ResourceRepository resourceRepository,
      int resourceId) {
    Category category =
        Category.assertCourseCategory(courseRepository, courseId, categoryRepository, categoryId);
    Resource resource =
        resourceRepository
            .findByResourceId(resourceId)
            .orElseThrow(() -> new ResourceNotFoundException(resourceId));
    if (!resource.getCategory().getCategoryId().equals(category.getCategoryId())) {
      throw new ResourceNotFoundException(categoryId);
    }
    return resource;
  }
}
