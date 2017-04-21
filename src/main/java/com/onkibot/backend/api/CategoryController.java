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

@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/courses/{courseId}/categories")
public class CategoryController {
  @Autowired private CategoryRepository categoryRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private UserRepository userRepository;

  @RequestMapping(method = RequestMethod.GET, value = "/{categoryId}")
  CategoryModel get(@PathVariable int courseId, @PathVariable int categoryId, HttpSession session) {
    Category category = this.assertCourseCategory(courseId, categoryId);
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    return new CategoryModel(category, user);
  }

  @RequestMapping(method = RequestMethod.GET)
  Collection<CategoryModel> getAll(@PathVariable int courseId, HttpSession session) {
    Course course = this.assertCourse(courseId);
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    return course
        .getCategories()
        .stream()
        .map(category -> new CategoryModel(category, user))
        .collect(Collectors.toList());
  }

  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<CategoryModel> post(
      @PathVariable int courseId,
      @RequestBody CategoryInputModel categoryInput,
      HttpSession session) {
    Course course = this.assertCourse(courseId);
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Category newCategory =
        categoryRepository.save(
            new Category(course, categoryInput.getName(), categoryInput.getDescription()));
    return new ResponseEntity<>(new CategoryModel(newCategory, user), HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/{categoryId}")
  public ResponseEntity<Void> delete(
      @PathVariable int courseId, @PathVariable int categoryId, HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Category category = this.assertCourseCategory(courseId, categoryId);
    if (!user.getIsInstructor() || !user.isAttending(category.getCourse())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    categoryRepository.delete(category);
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
}
