package com.onkibot.backend.database.ids;

import com.onkibot.backend.database.entities.Course;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class CategoryId implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="course_id")
    public Course course;

    @Column(nullable = false)
    public String slug;

    protected CategoryId() {}

    public CategoryId(Course course, String slug) {
        this.course = course;
        this.slug = slug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryId that = (CategoryId) o;

        if (!course.equals(that.course)) return false;
        return slug.equals(that.slug);
    }

    @Override
    public int hashCode() {
        int result = course.hashCode();
        result = 31 * result + slug.hashCode();
        return result;
    }
}