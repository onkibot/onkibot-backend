package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Course;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends CrudRepository<Course, Integer> {
    Optional<Course> findByCourseId(int courseId);
}
