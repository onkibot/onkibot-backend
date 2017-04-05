package com.onkibot.backend.database.entities;

import com.onkibot.backend.database.ids.ExternalResourceApprovalId;
import java.io.Serializable;
import javax.persistence.*;

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
}
