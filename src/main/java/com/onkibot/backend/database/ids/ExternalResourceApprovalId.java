package com.onkibot.backend.database.ids;

import com.onkibot.backend.database.entities.ExternalResource;
import com.onkibot.backend.database.entities.ExternalResourceApproval;
import com.onkibot.backend.database.entities.User;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * This is a Composite key for the {@link ExternalResourceApproval} entity.
 *
 * <p>It makes sure a {@link User} only can approve a {@link ExternalResource} once.
 */
@Embeddable
public class ExternalResourceApprovalId implements Serializable {
  @ManyToOne
  @JoinColumn(name = "external_resource_id")
  private ExternalResource externalResource;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User approvalUser;

  protected ExternalResourceApprovalId() {}

  public ExternalResourceApprovalId(ExternalResource externalResource, User approvalUser) {
    this.externalResource = externalResource;
    this.approvalUser = approvalUser;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ExternalResourceApprovalId that = (ExternalResourceApprovalId) o;

    if (!externalResource.equals(that.externalResource)) return false;
    return approvalUser.equals(that.approvalUser);
  }

  @Override
  public int hashCode() {
    int result = externalResource.hashCode();
    result = 31 * result + approvalUser.hashCode();
    return result;
  }

  public ExternalResource getExternalResource() {
    return externalResource;
  }

  public User getApprovalUser() {
    return approvalUser;
  }
}
