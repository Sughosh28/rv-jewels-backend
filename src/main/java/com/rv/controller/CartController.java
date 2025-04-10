package com.rv.controller;

import com.rv.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/user")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addToCart(@RequestHeader("Authorization") String token, @RequestParam Long productId) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return cartService.addToCart(authToken, productId);
    }

    @GetMapping("/cart")
    public ResponseEntity<?> getCart(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return cartService.getCart(authToken);
    }


    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(
            @RequestHeader("Authorization") String authToken,
            @RequestParam Long productId) {
        return cartService.removeFromCart(authToken.substring(7), productId);
    }
}
