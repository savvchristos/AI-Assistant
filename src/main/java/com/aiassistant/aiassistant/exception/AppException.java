package com.aiassistant.aiassistant.exception;

import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {

    private final HttpStatus status;

    // ✅ Existing constructor
    public AppException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    // ✅ New constructor: message only, defaults to BAD_REQUEST
    public AppException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}