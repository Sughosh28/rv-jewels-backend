package com.rv.repository;

import com.rv.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
    List<Products> findByNameContainingIgnoreCase(String keyword);

    List<Products> findByCategory(Products.ProductCategory category);


    List<Products> findByPriceBetween(double minPrice, double maxPrice);

    List<Products> findByStockQuantityLessThan(int i);

    List<Products> findByStockQuantity(int i);
}
