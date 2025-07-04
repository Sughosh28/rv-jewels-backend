package com.rv.service;

import com.rv.aws.AwsS3ImplService;
import com.rv.dto.ReviewDTO;
import com.rv.dto.ReviewRequest;
import com.rv.jwt.JwtService;
import com.rv.model.Products;
import com.rv.model.Review;
import com.rv.model.UserEntity;
import com.rv.repository.*;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
    private final AwsS3ImplService awsS3ImplService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderItemsRepository orderItemsRepository;



    public ReviewService(ReviewRepository reviewRepository, JwtService jwtService, UserRepository userRepository, ProductRepository productRepository, AwsS3ImplService awsS3ImplService,RedisTemplate<String, Object> redisTemplate,OrderItemsRepository orderItemsRepository) {
        this.reviewRepository = reviewRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.awsS3ImplService = awsS3ImplService;
        this.redisTemplate = redisTemplate;
        this.orderItemsRepository = orderItemsRepository;
    }


    @CacheEvict(value = {"reviews", "averageRating", "totalReviews"}, key = "#productId")
    public Map<String, Object> addReview(String token, Long productId, ReviewRequest reviewRequest, List<MultipartFile> images) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = jwtService.extractUserId(token);

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist!"));

            Products product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist!"));

            if (!orderItemsRepository.existsByUserIdAndProductId(userId, productId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must have placed an order for this product to post a review.");
            }

//            if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
//                response.put("status", "error");
//                response.put("message", "Rating must be between 1 and 5.");
//                return response;
//            }

            if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }

            if (images != null && images.stream().anyMatch(file -> file.isEmpty())) {
                throw new FileUploadException("Empty file detected in upload");
            }
            Review review = new Review();
            review.setUser(user);
            review.setReviewDate(LocalDateTime.now());
            review.setComment(reviewRequest.getComment());
            review.setRating(reviewRequest.getRating());
            review.setProduct(product);
            review.setVerifiedPurchase(true);

            List<String> urlList = awsS3ImplService.uploadImages(images, "reviews");
            review.setReviewImages(urlList);

            reviewRepository.save(review);

            response.put("status", "success");
            response.put("message", "Review posted successfully!");
            return response;

        } catch (ResponseStatusException e) {
            response.put("status", "error");
            response.put("message", e.getReason());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "An error occurred while posting the review.");
            return response;
        }
    }


    @Cacheable(value = "reviews", key = "#productId")
    public Map<String, Object> getReviews(Long productId) {

        try {
            Products product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist!"));

            List<Review> allReviews = reviewRepository.findReviewsByProductIdOrderByReviewDateDesc(productId);
            if (allReviews.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No reviews found for this product");
            }

            List<ReviewDTO> simplifiedReviews = allReviews.stream()
                    .map(review -> {
                        ReviewDTO dto = new ReviewDTO();
                        dto.setId(review.getId());
                        dto.setRating(review.getRating());
                        dto.setComment(review.getComment());
                        dto.setReviewDate(review.getReviewDate());
                        if (review.getReviewImages() != null) {
                            dto.setReviewImageUrls(review.getReviewImages());
                        } else {
                            dto.setReviewImageUrls(Collections.emptyList()); // Handle nulls
                        }
                        dto.setUserName(review.getUser().getUsername());
                        return dto;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = Map.of(
                    "totalReviews", allReviews.size(),
                    "reviews", simplifiedReviews
            );

            return response;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
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


    @Cacheable(value = "reviewCounts", key = "#productId")
    public Long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }


}
