package com.project.library.Issues.dto;

import java.sql.Date;

public class IssueDTO {

    private String issueId;

    private String userId;

    private String bookId;

    private Date issueDate;

    private Date dueDate;

    private Date returnDate;

    public IssueDTO() {}

    public IssueDTO(String issueId, String userId, String bookId, Date issueDate, Date dueDate, Date returnDate) {
        this.issueId = issueId;
        this.userId = userId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    // Getters & Setters
    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
}
