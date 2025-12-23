package com.project.library.Issues.dto;

public class IssueResponseDto {

    private String issueId;
    private String bookId;
    private String userId;
    private String message;

    public IssueResponseDto() {}

    public IssueResponseDto(String issueId, String bookId, String userId, String message) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.userId = userId;
        this.message = message;
    }

    // ------------------- GETTERS & SETTERS -------------------
    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
