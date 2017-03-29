package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Resource;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, Integer> {
  Optional<Resource> findByResourceId(int repositoryId);
}
