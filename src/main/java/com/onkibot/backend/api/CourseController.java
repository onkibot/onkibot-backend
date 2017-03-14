package com.onkibot.backend.api;

import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.models.CourseInputModel;
import com.onkibot.backend.models.CourseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;

    @RequestMapping(method = RequestMethod.GET, params = {"courseId"})
    public CourseModel get(
            @RequestParam int courseId
    ) {
        Course course = courseRepository.findOne(courseId);
        if (course != null) {
            return new CourseModel(course);
        } else {
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<CourseModel> getAll() {
        ArrayList<CourseModel> models = new ArrayList<>();
        courseRepository.findAll().forEach(course -> models.add(new CourseModel(course)));
        return models;
    }

    @RequestMapping(method = RequestMethod.POST)
    public CourseModel post(
            @RequestBody CourseInputModel courseInput
    ) {
        Course course = courseRepository.save(new Course(courseInput.getName(), courseInput.getDescription()));
        return new CourseModel(course);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(
            @RequestParam int courseId
    ) {
        courseRepository.delete(courseId);
    }
}
