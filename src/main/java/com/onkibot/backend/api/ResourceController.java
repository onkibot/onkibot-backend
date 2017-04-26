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
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Category category = this.assertCourseCategory(courseId, categoryId);
    Set<Resource> resources = category.getResources();
    return resources
        .stream()
        .map(resource -> new ResourceModel(resource, user))
        .collect(Collectors.toCollection(LinkedHashSet::new));
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
        new Resource(
            category,
            resourceInput.getName(),
            resourceInput.getBody(),
            resourceInput.getComment(),
            user);
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

    Resource savedResource = resourceRepository.save(newResource);
    Iterable<ExternalResource> savedExternalResources =
        externalResourceRepository.save(newExternalResources);
    savedExternalResources.forEach(
        newExternalResource -> savedResource.getExternalResources().add(newExternalResource));

    return new ResponseEntity<>(new ResourceModel(savedResource, user), HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/{resourceId}")
  public ResponseEntity<Void> delete(
      @PathVariable int courseId,
      @PathVariable int categoryId,
      @PathVariable int resourceId,
      HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Resource resource = this.assertCourseCategoryResource(courseId, categoryId, resourceId);
    if (!user.getIsInstructor()
        || !resource.getPublisherUser().getUserId().equals(user.getUserId())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    resourceRepository.delete(resource);
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
}
