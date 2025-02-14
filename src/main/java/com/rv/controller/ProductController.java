package com.rv.controller;

import com.rv.model.Products;
import com.rv.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/add-new-product")
    public ResponseEntity<?> addNewProduct(@RequestHeader("Authorization") String token, @RequestBody Products product) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.addNewProduct(product), HttpStatus.OK);
    }

    @PostMapping("/products/bulk")
    public ResponseEntity<?> addBulkProducts(
            @RequestHeader("Authorization") String token,
            @RequestBody List<Products> products) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.addBulkProducts(products), HttpStatus.OK);
    }

    @GetMapping("/all-products")
    public ResponseEntity<?> getAllProducts(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/products/search")
    public ResponseEntity<?> searchProducts(
            @RequestHeader("Authorization") String token,
            @RequestParam String keyword) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.searchProducts(keyword), HttpStatus.OK);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }

    @GetMapping("/products/category/{category}")
    public ResponseEntity<?> getProductsByCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Products.ProductCategory category) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.getProductsByCategory(category), HttpStatus.OK);
    }

    @GetMapping("/analytics/inventory")
    public ResponseEntity<?> getInventoryAnalytics(
            @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.getInventoryAnalytics(), HttpStatus.OK);
    }

    @GetMapping("/products/price-range")
    public ResponseEntity<?> getProductsByPriceRange(@RequestHeader("Authorization") String token
            , @RequestParam Double minPrice, @RequestParam Double maxPrice) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.getProductsByPriceRange(minPrice, maxPrice), HttpStatus.OK);
    }

    @GetMapping("/analytics/category-performance")
    public ResponseEntity<?> getCategoryAnalytics(
            @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.getCategoryAnalytics(), HttpStatus.OK);
    }

    @PutMapping("/update-product/{productId}")
    public ResponseEntity<?> updateProduct(@RequestHeader("Authorization") String token, @PathVariable Long productId, @RequestBody Products product) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.updateProduct(productId, product), HttpStatus.OK);
    }

    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<?> deleteProduct(@RequestHeader("Authorization") String token, @PathVariable Long productId) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.deleteProduct(productId), HttpStatus.OK);
    }


    @PatchMapping("/product/{productId}/stock")
    public ResponseEntity<?> updateProductStock(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.updateProductStock(productId, quantity), HttpStatus.OK);
    }
}
