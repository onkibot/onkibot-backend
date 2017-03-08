package com.onkibot.backend.database.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
