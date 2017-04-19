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

  @RequestMapping(method = RequestMethod.GET, value = "/{resourceFeedbackId}")
  ResourceFeedbackModel get(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int resourceFeedbackId) {
    ResourceFeedback resourceFeedback =
        this.assertCourseCategoryResourceFeedback(
            courseId, categoryId, resourceId, resourceFeedbackId);
    return new ResourceFeedbackModel(resourceFeedback);
  }

  @RequestMapping(method = RequestMethod.GET)
  Collection<ResourceFeedbackModel> getAll(
      @PathVariable int courseId, @PathVariable int categoryId, @PathVariable int resourceId) {
    Resource resource = this.assertCourseCategoryResource(courseId, categoryId, resourceId);
    return resource
        .getFeedback()
        .stream()
        .map(ResourceFeedbackModel::new)
        .collect(Collectors.toList());
  }

  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<ResourceFeedbackModel> post(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @RequestBody ResourceFeedbackInputModel resourceFeedbackInput,
      HttpSession session) {

    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Resource resource = this.assertCourseCategoryResource(courseId, categoryId, resourceId);
    ResourceFeedback newResourceFeedback =
        resourceFeedbackRepository.save(
            new ResourceFeedback(resource, resourceFeedbackInput.getComment(), user));
    return new ResponseEntity<>(new ResourceFeedbackModel(newResourceFeedback), HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/{resourceFeedbackId}")
  public ResponseEntity<Void> delete(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      @PathVariable int resourceFeedbackId,
      HttpSession session) {
    ResourceFeedback resourceFeedback =
        assertCourseCategoryResourceFeedback(courseId, categoryId, resourceId, resourceFeedbackId);
    User sessionUser = OnkibotBackendApplication.assertSessionUser(userRepository, session);

    if (!resourceFeedback.getFeedbackUser().getUserId().equals(sessionUser.getUserId())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    resourceFeedbackRepository.delete(resourceFeedback);
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

  private ResourceFeedback assertCourseCategoryResourceFeedback(
      int courseId, int categoryId, int resourceId, int resourceFeedbackId) {
    Resource resource = assertCourseCategoryResource(courseId, categoryId, resourceId);
    ResourceFeedback resourceFeedback =
        resourceFeedbackRepository
            .findByResourceFeedbackId(resourceFeedbackId)
            .orElseThrow(() -> new ResourceFeedbackNotFoundException(resourceFeedbackId));
    if (!resourceFeedback.getResource().getResourceId().equals(resource.getResourceId())) {
      throw new ExternalResourceNotFoundException(categoryId);
    }
    return resourceFeedback;
  }
}
