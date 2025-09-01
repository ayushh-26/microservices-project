package com.project.library.Reviews.repository;

import com.project.library.Reviews.model.Review;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewRowMapper implements RowMapper<Review> {

    @Override
public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
    Review review = new Review();
    review.setReviewId(rs.getString("review_id"));
    review.setUserId(rs.getString("user_id"));
    review.setBookId(rs.getString("book_id"));
    review.setRating(rs.getInt("rating"));
    review.setComment(rs.getString("comment"));
    review.setReviewDate(rs.getTimestamp("review_date")); // matches DB column
    return review;
}

}
