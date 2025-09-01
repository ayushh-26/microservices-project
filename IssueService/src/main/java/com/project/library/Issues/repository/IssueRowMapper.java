package com.project.library.Issues.repository;

import com.project.library.Issues.model.Issue;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IssueRowMapper implements RowMapper<Issue> {

    @Override
    public Issue mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Issue(
                rs.getString("issue_id"),
                rs.getString("book_id"),
                rs.getString("user_id"),
                rs.getDate("issue_date"),
                rs.getDate("due_date"),
                rs.getDate("return_date"));
    }
}
