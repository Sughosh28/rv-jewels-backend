package com.rv.controller;

import com.rv.dto.ReviewRequest;
import com.rv.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/review/add-review/{productId}")
    public ResponseEntity<?> addReview(@RequestHeader("Authorization") String token, @PathVariable Long productId, @RequestBody ReviewRequest reviewRequest) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return reviewService.addReview(authToken, productId, reviewRequest);
    }

    @GetMapping("/review/get-reviews/{productId}")
    public ResponseEntity<?> getReviews(@RequestHeader("Authorization") String token, @PathVariable Long productId) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return reviewService.getReviews(productId);
    }

    @DeleteMapping("/review/delete-review/{productId}/{reviewId}")
    public ResponseEntity<?> deleteReview(@RequestHeader("Authorization") String token,@PathVariable Long productId, @PathVariable Long reviewId) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return reviewService.deleteReview(authToken,productId, reviewId);
    }

}
