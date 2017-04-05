package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.ResourceFeedback;

public class ResourceFeedbackModel {
  private int resourceFeedbackId;
  private int resourceId;
  private String comment;

  protected ResourceFeedbackModel() {}

  public ResourceFeedbackModel(ResourceFeedback resourceFeedback) {
    this.resourceFeedbackId = resourceFeedback.getResourceFeedbackId();
    this.resourceId = resourceFeedback.getResource().getResourceId();
    this.comment = resourceFeedback.getComment();
  }

  public int getResourceFeedbackId() {
    return resourceFeedbackId;
  }

  public int getResourceId() {
    return resourceId;
  }

  public String getComment() {
    return comment;
  }
}
