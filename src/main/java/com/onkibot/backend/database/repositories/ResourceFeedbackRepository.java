package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.ResourceFeedback;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ResourceFeedbackRepository extends CrudRepository<ResourceFeedback, Integer> {
  Optional<ResourceFeedback> findByResourceFeedbackId(int resourceFeedbackId);
}
