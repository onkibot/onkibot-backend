package com.onkibot.backend.models;

public class ResourceFeedbackInputModel {
  private String comment;

  protected ResourceFeedbackInputModel() {}

  public ResourceFeedbackInputModel(String comment) {
    this.comment = comment;
  }

  public String getComment() {
    return comment;
  }
}
