package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.*;
import com.onkibot.backend.database.ids.ExternalResourceApprovalId;
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

  @RequestMapping(method = RequestMethod.GET, value = "/{externalResourceId}")
  ExternalResourceModel get(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int externalResourceId,
      HttpSession session) {
    ExternalResource externalResource =
        this.assertCourseCategoryExternalResource(
            courseId, categoryId, resourceId, externalResourceId);
    return new ExternalResourceModel(
        externalResource, hasApprovedExternalResource(externalResource, session));
  }

  @RequestMapping(method = RequestMethod.GET)
  Collection<ExternalResourceModel> getAll(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      HttpSession session) {
    Resource resource = this.assertCourseCategoryResource(courseId, categoryId, resourceId);
    return resource
        .getExternalResources()
        .stream()
        .map(
            externalResource ->
                new ExternalResourceModel(
                    externalResource, hasApprovedExternalResource(externalResource, session)))
        .collect(Collectors.toList());
  }

  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<ExternalResourceModel> post(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @RequestBody ExternalResourceInputModel externalResourceInput,
      HttpSession session) {

    int userId = (int) session.getAttribute("userId");
    User user =
        userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(userId));
    Resource resource = this.assertCourseCategoryResource(courseId, categoryId, resourceId);
    ExternalResource newExternalResource =
        externalResourceRepository.save(
            new ExternalResource(resource, externalResourceInput.getUrl(), user));
    return new ResponseEntity<>(
        new ExternalResourceModel(newExternalResource, false), HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/{externalResourceId}")
  public ResponseEntity<Void> delete(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int externalResourceId,
      HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    ExternalResource externalResource =
        this.assertCourseCategoryExternalResource(
            courseId, categoryId, resourceId, externalResourceId);
    if (!user.getIsInstructor()
        || !externalResource.getPublisherUser().getUserId().equals(user.getUserId())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    externalResourceRepository.delete(externalResource);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  private Course assertCourse(int courseId) {
    return this.courseRepository
        .findByCourseId(courseId)
        .orElseThrow(() -> new CourseNotFoundException(courseId));
  }

  private Category assertCourseCategory(int courseId, int categoryId) {
    Course course = assertCourse(courseId);
    Category category =
        categoryRepository
            .findByCategoryId(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    if (!category.getCourse().getCourseId().equals(course.getCourseId())) {
      throw new CategoryNotFoundException(categoryId);
    }
    return category;
  }

  private Resource assertCourseCategoryResource(int courseId, int categoryId, int resourceId) {
    Category category = assertCourseCategory(courseId, categoryId);
    Resource resource =
        resourceRepository
            .findByResourceId(resourceId)
            .orElseThrow(() -> new ResourceNotFoundException(resourceId));
    if (!resource.getCategory().getCategoryId().equals(category.getCategoryId())) {
      throw new ResourceNotFoundException(categoryId);
    }
    return resource;
  }

  private ExternalResource assertCourseCategoryExternalResource(
      int courseId, int categoryId, int resourceId, int externalResourceId) {
    Resource resource = assertCourseCategoryResource(courseId, categoryId, resourceId);
    ExternalResource externalResource =
        externalResourceRepository
            .findByExternalResourceId(externalResourceId)
            .orElseThrow(() -> new ExternalResourceNotFoundException(externalResourceId));
    if (!externalResource.getResource().getResourceId().equals(resource.getResourceId())) {
      throw new ExternalResourceNotFoundException(categoryId);
    }
    return externalResource;
  }

  private boolean hasApprovedExternalResource(
      ExternalResource externalResource, HttpSession session) {
    User sessionUser = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    ExternalResourceApprovalId externalResourceApprovalId =
        new ExternalResourceApprovalId(externalResource, sessionUser);
    return externalResourceApprovalRepository
        .findByExternalResourceApprovalId(externalResourceApprovalId)
        .isPresent();
  }
}
