package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.ExternalResource;
import com.onkibot.backend.database.entities.Resource;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ExternalResourceRepository extends CrudRepository<ExternalResource, Integer> {
  Optional<ExternalResource> findByExternalResourceId(int externalResourceId);

  ExternalResource findByResourceAndUrl(Resource resource, String url);
}
