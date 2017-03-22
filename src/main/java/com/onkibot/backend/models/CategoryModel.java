package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Category;

public class CategoryModel {
    private int categoryId;
    private int courseId;
    private String name;
    private String description;

    protected CategoryModel() { }

    public CategoryModel(Category category) {
        this.categoryId = category.getCategoryId();
        this.courseId = category.getCourse().getCourseId();
        this.name = category.getName();
        this.description = category.getDescription();
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
