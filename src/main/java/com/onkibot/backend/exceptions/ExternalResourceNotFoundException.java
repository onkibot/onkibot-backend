package com.onkibot.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExternalResourceNotFoundException extends RuntimeException {

    public ExternalResourceNotFoundException(int externalResourceId) {
        super("Could not find External Resource ID '" + externalResourceId + "'.");
    }
}
