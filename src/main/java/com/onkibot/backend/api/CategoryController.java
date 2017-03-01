package com.onkibot.backend.api;

import com.github.slugify.Slugify;
import com.onkibot.backend.database.entities.Category;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.ids.CategoryId;
import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.exceptions.CategoryNotFoundException;
import com.onkibot.backend.exceptions.CourseNotFoundException;
import com.onkibot.backend.models.CategoryInputModel;
import com.onkibot.backend.models.CategoryModel;
import com.onkibot.backend.models.CourseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/categories")
public class CategoryController {
    private Slugify slugifier = new Slugify();

    private final CategoryRepository categoryRepository;

    private final CourseRepository courseRepository;

    @Autowired
    CategoryController(CategoryRepository categoryRepository,
        CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<CategoryModel> readCategories(@PathVariable int courseId) {
        this.validateCourse(courseId);
        Course course = this.courseRepository.findOne(courseId);
        ArrayList<CategoryModel> models = new ArrayList<>();
        categoryRepository.findByCategoryIdCourse(course).forEach(category -> models.add(new CategoryModel(category)));
        return models;
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@PathVariable int courseId, @RequestBody CategoryInputModel categoryInput) {
        this.validateCourse(courseId);
        Course course = this.courseRepository.findOne(courseId);
        String categorySlug = slugifier.slugify(categoryInput.getName());
        CategoryId categoryId = new CategoryId(course, categorySlug);
        Category existingCategory = this.categoryRepository.findOne(categoryId);
        if (existingCategory != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            Category newCategory = categoryRepository.save(new Category(categoryId, categoryInput.getName(), categoryInput.getDescription()));
            return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{categorySlug}")
    CategoryModel readCategory(@PathVariable int courseId, @PathVariable String categorySlug) {
        this.validateCourse(courseId);
        Course course = this.courseRepository.findOne(courseId);
        CategoryId categoryId = new CategoryId(course, categorySlug);
        Category category = this.categoryRepository.findOne(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException(categoryId);
        } else {
            return new CategoryModel(category);
        }
    }

    private void validateCourse(int courseId) {
        this.courseRepository.findByCourseId(courseId).orElseThrow(
        () -> new CourseNotFoundException(courseId));
    }
}