package com.project.library.Reviews.repository;

import com.project.library.Reviews.irepository.IReviewRepository;
import com.project.library.Reviews.repository.ReviewRowMapper;
import com.project.library.Reviews.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewRepositoryImpl implements IReviewRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ------------------- CREATE -------------------
    @Override
    public Review addReview(Review review) {
        String sql = "INSERT INTO review (review_id, user_id, book_id, rating, comment, review_date) " +
                "VALUES (?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql,
                review.getReviewId(),
                review.getUserId(),
                review.getBookId(),
                review.getRating(),
                review.getComment());
        return review;
    }

    // ------------------- READ -------------------
    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM review";
        return jdbcTemplate.query(sql, new ReviewRowMapper());
    }

    @Override
    public Review getReviewById(String reviewId) {
        String sql = "SELECT * FROM review WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { reviewId }, new ReviewRowMapper());
    }

    @Override
    public List<Review> getReviewsByUserId(String userId) {
        String sql = "SELECT * FROM review WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[] { userId }, new ReviewRowMapper());
    }

    @Override
    public List<Review> getReviewsByBookId(String bookId) {
        String sql = "SELECT * FROM review WHERE book_id = ?";
        return jdbcTemplate.query(sql, new Object[] { bookId }, new ReviewRowMapper());
    }

    // ------------------- UPDATE -------------------
    @Override
    public boolean updateReview(Review review) {
        String sql = "UPDATE review SET rating = ?, comment = ? WHERE review_id = ?";
        int updated = jdbcTemplate.update(sql,
                review.getRating(),
                review.getComment(),
                review.getReviewId());
        return updated > 0;
    }

    // ------------------- DELETE -------------------
    @Override
    public boolean deleteReviewById(String reviewId) {
        String sql = "DELETE FROM review WHERE review_id = ?";
        int deleted = jdbcTemplate.update(sql, reviewId);
        return deleted > 0;
    }

    // ------------------- CHECK -------------------
    @Override
    public boolean hasUserReviewedBook(String userId, String bookId) {
        String sql = "SELECT COUNT(*) FROM review WHERE user_id = ? AND book_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[] { userId, bookId }, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public List<Review> getReviewsPaginated(int offset, int size) {
        String sql = "SELECT * FROM review LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new ReviewRowMapper(), size, offset);
    }

    @Override
    public int getTotalReviewsCount() {
        String sql = "SELECT COUNT(*) FROM review";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

}
