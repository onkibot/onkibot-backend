package com.onkibot.backend.exceptions;

import com.onkibot.backend.database.ids.CategoryId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(CategoryId categoryId) {
        super("Could not find Category '" + categoryId.slug + "' under Course ID '" + categoryId.course.getCourseId() + "'.");
    }
}