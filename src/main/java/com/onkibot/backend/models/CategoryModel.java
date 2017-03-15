package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Category;

public class CategoryModel {
    private final int categoryId;
    private final int courseId;
    private final String name;
    private final String description;

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
