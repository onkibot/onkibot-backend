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

@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/courses/{courseId}/attendees")
public class CourseAttendeeController {
  @Autowired private CourseRepository courseRepository;
  @Autowired private UserRepository userRepository;

  @RequestMapping(method = RequestMethod.GET)
  public List<UserModel> getAll(@PathVariable int courseId, HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Course course = assertCourse(courseId, user);
    return course.getAttendees().stream().map(UserModel::new).collect(Collectors.toList());
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/{userId}")
  public ResponseEntity<Void> put(@PathVariable int courseId, @PathVariable int userId,
      HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Course course = assertCourse(courseId, user);
    if (user.getIsInstructor()) {
      User addUser = assertUser(userId);
      addUser.getAttending().add(course);
      userRepository.save(addUser);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
  public ResponseEntity<Void> delete(@PathVariable int courseId, @PathVariable int userId,
      HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Course course = assertCourse(courseId, user);
    User deleteUser = assertUser(userId);
    if (user.getIsInstructor() || user.getUserId().equals(deleteUser.getUserId())) {
      deleteUser.getAttending().remove(course);
      userRepository.save(deleteUser);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
  }

  private Course assertCourse(int courseId, User user) {
    Course course = this.courseRepository
        .findByCourseId(courseId)
        .orElseThrow(() -> new CourseNotFoundException(courseId));
    if (!course.getAttendees().contains(user)) {
      throw new CourseNotFoundException(courseId);
    }
    return course;
  }

  private User assertUser(int userId) {
    return this.userRepository
        .findByUserId(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }
}
