package com.onkibot.backend.models;

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
