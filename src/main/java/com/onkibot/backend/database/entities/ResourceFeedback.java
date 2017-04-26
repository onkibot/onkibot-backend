package com.onkibot.backend.database.entities;

import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.database.repositories.ResourceFeedbackRepository;
import com.onkibot.backend.database.repositories.ResourceRepository;
import com.onkibot.backend.exceptions.*;

import javax.persistence.*;

/**
 * The ResourceFeedback Entity contains everything related to the ResourceFeedback of a {@link Resource}.
 */
@Entity
public class ResourceFeedback {
  @Id @GeneratedValue private Integer resourceFeedbackId;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Lob
  @Column(nullable = false)
  private String comment;

  @Column(nullable = false, length = 1)
  private int difficulty;

  @ManyToOne
  @JoinColumn(name = "feedback_user_id")
  private User feedbackUser;

  protected ResourceFeedback() {}

  public ResourceFeedback(Resource resource, String comment, int difficulty, User feedbackUser) {
    this.resource = resource;
    this.comment = comment;
    this.difficulty = difficulty;
    this.feedbackUser = feedbackUser;
  }

  public Integer getResourceFeedbackId() {
    return resourceFeedbackId;
  }

  public Resource getResource() {
    return resource;
  }

  public String getComment() {
    return comment;
  }

  public int getDifficulty() {
    return difficulty;
  }

  public User getFeedbackUser() {
    return feedbackUser;
  }

  /**
   * Assert that the {@link ResourceFeedback} with the ID <code>resourceFeedbackId</code> exists.
   * <p>
   * The method also asserts that these entities exist:
   * <ul>
   * <li>{@link Course}</li>
   * <li>{@link Category}</li>
   * <li>{@link Resource}</li>
   * </ul>
   *
   * @param courseRepository The Repository service for the {@link Course} entity.
   * @param courseId The ID of the {@link Course} entity we want to assert.
   * @param categoryRepository The Repository service for the {@link Category} entity.
   * @param categoryId The ID of the {@link Category} entity we want to assert.
   * @param resourceRepository The Repository service for the {@link Resource} entity.
   * @param resourceId The ID of the {@link Resource} entity we want to assert.
   * @param resourceFeedbackRepository The Repository service for the {@link ResourceFeedback} entity.
   * @param resourceFeedbackId The ID of the {@link ResourceFeedback} entity we want to assert.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not found.
   * @throws ResourceFeedbackNotFoundException If a {@link ResourceFeedback} with the <code>resourceFeedbackId</code> is not found.
   * @return The {@link ResourceFeedback} entity if it exists.
   */
  public static ResourceFeedback assertCourseCategoryResourceFeedback(
          CourseRepository courseRepository, int courseId,
          CategoryRepository categoryRepository, int categoryId,
          ResourceRepository resourceRepository, int resourceId,
          ResourceFeedbackRepository resourceFeedbackRepository, int resourceFeedbackId
  ) {
    Resource resource = Resource.assertCourseCategoryResource(
            courseRepository, courseId,
            categoryRepository, categoryId,
            resourceRepository, resourceId
    );
    ResourceFeedback resourceFeedback =
            resourceFeedbackRepository
                    .findByResourceFeedbackId(resourceFeedbackId)
                    .orElseThrow(() -> new ResourceFeedbackNotFoundException(resourceFeedbackId));
    if (!resourceFeedback.getResource().getResourceId().equals(resource.getResourceId())) {
      throw new ExternalResourceNotFoundException(categoryId);
    }
    return resourceFeedback;
  }
}
