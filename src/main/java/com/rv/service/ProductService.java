package com.rv.service;

import com.rv.model.Products;
import com.rv.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReviewService reviewService;

    public ResponseEntity<?> addNewProduct(Products product) {
        try {

            Products newProduct = new Products();
            newProduct.setName(product.getName());
            newProduct.setDescription(product.getDescription());
            newProduct.setPrice(product.getPrice());
            newProduct.setDiscountedPrice(product.getDiscountedPrice());
            newProduct.setStockQuantity(product.getStockQuantity());
            newProduct.setCategory(product.getCategory());
            newProduct.setActive(product.getActive());
            newProduct.setColor(product.getColor());
            newProduct.setFeatured(product.getFeatured());
            newProduct.setAverageRating(product.getAverageRating());
            newProduct.setClarity(product.getClarity());
            newProduct.setTags(product.getTags());
            newProduct.setSpecifications(product.getSpecifications());
            newProduct.setImages(product.getImages());
            return new ResponseEntity<>(productRepository.save(newProduct), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getAllProducts() {
        try {
            List<Products> products = productRepository.findAll();
            for (Products product : products) {
                Double averageRating = reviewService.calculateProductAverageRating(product.getId());
                product.setAverageRating(averageRating);
            }
            productRepository.saveAll(products);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> updateProduct(Long productId, Products product) {
        try {
            Products productEntity = productRepository.findById(productId)
                    .orElseThrow(RuntimeException::new);
            productEntity.setName(product.getName());
            productEntity.setDescription(product.getDescription());
            productEntity.setPrice(product.getPrice());
            productEntity.setDiscountedPrice(product.getDiscountedPrice());
            productEntity.setStockQuantity(product.getStockQuantity());
            productEntity.setCategory(product.getCategory());
            productEntity.setActive(product.getActive());
            productEntity.setColor(product.getColor());
            productEntity.setFeatured(product.getFeatured());
            productEntity.setAverageRating(product.getAverageRating());
            productEntity.setClarity(product.getClarity());
            productEntity.setTags(product.getTags());
            productEntity.setSpecifications(product.getSpecifications());
            productEntity.setImages(product.getImages());
            productRepository.save(productEntity);
            return new ResponseEntity<>("Product updated successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteProduct(Long productId) {
        try {
            productRepository.deleteById(productId);
            return new ResponseEntity<>("Product deleted successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> searchProducts(String keyword) {
        try {
            List<Products> product = productRepository.findByNameContainingIgnoreCase(keyword);
            if (product.isEmpty()) {
                return new ResponseEntity<>("The product does not exist", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getProductById(Long productId) {
        try {
            Products product = productRepository.findById(productId)
                    .orElseThrow(RuntimeException::new);
            Double averageRating = reviewService.calculateProductAverageRating(productId);
            product.setAverageRating(averageRating);
            productRepository.save(product);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getProductsByCategory(Products.ProductCategory category) {
        try {
            List<Products> products = productRepository.findByCategory(category);
            if (products.isEmpty()) {
                return new ResponseEntity<>("No products found in the this category!", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<?> getProductsByPriceRange(double minPrice, double maxPrice) {
        try {
            return new ResponseEntity<>(productRepository.findByPriceBetween(minPrice, maxPrice), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> addBulkProducts(List<Products> products) {

        try {
            productRepository.saveAll(products);
            return new ResponseEntity<>("Products added successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> updateProductStock(Long productId, Integer quantity) {
        try {
            Products product = productRepository.findById(productId)
                    .orElseThrow(RuntimeException::new);
            product.setStockQuantity(quantity);
            productRepository.save(product);
            return new ResponseEntity<>("Product stock updated successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public Map<String, Object> getInventoryAnalytics() {
        try {
            Map<String, Object> analytics = new HashMap<>();

            long totalProducts = productRepository.count();

            List<Products> lowStockProducts = productRepository.findByStockQuantityLessThan(5);

            List<Products> outOfStockProducts = productRepository.findByStockQuantity(0);

            BigDecimal totalInventoryValue = productRepository.findAll().stream()
                    .map(p -> (p.getPrice())
                            .multiply(BigDecimal.valueOf(p.getStockQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            analytics.put("totalProducts", totalProducts);
            analytics.put("lowStockProducts", lowStockProducts);
            analytics.put("outOfStockProducts", outOfStockProducts);
            analytics.put("totalInventoryValue", totalInventoryValue);
            analytics.put("timestamp", new Date());

            return analytics;
        } catch (Exception e) {
            throw new RuntimeException("Error generating inventory analytics: " + e.getMessage());
        }
    }

    public Map<String, Object> getCategoryAnalytics() {
        try {
            Map<String, Object> categoryAnalytics = new HashMap<>();

            Map<Products.ProductCategory, List<Products>> productsByCategory = productRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(Products::getCategory));

            productsByCategory.forEach((category, products) -> {
                Map<String, Object> metrics = new HashMap<>();

                BigDecimal totalValue = products.stream()
                        .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                double averagePrice = products.stream()
                        .mapToDouble(p -> p.getPrice().doubleValue())
                        .average()
                        .orElse(0.0);

                metrics.put("productCount", products.size());
                metrics.put("totalInventoryValue", totalValue);
                metrics.put("averagePrice", averagePrice);
                metrics.put("averageRating", calculateAverageRating(products));

                categoryAnalytics.put(category.toString(), metrics);
            });

            categoryAnalytics.put("totalCategories", productsByCategory.size());
            categoryAnalytics.put("timestamp", new Date());

            return categoryAnalytics;
        } catch (Exception e) {
            throw new RuntimeException("Error generating category analytics: " + e.getMessage());
        }
    }

    private double calculateAverageRating(List<Products> products) {
        return products.stream()
                .mapToDouble(Products::getAverageRating)
                .average()
                .orElse(0.0);
    }


}
