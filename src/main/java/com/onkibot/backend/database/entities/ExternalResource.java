package com.onkibot.backend.database.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;

@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"resource_id", "url"}))
@Entity
public class ExternalResource implements Serializable {
  @Id @GeneratedValue private Integer externalResourceId;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Column(nullable = false, length = 50)
  private String title;

  @Lob
  @Column(nullable = false)
  private String comment;

  @Column(nullable = false, length = 2083)
  private String url;

  @ManyToOne
  @JoinColumn(name = "publisher_user_id")
  private User publisherUser;

  @OneToMany(mappedBy = "externalResourceApprovalId.externalResource", cascade = CascadeType.REMOVE)
  @OrderBy("external_resource_id")
  private Set<ExternalResourceApproval> userApprovals;

  protected ExternalResource() {}

  public ExternalResource(
      Resource resource, String title, String comment, String url, User publisherUser) {
    this.resource = resource;
    this.title = title;
    this.comment = comment;
    this.url = url;
    this.publisherUser = publisherUser;
  }

  public Integer getExternalResourceId() {
    return externalResourceId;
  }

  public Resource getResource() {
    return resource;
  }

  public String getTitle() {
    return title;
  }

  public String getComment() {
    return comment;
  }

  public String getUrl() {
    return url;
  }

  public User getPublisherUser() {
    return publisherUser;
  }

  public Set<ExternalResourceApproval> getUserApprovals() {
    return userApprovals;
  }

  public boolean hasUserApproved(User user) {
    return getUserApprovals() != null
        && getUserApprovals()
            .stream()
            .anyMatch(approval -> approval.getApprovalUser().getUserId().equals(user.getUserId()));
  }
}
