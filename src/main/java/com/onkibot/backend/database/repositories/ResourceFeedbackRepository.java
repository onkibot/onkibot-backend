package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Resource;
import com.onkibot.backend.database.entities.ResourceFeedback;
import java.util.Optional;

import com.onkibot.backend.database.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface ResourceFeedbackRepository extends CrudRepository<ResourceFeedback, Integer> {
  Optional<ResourceFeedback> findByResourceFeedbackId(int resourceFeedbackId);
  ResourceFeedback findByResourceAndFeedbackUser(Resource resource, User feedbackUser);
}
