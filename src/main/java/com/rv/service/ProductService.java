package com.rv.service;

import com.rv.aws.AwsS3ImplService;
import com.rv.dto.ProductListResponseDTO;
import com.rv.dto.ProductPriceRangeListDTO;
import com.rv.dto.ProductResponseDTO;
import com.rv.dto.ProductSearchResponse;
import com.rv.model.Products;
import com.rv.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ReviewService reviewService;
    private final AwsS3ImplService awsS3ImplService;

    public ProductService(ProductRepository productRepository, ReviewService reviewService,AwsS3ImplService awsS3ImplService) {
        this.productRepository = productRepository;
        this.reviewService = reviewService;
        this.awsS3ImplService = awsS3ImplService;
    }

    public ResponseEntity<?> addNewProduct(Products product, List<MultipartFile> images) {
        try {
            if (product.getName() == null || product.getPrice() == null) {
                return new ResponseEntity<>("Product name and price are required!", HttpStatus.BAD_REQUEST);
            }

            List<String> imageUrls = (images != null && !images.isEmpty())
                    ? awsS3ImplService.uploadImages(images, "products")
                    : new ArrayList<>();

            product.setImages(imageUrls);
            Products savedProduct = productRepository.save(product);

            return new ResponseEntity<>(savedProduct, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }


}

    @Cacheable(value = "allProducts")
    public Map<String, Object> getAllProducts() {
        try {
            List<Products> products = productRepository.findAll();

            products.forEach(product -> {
                Double averageRating = reviewService.calculateProductAverageRating(product.getId());
                product.setAverageRating(averageRating);
            });

            productRepository.saveAll(products);

            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("status", "success");

            return response;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    @CacheEvict(value = {"products", "allProducts", "productsByCategory"}, allEntries = true)
    public ResponseEntity<?> updateProduct(Long productId, Products product) {
        try {
            Products productEntity = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

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

    @CacheEvict(value = {"products", "allProducts", "productsByCategory"}, allEntries = true)
    public ResponseEntity<?> deleteProduct(Long productId) {
        try {
            productRepository.deleteById(productId);
            return new ResponseEntity<>("Product deleted successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Cacheable(value = "searchedProducts", key = "#keyword")
    public ProductSearchResponse searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword must not be empty.");
        }

        List<Products> products = productRepository.findByNameContainingIgnoreCase(keyword);

        return new ProductSearchResponse(
                products.size(),
                products,
                keyword,
                products.isEmpty() ? "No products found" : null
        );
    }

    @Cacheable(value = "products", key = "#productId")
    public ProductResponseDTO getProductById(Long productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    Double averageRating = reviewService.calculateProductAverageRating(productId);
                    return new ProductResponseDTO(
                            product.getId(),
                            product.getName(),
                            product.getDescription(),
                            product.getPrice(),
                            averageRating,
                            "Product retrieved successfully");
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }


    @Cacheable(value = "productsByCategory", key = "#category")
    public ProductListResponseDTO getProductsByCategory(Products.ProductCategory category) {
        List<Products> products = productRepository.findByCategory(category);

        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found in this category!");
        }

        List<ProductResponseDTO> productDTOs = products.stream()
                .map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getAverageRating(),
                        "Product retrieved successfully"
                ))
                .toList();

        return new ProductListResponseDTO(category.name(), productDTOs.size(), productDTOs);
    }



    @Cacheable(value = "productsByPrice", key = "#minPrice + '-' + #maxPrice")
    public ProductPriceRangeListDTO getProductsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice <= minPrice) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid price range. Ensure minPrice >= 0 and maxPrice > minPrice.");
        }

        List<Products> products = productRepository.findByPriceBetween(minPrice, maxPrice);

        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found in the given price range.");
        }

        List<ProductResponseDTO> productDTOs = products.stream()
                .map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getAverageRating(),
                        "Product retrieved successfully"
                ))
                .toList();

        return new ProductPriceRangeListDTO(minPrice, maxPrice, productDTOs.size(), productDTOs);
    }


    public ResponseEntity<?> addBulkProducts(List<Products> products) {

        try {
            productRepository.saveAll(products);
            return new ResponseEntity<>("Products added successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @CacheEvict(value = {"products", "allProducts", "productsByCategory"}, allEntries = true)
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

    @Cacheable(value = "inventoryAnalytics", key = "'inventory'")
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

    @Cacheable(value = "categoryAnalytics", key = "'categories'")
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
