package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.ExternalResource;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ExternalResourceRepository extends CrudRepository<ExternalResource, Integer> {
  Optional<ExternalResource> findByExternalResourceId(int externalResourceId);
}
