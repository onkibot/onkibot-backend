package com.onkibot.backend.database.entities;

import com.onkibot.backend.database.ids.CategoryId;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(
    name="category"
)
public class Category implements Serializable {
    @EmbeddedId
    private CategoryId categoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    protected Category() { }

    public Category(CategoryId categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    public CategoryId getCategoryId() { return categoryId; }

    public String getSlug() { return categoryId.slug; }

    public Course getCourse() { return categoryId.course; }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
