package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Category;
import com.onkibot.backend.database.entities.User;

/**
 * This class is used in the {@link com.onkibot.backend.api.CategoryController}
 * when a {@link User} is attempting to create a new {@link Category}.
 */
public class CategoryInputModel {
  private String name;
  private String description;

  protected CategoryInputModel() {}

  public CategoryInputModel(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
