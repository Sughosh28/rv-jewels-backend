package com.rv.repository;

import com.rv.model.Cart;
import com.rv.model.Products;
import com.rv.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserAndProduct(UserEntity user, Products product);

    List<Cart> findByUser(UserEntity user);
}
