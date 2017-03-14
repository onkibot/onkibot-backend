package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Category;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.ids.CategoryId;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, CategoryId> {
    Collection<Category> findByCategoryIdCourse(Course course);
}
