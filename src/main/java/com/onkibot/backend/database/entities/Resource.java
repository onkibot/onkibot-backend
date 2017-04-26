package com.onkibot.backend.database.entities;

import java.util.*;
import javax.persistence.*;

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
}
