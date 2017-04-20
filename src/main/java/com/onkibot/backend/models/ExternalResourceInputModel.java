package com.onkibot.backend.models;

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
