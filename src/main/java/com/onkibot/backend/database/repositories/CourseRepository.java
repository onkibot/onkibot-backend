package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Integer> { }
