package com.onkibot.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends RuntimeException {

  public CategoryNotFoundException(int categoryId) {
    super("Could not find Category ID '" + categoryId + "'.");
  }
}
