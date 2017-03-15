package com.onkibot.backend.api;

import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.models.CourseInputModel;
import com.onkibot.backend.models.CourseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;

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
    public ResponseEntity<CourseModel> post(@RequestBody CourseInputModel courseInput) {
        Course course = courseRepository.save(new Course(courseInput.getName(), courseInput.getDescription()));
        return new ResponseEntity<>(new CourseModel(course), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam int courseId) {
        Course course = assertCourse(courseId);
        courseRepository.delete(course);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Course assertCourse(int courseId) {
        return this.courseRepository.findByCourseId(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
    }
}
