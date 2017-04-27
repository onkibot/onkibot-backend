package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.Category;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.CategoryNotFoundException;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.models.CategoryInputModel;
import com.onkibot.backend.models.CategoryModel;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The CategoryController controls the request done to the /courses/{courseId}/categories API URL.
 */
@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/courses/{courseId}/categories")
public class CategoryController {
  @Autowired private CategoryRepository categoryRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private UserRepository userRepository;

  /**
   * This request requires a GET HTTP request to the /courses/{courseId}/categories/{categoryId} API
   * URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Sprint
   *     Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @return The {@link CategoryModel} of the requested <code>categoryId</code>.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{categoryId}")
  CategoryModel get(@PathVariable int courseId, @PathVariable int categoryId, HttpSession session) {
    /*
    Assert that the Course and Category exists,
    throws a CourseNotFoundException if the Course is not found,
    and throws a CategoryNotFoundException if the Category is not found.
    */
    Category category =
        Category.assertCourseCategory(
            this.courseRepository, courseId, this.categoryRepository, categoryId);
    // Grab the session user.
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    // Return the Category entity formatted through the CategoryModel.
    return new CategoryModel(category, user);
  }

  /**
   * This request requires a GET HTTP request to the /courses/{courseId}/categories API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @return A Collection of all the {@link Category} entities formatted through the {@link
   *     CategoryModel}.
   */
  @RequestMapping(method = RequestMethod.GET)
  Collection<CategoryModel> getAll(@PathVariable int courseId, HttpSession session) {
    /*
    Assert that the Course and Category exists,
    throws a CourseNotFoundException if the Course is not found,
    */
    Course course = Course.assertCourse(this.courseRepository, courseId);
    // Grab the session user.
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    // Return the Collection.
    return course
        .getCategories()
        .stream()
        .map(category -> new CategoryModel(category, user))
        .collect(Collectors.toList());
  }

  /**
   * This request requires a POST HTTP request to the /courses/{courseId}/categories API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryInput The input for the new {@link Category}.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @return The new {@link Category} entity formatted through the {@link CategoryModel}.
   */
  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<CategoryModel> post(
      @PathVariable int courseId,
      @RequestBody CategoryInputModel categoryInput,
      HttpSession session) {
    /*
    Assert that the Course and Category exists,
    throws a CourseNotFoundException if the Course is not found,
    */
    Course course = Course.assertCourse(this.courseRepository, courseId);
    // Grab the session user.
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    // Create the new Category and save it.
    Category newCategory =
        categoryRepository.save(
            new Category(course, categoryInput.getName(), categoryInput.getDescription()));
    // Return the new Category with a 201 (Created) HTTP Status Code.
    return new ResponseEntity<>(new CategoryModel(newCategory, user), HttpStatus.CREATED);
  }

  /**
   * This request requires a DELETE HTTP request to the /courses/{courseId}/categories/{categoryId}
   * API URL.
   *
   * <p>Only a Instructor that is attending the {@link Course} is allowed to delete the {@link
   * Category}.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param categoryId The {@link Category} ID, this is handled by the PathVariable from Sprint
   *     Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws CategoryNotFoundException If a {@link Category} with the <code>categoryId</code> is not
   *     found.
   * @return An empty request with the HTTP 204 (No Content) Status Code.
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{categoryId}")
  public ResponseEntity<Void> delete(
      @PathVariable int courseId, @PathVariable int categoryId, HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Category category =
        Category.assertCourseCategory(
            this.courseRepository, courseId, this.categoryRepository, categoryId);
    /*
    Check if the user is an instructor and is attending the specified course.
    Returns a 403 (Forbidden) if not.
    */
    if (!user.getIsInstructor() || !user.isAttending(category.getCourse())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    // Delete the Category and return an empty response with the proper HTTP Status Code.
    categoryRepository.delete(category);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
