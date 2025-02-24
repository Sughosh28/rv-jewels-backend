package com.rv.controller;

import com.rv.dto.ReviewRequest;
import com.rv.service.CacheInspectionService;
import com.rv.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/user")
public class ReviewController {
    private final ReviewService reviewService;
    private final CacheInspectionService cacheInspectionService;

    public ReviewController(ReviewService reviewService, CacheInspectionService cacheInspectionService) {
        this.reviewService = reviewService;
        this.cacheInspectionService = cacheInspectionService;
    }

    @PostMapping("/review/add-review/{productId}")
    public ResponseEntity<?> addReview(@RequestHeader("Authorization") String token, @PathVariable Long productId, @RequestPart ReviewRequest reviewRequest, @RequestPart(required = false) List<MultipartFile> images) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return new ResponseEntity<>( reviewService.addReview(authToken, productId, reviewRequest, images), HttpStatus.OK);
    }

    @GetMapping("/review/get-reviews/{productId}")
    public ResponseEntity<?> getReviews(@RequestHeader("Authorization") String token, @PathVariable Long productId) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }

        try {
            Map<String, Object> reviews = reviewService.getReviews(productId);
            return ResponseEntity.ok(reviews); // Construct ResponseEntity here
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/review/delete-review/{productId}/{reviewId}")
    public ResponseEntity<?> deleteReview(@RequestHeader("Authorization") String token,@PathVariable Long productId, @PathVariable Long reviewId) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return reviewService.deleteReview(authToken,productId, reviewId);
    }

    @GetMapping("/review/get-cache")
    public void getCache() {
         cacheInspectionService.printCacheContents("reviews");
    }

    @GetMapping("/review/get-count")
    public Long getReviewCount(@RequestParam Long productId) {
        return reviewService.getReviewCount(productId);
    }

}
