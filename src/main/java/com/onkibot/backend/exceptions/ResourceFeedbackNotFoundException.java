package com.onkibot.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceFeedbackNotFoundException extends RuntimeException {

  public ResourceFeedbackNotFoundException(int resourceFeedbackId) {
    super("Could not find Resource Feedback ID '" + resourceFeedbackId + "'.");
  }
}
