package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.ExternalResource;
import com.onkibot.backend.database.entities.ExternalResourceApproval;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.ids.ExternalResourceApprovalId;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ExternalResourceApprovalRepository
    extends CrudRepository<ExternalResourceApproval, ExternalResourceApprovalId> {
  Collection<ExternalResourceApproval> findByExternalResourceApprovalIdApprovalUser(
      User approvalUser);

  Collection<ExternalResourceApproval> findByExternalResourceApprovalIdExternalResource(
      ExternalResource externalResource);

  Optional<ExternalResourceApproval> findByExternalResourceApprovalId(
      ExternalResourceApprovalId externalResourceApprovalId);
}
