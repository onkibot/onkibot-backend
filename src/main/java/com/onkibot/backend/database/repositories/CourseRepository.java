package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Course;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository<Course, Integer> {
  Optional<Course> findByCourseId(int courseId);
}
