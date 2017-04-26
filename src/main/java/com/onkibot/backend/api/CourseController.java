package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.models.CourseInputModel;
import com.onkibot.backend.models.CourseModel;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The CourseController controls the request done to the /courses/{courseId}/attendees API URL.
 */
@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/courses")
public class CourseController {
  @Autowired private CourseRepository courseRepository;

  @Autowired private UserRepository userRepository;

  /**
   * This request requires a GET HTTP request to the /courses/{courseId} API URL.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not found.
   * @return The CourseModel of the requested <code>courseId</code>.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{courseId}")
  public CourseModel get(@PathVariable int courseId, HttpSession session) {
    // Make sure the Course exists and return it if so.
    Course course = Course.assertCourse(this.courseRepository, courseId);
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    return new CourseModel(course, user);
  }

  /**
   * This request requires a GET HTTP request to the /courses API URL.
   *
   * @param session The current session of the visitor.
   * @return A Collection of all the {@link Course} entities formatted through the {@link CourseModel}.
   */
  @RequestMapping(method = RequestMethod.GET)
  public List<CourseModel> getAll(HttpSession session) {
    // Get all the Course entities and return them as a Collection with CourseModels.
    ArrayList<CourseModel> models = new ArrayList<>();
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    courseRepository.findAll().forEach(course -> models.add(new CourseModel(course, user)));
    return models;
  }

  /**
   * This request requires a POST HTTP request to the /courses API URL.
   *
   * @param session The current session of the visitor.
   * @return The new {@link Course} entity formatted through the {@link CourseModel}.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<CourseModel> post(
      @RequestBody CourseInputModel courseInput, HttpSession session) {
    // Create the new Course.
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Course course =
        courseRepository.save(new Course(courseInput.getName(), courseInput.getDescription()));
    // Add the User to the attendees of the Course.
    user.getAttending().add(course);
    course.getAttendees().add(user);
    userRepository.save(user);
    // Return the response.
    return new ResponseEntity<>(new CourseModel(course, user), HttpStatus.CREATED);
  }

  /**
   * This request requires a DELETE HTTP request to the /courses/{courseId} API URL.
   * <p>
   * Only a Instructor that is attending the {@link Course} is allowed to delete the {@link Course}.
   *
   * @param courseId The {@link Course} ID, this is handled by the PathVariable from Spring Boot.
   * @param session The current session of the visitor.
   * @throws CourseNotFoundException If a {@link Course} with the <code>courseId</code> is not found.
   * @return An empty request with the HTTP 204 (No Content) Status Code.
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{courseId}")
  public ResponseEntity<Void> delete(@PathVariable int courseId, HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Course course = Course.assertCourse(this.courseRepository, courseId);
    /*
    Check if the user is an instructor and is attending the specified course.
    Returns a 403 (Forbidden) if not.
    */
    if (!user.getIsInstructor() || !user.isAttending(course)) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    // Delete the Course and return an empty response.
    courseRepository.delete(course);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
