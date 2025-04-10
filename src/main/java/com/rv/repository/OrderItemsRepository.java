package com.rv.repository;

import com.rv.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, UUID> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
