package com.project.library.Issues.irepository;

import com.project.library.Issues.model.Issue;

import java.sql.Date;
import java.util.List;

public interface IIssueRepository {

    Issue addIssue(Issue issue);

    List<Issue> getAllIssues();

    Issue getIssueById(String issueId);

    boolean updateIssue(Issue issue);

    boolean deleteIssueById(String issueId);

    List<Issue> getIssuesByUserId(String userId);

    List<Issue> getIssuesByBookId(String bookId);

    List<Issue> getIssuesByIssueDate(Date issueDate);

    // Updated: Accepts a list of book IDs instead of single bookName
    List<Issue> getIssuesByBookName(List<String> bookIds);

    List<Issue> getIssuesPaginated(int offset, int size);
int getTotalIssuesCount();

}
