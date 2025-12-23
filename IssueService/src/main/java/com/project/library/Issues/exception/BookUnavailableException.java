package com.project.library.Issues.exception;

public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String msg) {
        super(msg);
    }
}
