package com.onkibot.backend.api;

import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.models.CourseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/course")
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
}
