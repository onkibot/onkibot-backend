package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryModel {
    private int categoryId;
    private int courseId;
    private String name;
    private String description;
    private List<ResourceModel> resources;

    protected CategoryModel() { }

    public CategoryModel(Category category) {
        this.categoryId = category.getCategoryId();
        this.courseId = category.getCourse().getCourseId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.resources = category.getResources().stream().map(ResourceModel::new).collect(Collectors.toList());
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

    public List<ResourceModel> getResources() {
        return resources;
    }
}
