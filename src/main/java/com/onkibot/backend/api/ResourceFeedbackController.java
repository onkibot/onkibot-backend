package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.*;
import com.onkibot.backend.database.repositories.*;
import com.onkibot.backend.exceptions.*;
import com.onkibot.backend.models.*;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The ResourceFeedbackController controls the request done to the
 * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/feedback API URL.
 */
@RestController
@RequestMapping(
    OnkibotBackendApplication.API_BASE_URL
        + "/courses/{courseId}/categories/{categoryId}/resources/{resourceId}/feedback")
public class ResourceFeedbackController {
  @Autowired private ResourceFeedbackRepository resourceFeedbackRepository;

  @Autowired private ResourceRepository resourceRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private UserRepository userRepository;

  /**
   * This request requires a GET HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/feedback/{resourceFeedbackId}
   * API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceId The {@link Resource} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceFeedbackId The {@link ResourceFeedback} ID, this is handled by the PathVariable
   *     from Spring Boot.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not
   *     found.
   * @throws ResourceFeedbackNotFoundException If a {@link ResourceFeedback} with the <code>
   *     resourceFeedbackId</code> is not found.
   * @return The {@link ResourceFeedbackModel} of the requested <code>resourceFeedbackId</code>.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{resourceFeedbackId}")
  ResourceFeedbackModel get(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int resourceFeedbackId) {
    // Assert the entities and return the Model.
    ResourceFeedback resourceFeedback =
        ResourceFeedback.assertCourseCategoryResourceFeedback(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId,
            this.resourceFeedbackRepository, resourceFeedbackId);
    return new ResourceFeedbackModel(resourceFeedback);
  }

  /**
   * This request requires a GET HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/feedback API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceId The {@link Resource} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not
   *     found.
   * @return A Collection of all the {@link ResourceFeedback} entities formatted through the {@link
   *     ResourceFeedbackModel}.
   */
  @RequestMapping(method = RequestMethod.GET)
  Collection<ResourceFeedbackModel> getAll(
      @PathVariable int courseId, @PathVariable int categoryId, @PathVariable int resourceId) {
    // Assert the entities and return the Collection with Models.
    Resource resource =
        Resource.assertCourseCategoryResource(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId);
    return resource
        .getFeedback()
        .stream()
        .map(ResourceFeedbackModel::new)
        .collect(Collectors.toList());
  }

  /**
   * This request requires a POST HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/feedback API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceId The {@link Resource} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceFeedbackInput The input for the new {@link ResourceFeedback}.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not
   *     found.
   * @return The new {@link ResourceFeedback} entity formatted through the {@link
   *     ResourceFeedbackModel}.
   */
  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<ResourceFeedbackModel> post(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @RequestBody ResourceFeedbackInputModel resourceFeedbackInput,
      HttpSession session) {

    // Check that the Difficulty is between 1 and 5.
    if (resourceFeedbackInput.getDifficulty() <= 0 || resourceFeedbackInput.getDifficulty() > 5) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Assert the entities.
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Resource resource =
        Resource.assertCourseCategoryResource(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId);

    // Check if there is an existing ResourceFeedback with the input parameters in the database.
    ResourceFeedback existingResourceFeedback =
        resourceFeedbackRepository.findByResourceAndFeedbackUser(resource, user);

    // If the existingResourceFeedback is not null, it exists, throw a Conflict (409).
    if (existingResourceFeedback != null) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    // Create the new ResourceFeedback and return it.
    ResourceFeedback newResourceFeedback =
        resourceFeedbackRepository.save(
            new ResourceFeedback(
                resource,
                resourceFeedbackInput.getComment(),
                resourceFeedbackInput.getDifficulty(),
                user));
    return new ResponseEntity<>(new ResourceFeedbackModel(newResourceFeedback), HttpStatus.CREATED);
  }

  /**
   * This request requires a DELETE HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/feedback/{resourceFeedbackId}
   * API URL.
   *
   * <p>Returns a {@link HttpStatus#FORBIDDEN} if the {@link ResourceFeedback} is not the request
   * {@link User}s' own {@link ResourceFeedback}.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceId The {@link Resource} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceFeedbackId The {@link ResourceFeedback} ID, this is handled by the PathVariable
   *     from Spring Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not
   *     found.
   * @throws ResourceFeedbackNotFoundException If an {@link ResourceFeedback} with the <code>
   *     resourceFeedbackId</code> is not found.
   * @return An empty response with an appropriate HTTP status code.
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{resourceFeedbackId}")
  public ResponseEntity<Void> delete(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int resourceFeedbackId,
      HttpSession session) {
    // Assert the entities.
    ResourceFeedback resourceFeedback =
        ResourceFeedback.assertCourseCategoryResourceFeedback(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId,
            this.resourceFeedbackRepository, resourceFeedbackId);
    User sessionUser = OnkibotBackendApplication.assertSessionUser(userRepository, session);

    // Check if the User deleting the feedback is the same as the user that created the feedback.
    if (!resourceFeedback.getFeedbackUser().getUserId().equals(sessionUser.getUserId())) {
      // The User is not the feedback creator, return a Forbidden.
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    // Delete the feedback and return an empty response.
    resourceFeedbackRepository.delete(resourceFeedback);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
