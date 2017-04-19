package com.onkibot.backend.database.entities;

import javax.persistence.*;

@Entity
public class ResourceFeedback {
  @Id @GeneratedValue private Integer resourceFeedbackId;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Lob
  @Column(nullable = false)
  private String comment;

  @ManyToOne
  @JoinColumn(name = "feedback_user_id")
  private User feedbackUser;

  protected ResourceFeedback() {}

  public ResourceFeedback(Resource resource, String comment, User feedbackUser) {
    this.resource = resource;
    this.comment = comment;
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

  public User getFeedbackUser() {
    return feedbackUser;
  }
}
