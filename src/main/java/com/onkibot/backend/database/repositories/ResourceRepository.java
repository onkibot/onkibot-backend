package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.Resource;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ResourceRepository extends CrudRepository<Resource, Integer> {
    Optional<Resource> findByResourceId(int repositoryId);
}