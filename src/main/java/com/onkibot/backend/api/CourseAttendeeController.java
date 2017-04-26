package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.exceptions.UserNotFoundException;
import com.onkibot.backend.models.UserModel;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The CourseAttendeeController controls the request done to the /courses/{courseId}/attendees API
 * URL.
 */
@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/courses/{courseId}/attendees")
public class CourseAttendeeController {
  @Autowired private CourseRepository courseRepository;

  @Autowired private UserRepository userRepository;

  /**
   * This request requires a GET HTTP request to the /courses/{courseId}/attendees API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @return A Collection of all the User entities formatted through the UserModel that are
   *     attending the specified Course.
   */
  @RequestMapping(method = RequestMethod.GET)
  public List<UserModel> getAll(@PathVariable int courseId, HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Course course = Course.assertCourse(courseRepository, courseId, user);
    return course.getAttendees().stream().map(UserModel::new).collect(Collectors.toList());
  }

  /**
   * This request requires a PUT HTTP request to the /courses/{courseId}/attendees/{userId} API URL.
   *
   * <p>Only a Instructor that is attending the course is allowed to add a user to the course.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param userId The {@link User} ID, this is handled by the PathVariable from Sprint Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws UserNotFoundException If a {@link User} with the <code>userId</code> is not found.
   * @return An empty response with an appropriate HTTP status code.
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{userId}")
  public ResponseEntity<Void> put(
      @PathVariable int courseId, @PathVariable int userId, HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    // Assert that the Course exists and that the user (instructor) is attending the Course.
    Course course = Course.assertCourse(courseRepository, courseId, user);
    // Check if the user is an Instructor.
    if (user.getIsInstructor()) {
      // Make sure the User with the userId parameter exists and add the user to the Course.
      User addUser = User.assertUser(this.userRepository, userId);
      addUser.getAttending().add(course);
      userRepository.save(addUser);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } else {
      // User is not an instructor, and therefore not allowed.
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /**
   * This request requires a DELETE HTTP request to the /courses/{courseId}/attendees/{userId} API
   * URL.
   *
   * <p>An instructor attending the course is able to delete other users, and users themselves are
   * also able to remove themselves from the course.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param userId The {@link User} ID, this is handled by the PathVariable from Sprint Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not
   *     found.
   * @throws UserNotFoundException If a {@link User} with the <code>userId</code> is not found.
   * @return An empty response with an appropriate HTTP status code.
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
  public ResponseEntity<Void> delete(
      @PathVariable int courseId, @PathVariable int userId, HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    // Make sure the Course exists and the User is attending the Course.
    Course course = Course.assertCourse(courseRepository, courseId, user);
    // Make sure the User with the ID userId exists.
    User deleteUser = User.assertUser(this.userRepository, userId);
    // Check if the user is an instructor or themselves.
    if (user.getIsInstructor() || user.getUserId().equals(deleteUser.getUserId())) {
      // Remove the user from the course.
      deleteUser.getAttending().remove(course);
      userRepository.save(deleteUser);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // User is not an instructor, nor themselves, they're therefore not allowed to remove the user from the course.
    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
  }
}
