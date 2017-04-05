package com.onkibot.backend.database.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
public class User implements Serializable {
  @Id @GeneratedValue private Integer userId;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String encodedPassword;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Date createdTime;

  @Column(nullable = false)
  private boolean isInstructor;

  @ManyToMany(cascade = CascadeType.PERSIST)
  @JoinTable(
    name = "attends",
    joinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId"),
    inverseJoinColumns = @JoinColumn(name = "courseId", referencedColumnName = "courseId")
  )
  private List<Course> attending;

  @OneToMany(mappedBy = "publisherUser")
  private List<Resource> resources;

  @OneToMany(cascade = CascadeType.PERSIST)
  @JoinTable(
    name = "approved_external_resources",
    joinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId"),
    inverseJoinColumns =
        @JoinColumn(name = "externalResourceId", referencedColumnName = "externalResourceId")
  )
  private List<ExternalResource> approvedExternalResources;

  protected User() {}

  public User(String email, String encodedPassword, String name, boolean isInstructor) {
    this.email = email;
    this.encodedPassword = encodedPassword;
    this.name = name;
    this.createdTime = new Date();
    this.isInstructor = isInstructor;
    this.attending = new ArrayList<>();
    this.resources = new ArrayList<>();
    this.approvedExternalResources = new ArrayList<>();
  }

  public Integer getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public String getEncodedPassword() {
    return encodedPassword;
  }

  public String getName() {
    return name;
  }

  public Date getCreatedTime() {
    return createdTime;
  }

  public boolean getIsInstructor() {
    return isInstructor;
  }

  public List<Course> getAttending() {
    return attending;
  }

  public List<Resource> getResources() {
    return resources;
  }

  public List<ExternalResource> getApprovedExternalResources() {
    return approvedExternalResources;
  }

  public boolean hasApprovedExternalResource(ExternalResource externalResource) {
    for (ExternalResource approvedExternalResource : approvedExternalResources) {
      if (approvedExternalResource.equals(externalResource)) {
        return true;
      }
    }
    return false;
  }
}
