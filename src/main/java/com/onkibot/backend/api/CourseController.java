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

@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/courses")
public class CourseController {
  @Autowired private CourseRepository courseRepository;
  @Autowired private UserRepository userRepository;

  @RequestMapping(method = RequestMethod.GET, value = "/{courseId}")
  public CourseModel get(@PathVariable int courseId) {
    Course course = assertCourse(courseId);
    return new CourseModel(course);
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<CourseModel> getAll() {
    ArrayList<CourseModel> models = new ArrayList<>();
    courseRepository.findAll().forEach(course -> models.add(new CourseModel(course)));
    return models;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<CourseModel> post(
      @RequestBody CourseInputModel courseInput, HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Course course =
        courseRepository.save(new Course(courseInput.getName(), courseInput.getDescription()));
    user.getAttending().add(course);
    course.getAttendees().add(user);
    userRepository.save(user);
    return new ResponseEntity<>(new CourseModel(course), HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/{courseId}")
  public ResponseEntity<Void> delete(@PathVariable int courseId, HttpSession session) {
    User user = OnkibotBackendApplication.assertSessionUser(userRepository, session);
    Course course = assertCourse(courseId);
    if (!user.getIsInstructor() || !user.isAttending(course)) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    courseRepository.delete(course);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  private Course assertCourse(int courseId) {
    return this.courseRepository
        .findByCourseId(courseId)
        .orElseThrow(() -> new CourseNotFoundException(courseId));
  }
}
