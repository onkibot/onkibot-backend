package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.ResourceFeedback;
import com.onkibot.backend.database.entities.User;

/**
 * This class is used to serializer a {@link ResourceFeedback}.
 *
 * <p>It is essential that we do not show the {@link User} that left the feedback. This makes sure
 * that never happens.
 */
public class ResourceFeedbackModel {
  private int resourceFeedbackId;
  private int resourceId;
  private String comment;
  private int difficulty;

  protected ResourceFeedbackModel() {}

  public ResourceFeedbackModel(ResourceFeedback resourceFeedback) {
    this.resourceFeedbackId = resourceFeedback.getResourceFeedbackId();
    this.resourceId = resourceFeedback.getResource().getResourceId();
    this.comment = resourceFeedback.getComment();
    this.difficulty = resourceFeedback.getDifficulty();
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

  public int getDifficulty() {
    return difficulty;
  }
}
