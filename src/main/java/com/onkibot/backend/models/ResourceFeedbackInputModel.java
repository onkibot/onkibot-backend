package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.ResourceFeedback;
import com.onkibot.backend.database.entities.User;

/**
 * This class is used in the {@link com.onkibot.backend.api.ResourceFeedbackController}
 * when a {@link User} is attempting to create a new {@link ResourceFeedback}.
 */
public class ResourceFeedbackInputModel {
  private String comment;
  private int difficulty;

  protected ResourceFeedbackInputModel() {}

  public ResourceFeedbackInputModel(String comment, int difficulty) {
    this.comment = comment;
    this.difficulty = difficulty;
  }

  public String getComment() {
    return comment;
  }

  public int getDifficulty() {
    return difficulty;
  }
}
