package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.User;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used to serializer a {@link User}.
 * <p>
 * This shall ONLY be used when returning the model to the {@link User},
 * as it contains personal information such as their email.
 */
public class UserDetailModel extends UserModel {
  private String email;
  private List<CourseModel> attending;
  private List<ResourceModel> resources;
  private List<ExternalResourceModel> externalResources;

  protected UserDetailModel() {}

  public UserDetailModel(User user) {
    super(user);
    this.email = user.getEmail();
    // Add the Courses that the User is attending.
    this.attending =
        user.getAttending()
            .stream()
            .map(course -> new CourseModel(course, user))
            .collect(Collectors.toList());
    // Add the Resources that the User has created.
    this.resources =
        user.getResources()
            .stream()
            .map(resource -> new ResourceModel(resource, user))
            .collect(Collectors.toList());
    // Add the ExternalResources that the User has created.
    this.externalResources =
        user.getExternalResources()
            .stream()
            .map(externalResource -> new ExternalResourceModel(externalResource, user))
            .collect(Collectors.toList());
  }

  public String getEmail() {
    return email;
  }

  public List<CourseModel> getAttending() {
    return attending;
  }

  public List<ResourceModel> getResources() {
    return resources;
  }

  public List<ExternalResourceModel> getExternalResources() {
    return externalResources;
  }
}
