package com.rv.repository;

import com.rv.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    List<Review> findReviewsByProductIdOrderByReviewDateDesc(Long productId);

    List<Review> findReviewsByProductId(Long productId);

    Long countByProductId(Long productId);
}
