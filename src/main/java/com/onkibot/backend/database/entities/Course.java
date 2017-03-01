package com.onkibot.backend.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Course {
    @Id @GeneratedValue
    private Integer courseId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    protected Course() { }

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
