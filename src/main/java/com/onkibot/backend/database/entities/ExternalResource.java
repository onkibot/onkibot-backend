package com.onkibot.backend.database.entities;

import java.io.Serializable;
import javax.persistence.*;

@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"resource_id", "url"}))
@Entity
public class ExternalResource implements Serializable {
  @Id @GeneratedValue private Integer externalResourceId;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Column(nullable = false)
  private String url;

  @ManyToOne
  @JoinColumn(name = "publisher_user_id")
  private User publisherUser;

  protected ExternalResource() {}

  public ExternalResource(Resource resource, String url, User publisherUser) {
    this.resource = resource;
    this.url = url;
    this.publisherUser = publisherUser;
  }

  public Integer getExternalResourceId() {
    return externalResourceId;
  }

  public Resource getResource() {
    return resource;
  }

  public String getUrl() {
    return url;
  }

  public User getPublisherUser() {
    return publisherUser;
  }
}
