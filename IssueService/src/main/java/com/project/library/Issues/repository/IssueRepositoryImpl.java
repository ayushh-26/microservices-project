package com.project.library.Issues.repository;

import com.project.library.Issues.irepository.IIssueRepository;
import com.project.library.Issues.model.Issue;
import com.project.library.Issues.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

@Repository
public class IssueRepositoryImpl implements IIssueRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String generateIssueId() {
        try {
            String sql = "SELECT issue_id FROM issues ORDER BY issue_id DESC LIMIT 1";
            List<String> lastIdList = jdbcTemplate.queryForList(sql, String.class);
            String lastId = lastIdList.isEmpty() ? null : lastIdList.get(0);
            int nextNum = 1;
            if (lastId != null) {
                nextNum = Integer.parseInt(lastId.substring(1)) + 1;
            }
            return String.format("I%03d", nextNum);
        } catch (Exception e) {
            throw new DatabaseException("Failed to generate Issue ID: " + e.getMessage());
        }
    }

    @Override
    public Issue addIssue(Issue issue) {
        try {
            String issueId = generateIssueId();
            issue.setIssueId(issueId);
            String sql = "INSERT INTO issues (issue_id, book_id, user_id, issue_date, due_date, return_date) VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, issue.getIssueId(), issue.getBookId(), issue.getUserId(),
                    issue.getIssueDate(), issue.getDueDate(), issue.getReturnDate());
            return issue;
        } catch (Exception e) {
            throw new DatabaseException("Failed to add issue: " + e.getMessage());
        }
    }

    @Override
    public List<Issue> getAllIssues() {
        try {
            return jdbcTemplate.query("SELECT * FROM issues", new IssueRowMapper());
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch all issues: " + e.getMessage());
        }
    }

    @Override
    public Issue getIssueById(String issueId) {
        try {
            List<Issue> issues = jdbcTemplate.query("SELECT * FROM issues WHERE issue_id=?", new IssueRowMapper(), issueId);
            return issues.isEmpty() ? null : issues.get(0);
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch issue by ID: " + e.getMessage());
        }
    }

    @Override
    public boolean updateIssue(Issue issue) {
        try {
            String sql = "UPDATE issues SET book_id=?, user_id=?, issue_date=?, due_date=?, return_date=? WHERE issue_id=?";
            int updated = jdbcTemplate.update(sql, issue.getBookId(), issue.getUserId(), issue.getIssueDate(),
                    issue.getDueDate(), issue.getReturnDate(), issue.getIssueId());
            return updated > 0;
        } catch (Exception e) {
            throw new DatabaseException("Failed to update issue: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteIssueById(String issueId) {
        try {
            int deleted = jdbcTemplate.update("DELETE FROM issues WHERE issue_id=?", issueId);
            return deleted > 0;
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete issue: " + e.getMessage());
        }
    }

    @Override
    public List<Issue> getIssuesByUserId(String userId) {
        try {
            return jdbcTemplate.query("SELECT * FROM issues WHERE user_id=?", new IssueRowMapper(), userId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch issues by userId: " + e.getMessage());
        }
    }

    @Override
    public List<Issue> getIssuesByBookId(String bookId) {
        try {
            return jdbcTemplate.query("SELECT * FROM issues WHERE book_id=?", new IssueRowMapper(), bookId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch issues by bookId: " + e.getMessage());
        }
    }

    @Override
    public List<Issue> getIssuesByIssueDate(Date issueDate) {
        try {
            return jdbcTemplate.query("SELECT * FROM issues WHERE issue_date=?", new IssueRowMapper(), issueDate);
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch issues by issueDate: " + e.getMessage());
        }
    }

    @Override
    public List<Issue> getIssuesByBookName(List<String> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) return Collections.emptyList();
        try {
            String inSql = String.join(",", Collections.nCopies(bookIds.size(), "?"));
            String sql = "SELECT * FROM issues WHERE book_id IN (" + inSql + ")";
            return jdbcTemplate.query(sql, new IssueRowMapper(), bookIds.toArray());
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch issues by book names: " + e.getMessage());
        }
    }

    @Override
    public List<Issue> getIssuesPaginated(int offset, int size) {
        try {
            String sql = "SELECT * FROM issues LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, new IssueRowMapper(), size, offset);
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch paginated issues: " + e.getMessage());
        }
    }

    @Override
    public int getTotalIssuesCount() {
        try {
            String sql = "SELECT COUNT(*) FROM issues";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            throw new DatabaseException("Failed to count total issues: " + e.getMessage());
        }
    }
}
