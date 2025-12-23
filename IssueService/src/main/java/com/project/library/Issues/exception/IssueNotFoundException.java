package com.project.library.Issues.exception;

public class IssueNotFoundException extends RuntimeException {
    public IssueNotFoundException(String msg) {
        super(msg);
    }
}
