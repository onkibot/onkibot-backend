package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Category;

public class CategoryModel {
    private final String slug;
    private final String name;
    private final String description;

    public CategoryModel(Category category) {
        this.slug = category.getSlug();
        this.name = category.getName();
        this.description = category.getDescription();
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
