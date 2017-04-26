package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.*;
import com.onkibot.backend.database.ids.ExternalResourceApprovalId;
import com.onkibot.backend.database.repositories.*;
import com.onkibot.backend.exceptions.*;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The ExternalResourceApprovalController controls the request done to the
 * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/externals/{externalResourceId}/approve
 * API URL.
 */
@RestController
@RequestMapping(
    OnkibotBackendApplication.API_BASE_URL
        + "/courses/{courseId}/categories/{categoryId}/resources/{resourceId}/externals/{externalResourceId}/approve")
public class ExternalResourceApprovalController {
  @Autowired private ExternalResourceApprovalRepository externalResourceApprovalRepository;

  @Autowired private ExternalResourceRepository externalResourceRepository;

  @Autowired private ResourceRepository resourceRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private UserRepository userRepository;

  /**
   * This request requires a PUT HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/externals/{externalResourceId}/approve
   * API URL.
   *
   * <p>Returns a {@link HttpStatus#CONFLICT} if the {@link User} has already approved the {@link
   * ExternalResource}.
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
  @RequestMapping(method = RequestMethod.PUT)
  ResponseEntity<Void> put(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int externalResourceId,
      HttpSession session) {
    User sessionUser = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    // Make sure the entities exist.
    ExternalResource externalResource =
        ExternalResource.assertCourseCategoryExternalResource(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId,
            this.externalResourceRepository, externalResourceId);
    // Generate a ExternalResourceApprovalId
    ExternalResourceApprovalId externalResourceApprovalId =
        new ExternalResourceApprovalId(externalResource, sessionUser);
    // Check if the user has already approved the External Resource.
    if (externalResourceApprovalRepository
        .findByExternalResourceApprovalId(externalResourceApprovalId)
        .isPresent()) {
      // User has already approved the ExternalResource, return a Conflict message.
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    // User has not approved the ExternalResource earlier, approve it and return a response.
    externalResourceApprovalRepository.save(
        new ExternalResourceApproval(externalResourceApprovalId));
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  /**
   * This request requires a DELETE HTTP request to the
   * /courses/{courseId}/categories/{categoryId}/resources/{resourceId}/externals/{externalResourceId}/approve
   * API URL.
   *
   * <p>Returns a {@link HttpStatus#FORBIDDEN} if the user is trying to delete another {@link
   * User}s' approval.
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
   * @throws ExternalResourceApprovalNotFoundException If the {@link User} has not approved the
   *     {@link ExternalResource} with the <code>externalResourceId</code>.
   * @return An empty response with an appropriate HTTP status code.
   */
  @RequestMapping(method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int externalResourceId,
      HttpSession session) {
    User sessionUser = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    // Make sure the entities exist.
    ExternalResource externalResource =
        ExternalResource.assertCourseCategoryExternalResource(
            this.courseRepository, courseId,
            this.categoryRepository, categoryId,
            this.resourceRepository, resourceId,
            this.externalResourceRepository, externalResourceId);
    // Make sure the ExternalResourceApproval entity exists.
    ExternalResourceApproval externalResourceApproval =
        ExternalResourceApproval.assertExternalResourceApproval(
            this.externalResourceApprovalRepository, externalResource, sessionUser);
    // Check that the User of the ExternalResourceApproval is equal to the User that executed this request.
    if (!externalResourceApproval.getApprovalUser().getUserId().equals(sessionUser.getUserId())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    // Remove the ExternalResourceApproval.
    externalResourceApprovalRepository.delete(externalResourceApproval);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
