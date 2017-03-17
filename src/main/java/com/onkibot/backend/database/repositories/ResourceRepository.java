package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.Resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, Integer> {
    Optional<Resource> findByResourceId(int repositoryId);
}
