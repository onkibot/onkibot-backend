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

  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<Void> post(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int externalResourceId,
      HttpSession session) {
    User sessionUser = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    ExternalResource externalResource =
        assertCourseCategoryExternalResource(courseId, categoryId, resourceId, externalResourceId);
    ExternalResourceApprovalId externalResourceApprovalId =
        new ExternalResourceApprovalId(externalResource, sessionUser);
    if (externalResourceApprovalRepository
        .findByExternalResourceApprovalId(externalResourceApprovalId)
        .isPresent()) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    externalResourceApprovalRepository.save(
        new ExternalResourceApproval(externalResourceApprovalId));
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int externalResourceId,
      HttpSession session) {
    User sessionUser = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    ExternalResource externalResource =
        assertCourseCategoryExternalResource(courseId, categoryId, resourceId, externalResourceId);
    ExternalResourceApproval externalResourceApproval =
        assertExternalResourceApproval(externalResource, sessionUser);
    externalResourceApprovalRepository.delete(externalResourceApproval);
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

  private ExternalResourceApproval assertExternalResourceApproval(
      ExternalResource externalResource, User approvalUser) {
    ExternalResourceApprovalId externalResourceApprovalId =
        new ExternalResourceApprovalId(externalResource, approvalUser);
    return this.externalResourceApprovalRepository
        .findByExternalResourceApprovalId(externalResourceApprovalId)
        .orElseThrow(ExternalResourceApprovalNotFoundException::new);
  }
}
