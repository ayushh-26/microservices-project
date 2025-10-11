package com.project.library.Reviews.controller;

import com.project.library.Reviews.dto.ReviewDTO;
import com.project.library.Reviews.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // ------------------- CREATE -------------------
    /*
     * POST http://localhost:9004/reviews/add
     * Body example:
     * {
     * "userId":"U001",
     * "bookId":"B001",
     * "rating":5,
     * "comment":"Great book!"
     * }
     * Response example:
     * {
     * "reviewId":"R001",
     * "userId":"U001",
     * "bookId":"B001",
     * "rating":5,
     * "comment":"Great book!"
     * }
     */
    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO dto) {
        try {
            ReviewDTO created = reviewService.addReview(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ------------------- READ -------------------
    /*
     * GET http://localhost:9004/reviews/all
     * Response example:
     * [
     * {"reviewId":"R001","userId":"U001","bookId":"B001","rating":5,
     * "comment":"Great book!"},
     * {"reviewId":"R002","userId":"U002","bookId":"B002","rating":4,
     * "comment":"Nice read"}
     * ]
     */
    @GetMapping("/all")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    /*
     * GET http://localhost:9004/reviews/book/{bookId}
     * Example: /reviews/book/B001
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByBook(@PathVariable String bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(bookId));
    }

    /*
     * GET http://localhost:9004/reviews/user/{userId}
     * Example: /reviews/user/U001
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    // ------------------- UPDATE -------------------
    /*
     * PUT http://localhost:9004/reviews/update
     * Body example:
     * {
     * "reviewId":"R001",
     * "userId":"U001",
     * "bookId":"B001",
     * "rating":4,
     * "comment":"Updated comment"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateReview(@RequestBody ReviewDTO dto) {
        boolean updated = reviewService.updateReview(dto);
        return updated ? ResponseEntity.ok("Review updated successfully.")
                : ResponseEntity.badRequest().body("Failed to update review.");
    }

    // ------------------- DELETE -------------------
    /*
     * DELETE http://localhost:9004/reviews/delete/{reviewId}
     * Example: /reviews/delete/R001
     */
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable String reviewId) {
        boolean deleted = reviewService.deleteReview(reviewId);
        return deleted ? ResponseEntity.ok("Review deleted successfully.") : ResponseEntity.notFound().build();
    }

    // GET http://localhost:9004/reviews/nextpage?page=0&size=5
    @GetMapping("/nextpage")
    public ResponseEntity<Map<String, Object>> getReviewsPaginated(
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(reviewService.getReviewsPaginated(page, size));
    }

}
