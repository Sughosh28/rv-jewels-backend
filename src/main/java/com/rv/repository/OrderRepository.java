package com.rv.repository;

import com.rv.model.Orders;
import com.rv.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Orders, UUID> {

    List<Orders> findByUser(UserEntity userEntity);

    @Query("select sum(o.totalAmount) from Orders o")
    Object getTotalRevenue();
}
