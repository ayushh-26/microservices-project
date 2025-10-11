package com.project.library.Reviews.irepository;

import com.project.library.Reviews.model.Review;

import java.util.List;

public interface IReviewRepository {

    // CREATE
    Review addReview(Review review);

    // READ
    List<Review> getAllReviews();

    Review getReviewById(String reviewId);

    List<Review> getReviewsByUserId(String userId);

    List<Review> getReviewsByBookId(String bookId);

    // UPDATE
    boolean updateReview(Review review);

    // DELETE
    boolean deleteReviewById(String reviewId);

    // CHECK if user already reviewed a book
    boolean hasUserReviewedBook(String userId, String bookId);

    // Pagination support
    List<Review> getReviewsPaginated(int offset, int size);

    int getTotalReviewsCount();

}
