package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {
    Optional<Category> findByCategoryId(int categoryId);
}
