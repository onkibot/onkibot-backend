package com.onkibot.backend.api;

import com.onkibot.backend.database.entities.Category;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.exceptions.CategoryNotFoundException;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.models.CategoryInputModel;
import com.onkibot.backend.models.CategoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @RequestMapping(method = RequestMethod.GET)
    Collection<CategoryModel> readCategories(@PathVariable int courseId) {
        Course course = this.assertCourse(courseId);
        return course.getCategories().stream().map(CategoryModel::new).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<CategoryModel> add(@PathVariable int courseId, @RequestBody CategoryInputModel categoryInput) {
        Course course = this.assertCourse(courseId);
        Category newCategory = categoryRepository.save(new Category(course, categoryInput.getName(), categoryInput.getDescription()));
        return new ResponseEntity<>(new CategoryModel(newCategory), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{categoryId}")
    CategoryModel readCategory(@PathVariable int courseId, @PathVariable int categoryId) {
        Category category = this.assertCourseCategory(courseId, categoryId);
        return new CategoryModel(category);
    }

    private Course assertCourse(int courseId) {
        return this.courseRepository.findByCourseId(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    private Category assertCourseCategory(int courseId, int categoryId) {
        Course course = assertCourse(courseId);
        Category category = categoryRepository.findByCategoryId(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        if (category.getCourse().getCourseId().equals(course.getCourseId())) {
            throw new CategoryNotFoundException(categoryId);
        }
        return category;
    }
}