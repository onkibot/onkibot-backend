package com.onkibot.backend.database.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Category implements Serializable {
    @Id @GeneratedValue
    private Integer categoryId;

    @ManyToOne()
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    protected Category() { }

    public Category(Course course, String name, String description) {
        this.course = course;
        this.name = name;
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public Course getCourse() {
        return course;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
