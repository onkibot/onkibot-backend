package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Course;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer> {
    Optional<Course> findByCourseId(int courseId);
}
