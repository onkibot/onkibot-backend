package com.onkibot.backend.database.entities;

import com.onkibot.backend.database.ids.ExternalResourceApprovalId;
import com.onkibot.backend.database.repositories.ExternalResourceApprovalRepository;
import com.onkibot.backend.exceptions.*;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The ExternalResourceApproval Entity contains everything related to the ExternalResourceApproval of an {@link ExternalResource}.
 */
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"external_resource_id", "user_id"}))
@Entity
public class ExternalResourceApproval implements Serializable {
  @EmbeddedId private ExternalResourceApprovalId externalResourceApprovalId;

  protected ExternalResourceApproval() {}

  public ExternalResourceApproval(ExternalResourceApprovalId externalResourceApprovalId) {
    this.externalResourceApprovalId = externalResourceApprovalId;
  }

  public ExternalResourceApprovalId getExternalResourceApprovalId() {
    return externalResourceApprovalId;
  }

  public ExternalResource getExternalResource() {
    return externalResourceApprovalId.getExternalResource();
  }

  public User getApprovalUser() {
    return externalResourceApprovalId.getApprovalUser();
  }

  /**
   * Assert that the {@link ExternalResourceApproval} with the <code>externalResource</code> exists.
   *
   * @param externalResourceApprovalRepository The Repository service for the {@link ExternalResource} entity.
   * @param externalResource The {@link ExternalResource} entity we want to check that the <code>user</code> has approved.
   * @param approvalUser The {@link User} we want to check the approval for.
   * @throws ExternalResourceApprovalNotFoundException If a {@link ExternalResourceApproval} with the
   *                                                   <code>externalResource</code> for {@link User}
   *                                                   <code>approvalUser</code> is not found.
   * @return The {@link ExternalResourceApproval} entity if it exists.
   */
  public static ExternalResourceApproval assertExternalResourceApproval(
          ExternalResourceApprovalRepository externalResourceApprovalRepository,
          ExternalResource externalResource, User approvalUser) {
    // Generate the ExternalResourceApprovalId (based on externalResource and approvalUser).
    ExternalResourceApprovalId externalResourceApprovalId =
            new ExternalResourceApprovalId(externalResource, approvalUser);
    // Return the ExternalResourceApproval if it exists or throw an exception.
    return externalResourceApprovalRepository
            .findByExternalResourceApprovalId(externalResourceApprovalId)
            .orElseThrow(ExternalResourceApprovalNotFoundException::new);
  }
}
