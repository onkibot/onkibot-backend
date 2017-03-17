package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.ExternalResource;
import com.onkibot.backend.database.entities.Resource;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ExternalResourceRepository extends CrudRepository<ExternalResource, Integer> {
    Optional<ExternalResource> findByResource(Resource resource);
    Optional<ExternalResource> findByExternalResourceId(int externalResourceId);
}
