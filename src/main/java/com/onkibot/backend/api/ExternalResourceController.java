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
 * The ExternalResourceController controls the request done to the
 * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/externals API URL.
 */
@RestController
@RequestMapping(
    OnkibotBackendApplication.API_BASE_URL
        + "/courses/{courseId}/categories/{categoryId}/resources/{resourceId}/externals")
public class ExternalResourceController {
  @Autowired private ExternalResourceApprovalRepository externalResourceApprovalRepository;

  @Autowired private ExternalResourceRepository externalResourceRepository;

  @Autowired private ResourceRepository resourceRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private UserRepository userRepository;

  /**
   * This request requires a GET HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/externals/{externalResourceId}
   * API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceId The {@link Resource} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param externalResourceId The {@link ExternalResource} ID, this is handled by the PathVariable
   *     from Spring Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not
   *     found.
   * @throws ExternalResourceNotFoundException If an {@link ExternalResource} with the <code>
   *     externalResourceId</code> is not found.
   * @return The {@link ExternalResource} of the requested <code>externalResourceId</code>.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{externalResourceId}")
  ExternalResourceModel get(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int externalResourceId,
      HttpSession session) {
    // Make sure the entities exist.
    ExternalResource externalResource =
        ExternalResource.assertCourseCategoryExternalResource(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId,
            this.externalResourceRepository, externalResourceId);
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    return new ExternalResourceModel(externalResource, user);
  }

  /**
   * This request requires a GET HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/externals API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceId The {@link Resource} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not
   *     found.
   * @return A Collection of all the {@link ExternalResource} entities formatted through the {@link
   *     ExternalResourceModel}.
   */
  @RequestMapping(method = RequestMethod.GET)
  Collection<ExternalResourceModel> getAll(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      HttpSession session) {
    // Assert that the entities exist.
    Resource resource =
        Resource.assertCourseCategoryResource(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId);
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    return resource
        .getExternalResources()
        .stream()
        .map(externalResource -> new ExternalResourceModel(externalResource, user))
        .collect(Collectors.toList());
  }

  /**
   * This request requires a POST HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/externals API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceId The {@link Resource} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param externalResourceInput The input for the new {@link ExternalResource}.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not
   *     found.
   * @return The new {@link ExternalResource} entity formatted through the {@link
   *     ExternalResourceModel}.
   */
  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<ExternalResourceModel> post(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @RequestBody ExternalResourceInputModel externalResourceInput,
      HttpSession session) {

    // Assert that the entities exist.
    Resource resource =
        Resource.assertCourseCategoryResource(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId);
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);

    // Check if there is an existing resource with the input parameters in the database.
    ExternalResource existingExternalResource =
        externalResourceRepository.findByResourceAndUrl(resource, externalResourceInput.getUrl());

    // If the existingExternalResource is not null, it exists, throw a Conflict (409).
    if (existingExternalResource != null) {
      return new ResponseEntity<>(
          new ExternalResourceModel(existingExternalResource, user), HttpStatus.CONFLICT);
    }

    // Create the new ExternalResource and return it.
    ExternalResource newExternalResource =
        externalResourceRepository.save(
            new ExternalResource(
                resource,
                externalResourceInput.getTitle(),
                externalResourceInput.getComment(),
                externalResourceInput.getUrl(),
                user));
    return new ResponseEntity<>(
        new ExternalResourceModel(newExternalResource, user), HttpStatus.CREATED);
  }

  /**
   * This request requires a DELETE HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/externals/{externalResourceId}
   * API URL.
   *
   * <p>Returns a Forbidden if the user is not an instructor or the {@link ExternalResource} is not
   * the request {@link User}s' own {@link ExternalResource}.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param resourceId The {@link Resource} ID, this is handled by the PathVariable from Spring
   *     Boot.
   * @param externalResourceId The {@link ExternalResource} ID, this is handled by the PathVariable
   *     from Spring Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not
   *     found.
   * @throws ExternalResourceNotFoundException If an {@link ExternalResource} with the <code>
   *     externalResourceId</code> is not found.
   * @return An empty response with an appropriate HTTP status code.
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{externalResourceId}")
  public ResponseEntity<Void> delete(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int externalResourceId,
      HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    // Make sure the entities exist.
    ExternalResource externalResource =
        ExternalResource.assertCourseCategoryExternalResource(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId,
            this.externalResourceRepository, externalResourceId);
    // Check if the User is an instructor or is creator of the ExternalResource.
    if (!user.getIsInstructor()
        || !externalResource.getPublisherUser().getUserId().equals(user.getUserId())) {
      // The User is neither an Instructor nor the creator of the ExternalResource.
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    // Delete the ExternalResource and return an empty response.
    externalResourceRepository.delete(externalResource);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
