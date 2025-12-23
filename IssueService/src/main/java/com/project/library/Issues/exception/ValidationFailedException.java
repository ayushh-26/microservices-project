package com.project.library.Issues.exception;

public class ValidationFailedException extends RuntimeException {
    public ValidationFailedException(String message) { super(message); }
}
