package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Resource;
import com.onkibot.backend.database.entities.User;
import java.util.List;

/**
 * This class is used in the {@link com.onkibot.backend.api.ResourceController} when a {@link User}
 * is attempting to create a new {@link Resource}.
 */
public class ResourceInputModel {
  private String name;
  private String body;
  private String comment;
  private List<ExternalResourceInputModel> externalResources;

  protected ResourceInputModel() {}

  public ResourceInputModel(
      String name,
      String body,
      String comment,
      List<ExternalResourceInputModel> externalResources) {
    this.name = name;
    this.body = body;
    this.comment = comment;
    this.externalResources = externalResources;
  }

  public String getName() {
    return name;
  }

  public String getBody() {
    return body;
  }

  public String getComment() {
    return comment;
  }

  public List<ExternalResourceInputModel> getExternalResources() {
    return externalResources;
  }
}
