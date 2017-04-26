package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.Resource;
import com.onkibot.backend.database.entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** This class is used to serializer a {@link Resource}. */
public class ResourceModel {
  private int resourceId;
  private int categoryId;
  private String name;
  private String body;
  private String comment;
  private UserModel publisherUser;
  private List<ExternalResourceModel> externalResources;
  private ResourceFeedbackModel myFeedback;
  private List<ResourceFeedbackModel> feedback;
  private int averageFeedbackDifficulty;

  protected ResourceModel() {}

  public ResourceModel(Resource resource, User forUser) {
    this.resourceId = resource.getResourceId();
    this.categoryId = resource.getCategory().getCategoryId();
    this.name = resource.getName();
    this.body = resource.getBody();
    this.comment = resource.getComment();
    this.publisherUser = new UserModel(resource.getPublisherUser());
    // Add the list of the ExternalResources for the Resource.
    this.externalResources =
        resource
            .getExternalResources()
            .stream()
            .map(externalResource -> new ExternalResourceModel(externalResource, forUser))
            .collect(Collectors.toList());
    // Add the ResourceFeedback that the User gave this
    // ExternalResource if any, else it gets set to null.
    this.myFeedback =
        resource.getFeedbackForUser(forUser).map(ResourceFeedbackModel::new).orElse(null);
    if (forUser.getIsInstructor()) {
      this.feedback =
          resource
              .getFeedback()
              .stream()
              .map(ResourceFeedbackModel::new)
              .collect(Collectors.toList());
    } else {
      this.feedback = new ArrayList<>();
    }
    this.averageFeedbackDifficulty = resource.getAverageFeedbackDifficulty();
  }

  public int getResourceId() {
    return resourceId;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public String getName() {
    return name;
  }

  public String getBody() {
    return body;
  }

  public String getComment() {
    return comment;
  }

  public UserModel getPublisherUser() {
    return publisherUser;
  }

  public List<ExternalResourceModel> getExternalResources() {
    return externalResources;
  }

  public ResourceFeedbackModel getMyFeedback() {
    return myFeedback;
  }

  public List<ResourceFeedbackModel> getFeedback() {
    return feedback;
  }

  public int getAverageFeedbackDifficulty() {
    return averageFeedbackDifficulty;
  }
}
