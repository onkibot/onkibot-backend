package com.onkibot.backend.database.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Resource {
  @Id @GeneratedValue private Integer resourceId;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @Column(nullable = false, length = 50)
  private String name;

  @Lob
  @Column(nullable = false)
  private String body;

  @ManyToOne
  @JoinColumn(name = "publisher_user_id")
  private User publisherUser;

  @ManyToMany(mappedBy = "resource")
  private List<ExternalResource> externalResources;

  protected Resource() {}

  public Resource(Category category, String name, String body, User publisherUser) {
    this.category = category;
    this.name = name;
    this.body = body;
    this.publisherUser = publisherUser;
    this.externalResources = new ArrayList<>();
  }

  public Integer getResourceId() {
    return resourceId;
  }

  public Category getCategory() {
    return category;
  }

  public String getName() {
    return name;
  }

  public String getBody() {
    return body;
  }

  public User getPublisherUser() {
    return publisherUser;
  }

  public List<ExternalResource> getExternalResources() {
    return externalResources;
  }
}
