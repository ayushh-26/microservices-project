package com.project.library.Reviews.service;

import com.project.library.Reviews.dto.ReviewDTO;
import com.project.library.Reviews.model.Review;
import com.project.library.Reviews.repository.ReviewRepositoryImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepositoryImpl reviewRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RestClient restClient;

    private final String ISSUE_SERVICE_URL = "http://localhost:9003/issues/user/";

    // ------------------- CREATE -------------------
    public ReviewDTO addReview(ReviewDTO dto) {
        // 1. Verify if user has issued the book
        String url = ISSUE_SERVICE_URL + dto.getUserId();
        List<Map<String, Object>> userIssues;

        try {
            // RestClient GET request returning List of Maps
            userIssues = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(List.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Cannot connect to issue service or invalid response.");
        }

        boolean hasIssued = userIssues.stream()
                .anyMatch(issue -> dto.getBookId().equals(issue.get("bookId")));

        if (!hasIssued)
            throw new RuntimeException("User has not issued this book yet.");

        // 2. Map DTO to entity and generate reviewId
        Review review = modelMapper.map(dto, Review.class);
        review.setReviewId("R" + UUID.randomUUID().toString().substring(0, 8));

        Review created = reviewRepository.addReview(review);
        return modelMapper.map(created, ReviewDTO.class);
    }

    // ------------------- READ -------------------
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.getAllReviews().stream()
                .map(r -> modelMapper.map(r, ReviewDTO.class))
                .collect(Collectors.toList());
    }

    public ReviewDTO getReviewById(String reviewId) {
        Review review = reviewRepository.getReviewById(reviewId);
        return modelMapper.map(review, ReviewDTO.class);
    }

    public List<ReviewDTO> getReviewsByUserId(String userId) {
        return reviewRepository.getReviewsByUserId(userId).stream()
                .map(r -> modelMapper.map(r, ReviewDTO.class))
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByBookId(String bookId) {
        return reviewRepository.getReviewsByBookId(bookId).stream()
                .map(r -> modelMapper.map(r, ReviewDTO.class))
                .collect(Collectors.toList());
    }

    // ------------------- UPDATE -------------------
    public boolean updateReview(ReviewDTO dto) {
        Review review = modelMapper.map(dto, Review.class);
        return reviewRepository.updateReview(review);
    }

    // ------------------- DELETE -------------------
    public boolean deleteReview(String reviewId) {
        return reviewRepository.deleteReviewById(reviewId);
    }

    // ------------------- CHECK -------------------
    public boolean hasUserReviewedBook(String userId, String bookId) {
        return reviewRepository.hasUserReviewedBook(userId, bookId);
    }
}
