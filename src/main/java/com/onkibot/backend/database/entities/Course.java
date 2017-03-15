package com.onkibot.backend.database.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class Course {
    @Id @GeneratedValue
    private Integer courseId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "course")
    private List<Category> categories;

    @ManyToMany(mappedBy = "attending")
    private List<User> attendees;

    protected Course() { }

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
        this.categories = new ArrayList<>();
        this.attendees = new ArrayList<>();
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

    public List<Category> getCategories() {
        return categories;
    }

    public List<User> getAttendees() {
        return attendees;
    }
}
