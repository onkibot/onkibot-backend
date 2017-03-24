package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Course;

import java.util.List;
import java.util.stream.Collectors;

public class CourseModel {
    private int courseId;
    private String name;
    private String description;
    private List<CategoryModel> categories;

    protected CourseModel() { }

    public CourseModel(Course course) {
        this.courseId = course.getCourseId();
        this.name = course.getName();
        this.description = course.getDescription();
        this.categories = course.getCategories().stream().map(CategoryModel::new).collect(Collectors.toList());
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

    public List<CategoryModel> getCategories() {
        return categories;
    }
}
