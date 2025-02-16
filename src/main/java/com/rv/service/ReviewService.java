package com.rv.service;

import com.rv.dto.ReviewRequest;
import com.rv.jwt.JwtService;
import com.rv.model.Products;
import com.rv.model.Review;
import com.rv.model.UserEntity;
import com.rv.repository.ProductRepository;
import com.rv.repository.ReviewRepository;
import com.rv.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, JwtService jwtService, UserRepository userRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @CacheEvict(value = {"reviews", "averageRating", "totalReviews"}, key = "#productId")
    public ResponseEntity<?> addReview(String token, Long productId, ReviewRequest reviewRequest) {
        try {
            Long userId = jwtService.extractUserId(token);

            Optional<UserEntity> userEntity = userRepository.findById(userId);
            if (userEntity.isEmpty()) {
                return new ResponseEntity<>("User does not exist!", HttpStatus.UNAUTHORIZED);
            }
            Optional<Products> productEntity = productRepository.findById(productId);
            if (productEntity.isEmpty()) {
                return new ResponseEntity<>("Product does not exist!", HttpStatus.NOT_FOUND);
            }
            Review reviewEntity = new Review();
            reviewEntity.setReviewImages(reviewRequest.getReviewImages());
            reviewEntity.setUser(userEntity.get());
            reviewEntity.setReviewDate(LocalDateTime.now());
            reviewEntity.setComment(reviewRequest.getComment());
            reviewEntity.setRating(reviewRequest.getRating());
            reviewEntity.setProduct(productEntity.get());
            reviewEntity.setVerifiedPurchase(true);
            reviewRepository.save(reviewEntity);
            return new ResponseEntity<>("Review posted!", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Cacheable(value = "reviews", key = "#productId")
    public ResponseEntity<?> getReviews(Long productId) {
        System.out.println("Review table DB hit");
        try {
            Products product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist!"));

            List<Review> allReviews = reviewRepository.findReviewsByProductIdOrderByReviewDateDesc(productId);
            if (allReviews.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No reviews found for this product");
            }

            List<Map<String, Object>> simplifiedReviews = allReviews.stream()
                    .map(review -> Map.of(
                            "id", review.getId(),
                            "rating", review.getRating(),
                            "comment", review.getComment(),
                            "reviewDate", review.getReviewDate(),
                            "reviewImages", review.getReviewImages(),
                            "userName", review.getUser().getUsername()
                    ))
                    .collect(Collectors.toList());

            Map<String, Object> response = Map.of(
                    "totalReviews", allReviews.size(),
                    "reviews", simplifiedReviews
            );

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @CacheEvict(value = {"reviews", "averageRating", "totalReviews"}, key = "#productId")
    public ResponseEntity<?> deleteReview(String token, Long productId, Long reviewId) {
        try {
            Long userId = jwtService.extractUserId(token);
            Optional<UserEntity> userEntity = userRepository.findById(userId);
            if (userEntity.isEmpty()) {
                return new ResponseEntity<>("User does not exist!", HttpStatus.UNAUTHORIZED);
            }
            Optional<Products> productEntity = productRepository.findById(productId);
            if (productEntity.isEmpty()) {
                return new ResponseEntity<>("Product does not exist!", HttpStatus.NOT_FOUND);
            }
            Optional<Review> reviewEntity = reviewRepository.findById(reviewId);
            if (reviewEntity.isEmpty()) {
                return new ResponseEntity<>("Review does not exist!", HttpStatus.NOT_FOUND);
            }
            if (!reviewEntity.get().getProduct().getId().equals(productId) ||
                    !reviewEntity.get().getUser().getId().equals(userId)) {
                return new ResponseEntity<>("Not authorized to delete this review", HttpStatus.FORBIDDEN);
            }
            reviewRepository.deleteById(reviewId);
            return new ResponseEntity<>("Review delete successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Cacheable(value = "averageRating", key = "#productId")
    public double calculateProductAverageRating(Long productId) {
        List<Review> reviews = reviewRepository.findReviewsByProductId(productId);
        if (reviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .sum();

        return totalRating / reviews.size();
    }

    @Cacheable(value = "totalReviews", key = "#productId")
    private Long countTotalReviews(Long productId) {
        return reviewRepository.countReviewsByProductId(productId);
    }
}
