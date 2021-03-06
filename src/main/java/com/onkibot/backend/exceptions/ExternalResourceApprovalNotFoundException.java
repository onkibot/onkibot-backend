package com.onkibot.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExternalResourceApprovalNotFoundException extends RuntimeException {

  public ExternalResourceApprovalNotFoundException() {
    super("Could not find External Resource approval for your user");
  }
}
