package com.onkibot.backend.models;

import java.util.List;

public class ResourceInputModel {
  private String name;
  private String body;
  private List<ExternalResourceInputModel> externalResources;

  protected ResourceInputModel() {}

  public ResourceInputModel(String name, String body, List<ExternalResourceInputModel> externalResources) {
    this.name = name;
    this.body = body;
    this.externalResources = externalResources;
  }

  public String getName() {
    return name;
  }

  public String getBody() {
    return body;
  }

  public List<ExternalResourceInputModel> getExternalResources() {
    return externalResources;
  }
}
