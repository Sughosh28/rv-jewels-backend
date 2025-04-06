package com.rv.controller;

import com.rv.model.Products;
import com.rv.service.ProductService;
import jakarta.mail.Multipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v2/admin")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add-new-product")
    public ResponseEntity<?> addNewProduct(@RequestHeader("Authorization") String token, @RequestPart("product") Products product, @RequestPart("images")List<MultipartFile> images) throws IOException {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.addNewProduct(product, images), HttpStatus.OK);
    }



    @GetMapping("/analytics/inventory")
    public ResponseEntity<?> getInventoryAnalytics(
            @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.getInventoryAnalytics(), HttpStatus.OK);
    }


    @GetMapping("/analytics/category-performance")
    public ResponseEntity<?> getCategoryAnalytics(
            @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.getCategoryAnalytics(), HttpStatus.OK);
    }

    @PutMapping("/update-product/{productId}")
    public ResponseEntity<?> updateProduct(@RequestHeader("Authorization") String token, @PathVariable Long productId, @RequestBody Products product) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.updateProduct(productId, product), HttpStatus.OK);
    }

    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<?> deleteProduct(@RequestHeader("Authorization") String token, @PathVariable Long productId) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.deleteProduct(productId), HttpStatus.OK);
    }


    @PatchMapping("/product/{productId}/stock")
    public ResponseEntity<?> updateProductStock(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.updateProductStock(productId, quantity), HttpStatus.OK);
    }

    @GetMapping("/total-products")
    public ResponseEntity<?> getTotalProducts(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.getTotalProducts(), HttpStatus.OK);
    }

    @GetMapping("/total-users")
    public ResponseEntity<?> getTotalUsers(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.getTotalUsers(), HttpStatus.OK);
    }

    @GetMapping("/total-orders")
    public ResponseEntity<?> getTotalOrders(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.getTotalOrders(), HttpStatus.OK);
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<?> getTotalRevenue(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.getTotalRevenue(), HttpStatus.OK);
    }
}
