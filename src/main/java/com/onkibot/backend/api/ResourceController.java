package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.*;
import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.database.repositories.ResourceRepository;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.CategoryNotFoundException;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.exceptions.ResourceNotFoundException;
import com.onkibot.backend.models.*;
import java.util.*;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
    OnkibotBackendApplication.API_BASE_URL
        + "/courses/{courseId}/categories/{categoryId}/resources")
public class ResourceController {
  @Autowired private ResourceRepository resourceRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private UserRepository userRepository;

  @RequestMapping(method = RequestMethod.GET, value = "/{resourceId}")
  ResourceModel get(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      HttpSession session) {
    User sessionUser = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Resource resource = this.assertCourseCategoryResource(courseId, categoryId, resourceId);
    return new ResourceModel(resource, sessionUser);
  }

  @RequestMapping(method = RequestMethod.GET)
  Collection<ResourceModel> getAll(
      @PathVariable int courseId, @PathVariable int categoryId, HttpSession session) {
    User sessionUser = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Category category = this.assertCourseCategory(courseId, categoryId);
    Set<Resource> resources = category.getResources();
    Set<ResourceModel> resourceModels = new LinkedHashSet<>();

    for (Resource resource : resources) {
      resourceModels.add(new ResourceModel(resource, sessionUser));
    }

    return resourceModels;
  }

  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<ResourceModel> post(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @RequestBody ResourceInputModel resourceInput,
      HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Category category = this.assertCourseCategory(courseId, categoryId);
    Resource newResource =
        resourceRepository.save(
            new Resource(category, resourceInput.getName(), resourceInput.getBody(), user));
    return new ResponseEntity<>(new ResourceModel(newResource), HttpStatus.CREATED);
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
}
