package com.onkibot.backend.database.entities;

import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.exceptions.UserNotFoundException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;

/**
 * The User Entity contains everything related to a User.
 */
@Entity
public class User implements Serializable {
  @Id @GeneratedValue private Integer userId;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String encodedPassword;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Date createdTime;

  @Column(nullable = false)
  private boolean isInstructor;

  @ManyToMany(cascade = CascadeType.ALL)
  @OrderBy("course_id")
  @JoinTable(
    name = "attends",
    joinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId"),
    inverseJoinColumns = @JoinColumn(name = "courseId", referencedColumnName = "courseId")
  )
  private Set<Course> attending;

  @OneToMany(mappedBy = "publisherUser")
  @OrderBy("resource_id")
  private Set<Resource> resources;

  @OneToMany(mappedBy = "publisherUser")
  @OrderBy("resource_id")
  private Set<ExternalResource> externalResources;

  @OneToMany(mappedBy = "externalResourceApprovalId.approvalUser", fetch = FetchType.LAZY)
  @OrderBy("external_resource_id")
  private Set<ExternalResourceApproval> externalResourceApprovals;

  protected User() {}

  public User(String email, String encodedPassword, String name, boolean isInstructor) {
    this.email = email;
    this.encodedPassword = encodedPassword;
    this.name = name;
    this.createdTime = new Date();
    this.isInstructor = isInstructor;
    this.attending = new LinkedHashSet<>();
    this.resources = new LinkedHashSet<>();
    this.externalResources = new LinkedHashSet<>();
    this.externalResourceApprovals = new LinkedHashSet<>();
  }

  public Integer getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public String getEncodedPassword() {
    return encodedPassword;
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

  public Set<Course> getAttending() {
    return attending;
  }

  public boolean isAttending(Course course) {
    return this.attending.contains(course);
  }

  public Set<Resource> getResources() {
    return resources;
  }

  public Set<ExternalResource> getExternalResources() {
    return externalResources;
  }

  public Set<ExternalResourceApproval> getExternalResourceApprovals() {
    return externalResourceApprovals;
  }

  public Set<ExternalResource> getApprovedExternalResources() {
    return externalResourceApprovals
        .stream()
        .map(ExternalResourceApproval::getExternalResource)
        .collect(Collectors.toSet());
  }

  /**
   * Checks if the user has approved an {@link ExternalResource}.
   *
   * @param externalResource The {@link ExternalResource} we want to check against.
   * @return <code>true</code> if the user has approved the resource, <code>false</code> if not.
   */
  public boolean hasApprovedExternalResource(ExternalResource externalResource) {
    return externalResourceApprovals
        .stream()
        .anyMatch(
            externalResourceApproval ->
                externalResourceApproval.getExternalResource().equals(externalResource));
  }

  /**
   * Assert that the {@link User} with the ID <code>userId</code> exists.
   *
   * @param userRepository The Repository service for the {@link User} entity.
   * @param userId The ID of the {@link User} entity we want to assert.
   * @throws UserNotFoundException If a {@link User} with the <code>userId</code> is not found.
   * @return The {@link User} entity if it exists.
   */
  public static User assertUser(UserRepository userRepository, int userId) {
    return userRepository
            .findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
  }
}
