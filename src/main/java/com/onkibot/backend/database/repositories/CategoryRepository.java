package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
    Optional<Category> findByCategoryId(int categoryId);
}
