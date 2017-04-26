package com.onkibot.backend.database.entities;

import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.database.repositories.ExternalResourceRepository;
import com.onkibot.backend.database.repositories.ResourceRepository;
import com.onkibot.backend.exceptions.CategoryNotFoundException;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.exceptions.ExternalResourceNotFoundException;
import com.onkibot.backend.exceptions.ResourceNotFoundException;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * The ExternalResource Entity contains everything related to the ExternalResource for a {@link
 * Resource}.
 */
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"resource_id", "url"}))
@Entity
public class ExternalResource implements Serializable {
  @Id @GeneratedValue private Integer externalResourceId;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Column(nullable = false, length = 50)
  private String title;

  @Lob
  @Column(nullable = false)
  private String comment;

  @Column(nullable = false, length = 2083)
  private String url;

  @ManyToOne
  @JoinColumn(name = "publisher_user_id")
  private User publisherUser;

  @OneToMany(mappedBy = "externalResourceApprovalId.externalResource", cascade = CascadeType.REMOVE)
  @OrderBy("external_resource_id")
  private Set<ExternalResourceApproval> userApprovals;

  protected ExternalResource() {}

  public ExternalResource(
      Resource resource, String title, String comment, String url, User publisherUser) {
    this.resource = resource;
    this.title = title;
    this.comment = comment;
    this.url = url;
    this.publisherUser = publisherUser;
    this.userApprovals = new LinkedHashSet<>();
  }

  public Integer getExternalResourceId() {
    return externalResourceId;
  }

  public Resource getResource() {
    return resource;
  }

  public String getTitle() {
    return title;
  }

  public String getComment() {
    return comment;
  }

  public String getUrl() {
    return url;
  }

  public User getPublisherUser() {
    return publisherUser;
  }

  public Set<ExternalResourceApproval> getUserApprovals() {
    return userApprovals;
  }

  public int getUserApprovalsCount() {
    return userApprovals.size();
  }

  public boolean hasUserApproved(User user) {
    return getUserApprovals() != null
        && getUserApprovals()
            .stream()
            .anyMatch(approval -> approval.getApprovalUser().getUserId().equals(user.getUserId()));
  }

  /**
   * Assert that the {@link ExternalResource} with the ID <code>externalResourceId</code> exists.
   *
   * <p>The method also asserts that these entities exist:
   *
   * <ul>
   *   <li>{@link Course}
   *   <li>{@link Category}
   *   <li>{@link Resource}
   * </ul>
   *
   * @param courseRepository The Repository service for the {@link Course} entity.
   * @param courseId The ID of the {@link Course} entity we want to assert.
   * @param categoryRepository The Repository service for the {@link Category} entity.
   * @param categoryId The ID of the {@link Category} entity we want to assert.
   * @param resourceRepository The Repository service for the {@link Resource} entity.
   * @param resourceId The ID of the {@link Resource} entity we want to assert.
   * @param externalResourceRepository The Repository service for the {@link ExternalResource}
   *     entity.
   * @param externalResourceId The ID of the {@link ExternalResource} entity we want to assert.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not
   *     found.
   * @throws ExternalResourceNotFoundException If a {@link ExternalResource} with the <code>
   *     externalResourceId</code> is not found.
   * @return The {@link ExternalResource} entity if it exists.
   */
  public static ExternalResource assertCourseCategoryExternalResource(
      CourseRepository courseRepository,
      int courseId,
      CategoryRepository categoryRepository,
      int categoryId,
      ResourceRepository resourceRepository,
      int resourceId,
      ExternalResourceRepository externalResourceRepository,
      int externalResourceId) {
    Resource resource =
        Resource.assertCourseCategoryResource(
            courseRepository, courseId,
            categoryRepository, categoryId,
            resourceRepository, resourceId);
    ExternalResource externalResource =
        externalResourceRepository
            .findByExternalResourceId(externalResourceId)
            .orElseThrow(() -> new ExternalResourceNotFoundException(externalResourceId));
    if (!externalResource.getResource().getResourceId().equals(resource.getResourceId())) {
      throw new ExternalResourceNotFoundException(categoryId);
    }
    return externalResource;
  }
}
