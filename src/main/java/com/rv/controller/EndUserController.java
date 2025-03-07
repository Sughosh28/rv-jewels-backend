package com.rv.controller;

import com.rv.model.Products;
import com.rv.service.OrderService;
import com.rv.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/end-user")
public class EndUserController {
    private final OrderService orderService;
    private final ProductService productService;

    public EndUserController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@RequestHeader("Authorization") String token, @PathVariable UUID orderId) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(orderService.getOrderDetails(authToken, orderId), HttpStatus.OK);
    }

    @GetMapping("/all-products")
    public ResponseEntity<?> getAllProducts(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Authentication is missing");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> response = productService.getAllProducts();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/products/search")
    public ResponseEntity<?> searchProducts(
            @RequestHeader("Authorization") String token,
            @RequestParam String keyword) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.searchProducts(keyword), HttpStatus.OK);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }

    @GetMapping("/products/category/{category}")
    public ResponseEntity<?> getProductsByCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Products.ProductCategory category) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.getProductsByCategory(category), HttpStatus.OK);
    }

    @GetMapping("/analytics/category-performance")
    public ResponseEntity<?> getCategoryAnalytics(
            @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.getCategoryAnalytics(), HttpStatus.OK);
    }
}
