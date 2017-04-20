package com.onkibot.backend.models;

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
