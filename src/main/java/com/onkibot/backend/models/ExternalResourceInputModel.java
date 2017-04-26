package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.User;

/**
 * This class is used in the {@link com.onkibot.backend.api.ExternalResourceController}
 * when a {@link User} is attempting to create a new {@link com.onkibot.backend.database.entities.ExternalResource}.
 */
public class ExternalResourceInputModel {
  private String title;
  private String comment;
  private String url;

  protected ExternalResourceInputModel() {}

  public ExternalResourceInputModel(String title, String comment, String url) {
    this.title = title;
    this.comment = comment;
    this.url = url;
  }

  public String getTitle() {
    return title;
  }

  public String getComment() {
    return title;
  }

  public String getUrl() {
    return url;
  }
}
