package com.onkibot.backend.models;

public class ExternalResourceInputModel {
  private String url;

  protected ExternalResourceInputModel() {}

  public ExternalResourceInputModel(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
}
