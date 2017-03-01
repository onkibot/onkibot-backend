package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Course;

public class CourseModel {
    private final int courseId;
    private final String name;
    private final String description;

    public CourseModel(int courseId, String name, String description) {
        this.courseId = courseId;
        this.name = name;
        this.description = description;
    }

    public CourseModel(Course course) {
        this.courseId = course.getCourseId();
        this.name = course.getName();
        this.description = course.getDescription();
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
