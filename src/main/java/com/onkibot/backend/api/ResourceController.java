package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.*;
import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.database.repositories.ExternalResourceRepository;
import com.onkibot.backend.database.repositories.ResourceRepository;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.CategoryNotFoundException;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.exceptions.ResourceNotFoundException;
import com.onkibot.backend.models.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The ResourceController controls the request done to the
 * /courses/{courseId}/categories/{categoryId}/resources API URL.
 */
@RestController
@RequestMapping(
    OnkibotBackendApplication.API_BASE_URL
        + "/courses/{courseId}/categories/{categoryId}/resources")
public class ResourceController {
  @Autowired private ResourceRepository resourceRepository;

  @Autowired private ExternalResourceRepository externalResourceRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private UserRepository userRepository;

  /**
   * This request requires a GET HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId} API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring Boot.
   * @param resourceId The {@link Resource} ID, this is handled by the PathVariable from Spring Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not found..
   * @return The {@link ResourceModel} of the requested <code>resourceId</code>.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{resourceId}")
  ResourceModel get(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      HttpSession session) {
    // Assert the User and entities, then return the ResourceModel.
    User sessionUser = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Resource resource = Resource.assertCourseCategoryResource(
        this.courseRepository, courseId,
        this.categoryRepository, categoryId,
        this.resourceRepository, resourceId
    );
    return new ResourceModel(resource, sessionUser);
  }

  /**
   * This request requires a GET HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not found.
   * @return A Collection of all the {@link Resource} entities formatted through the {@link ResourceModel}.
   */
  @RequestMapping(method = RequestMethod.GET)
  Collection<ResourceModel> getAll(
      @PathVariable int courseId, @PathVariable int categoryId, HttpSession session) {
    // Assert the User and entities, then return the Collection of ResourceModels.
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Category category = Category.assertCourseCategory(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId
    );
    Set<Resource> resources = category.getResources();
    return resources
        .stream()
        .map(resource -> new ResourceModel(resource, user))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  /**
   * This request requires a POST HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources API URL.
   * <p>
   * {@link ExternalResource}s sent in the <code>resourceInput</code> parameter is also created.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring Boot.
   * @param resourceInput The input for the new {@link Resource}.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not found.
   * @return The new {@link Resource} entity formatted through the {@link ResourceModel}.
   */
  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<ResourceModel> post(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @RequestBody ResourceInputModel resourceInput,
      HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    // Assert that the Course and Category exists.
    Category category = Category.assertCourseCategory(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId
    );
    // Create the new Resource.
    Resource newResource =
        new Resource(
            category,
            resourceInput.getName(),
            resourceInput.getBody(),
            resourceInput.getComment(),
            user);
    // Create the new ExternalResources that was sent with the request.
    Iterable<ExternalResource> newExternalResources =
        resourceInput
            .getExternalResources()
            .stream()
            .map(
                externalResourceInput ->
                    new ExternalResource(
                        newResource,
                        externalResourceInput.getTitle(),
                        externalResourceInput.getComment(),
                        externalResourceInput.getUrl(),
                        user))
            .collect(Collectors.toList());

    // Save the Resource.
    Resource savedResource = resourceRepository.save(newResource);
    // Save the ExternalResources.
    Iterable<ExternalResource> savedExternalResources =
        externalResourceRepository.save(newExternalResources);
    // Add the ExternalResources to the Resource.
    savedExternalResources.forEach(
        newExternalResource -> savedResource.getExternalResources().add(newExternalResource));

    // Return the new Resource.
    return new ResponseEntity<>(new ResourceModel(savedResource, user), HttpStatus.CREATED);
  }

  /**
   * This request requires a DELETE HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId} API URL.
   * <p>
   * Returns a Forbidden if the user is not an instructor or
   * the Resource is not the request Users' own Resource.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Spring Boot.
   * @param resourceId The {@link Resource} ID, this is handled by the PathVariable from Spring Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not found.
   * @throws ResourceNotFoundException If a {@link Resource} with the <code>resourceId</code> is not found.
   * @return An empty response with an appropriate HTTP status code.
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{resourceId}")
  public ResponseEntity<Void> delete(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      HttpSession session) {
    // Make sure the entities exist.
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Resource resource = Resource.assertCourseCategoryResource(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId
    );
    // Check if the User is an instructor or is creator of the Resource.
    if (!user.getIsInstructor()
        || !resource.getPublisherUser().getUserId().equals(user.getUserId())) {
      // The User is neither an Instructor nor the creator of the Resource.
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    // Delete the Resource and return an empty response.
    resourceRepository.delete(resource);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
