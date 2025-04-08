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
    public ResponseEntity<?> addToCart(@RequestHeader("Authorization") String token, @RequestParam Long productId, @RequestParam int quantity) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return cartService.addToCart(authToken, productId, quantity);
    }

    @GetMapping("/cart")
    public ResponseEntity<?> getCart(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return cartService.getCart(authToken);
    }


    @PutMapping("/update-quantity")
    public ResponseEntity<?> updateCartItemQuantity(
            @RequestHeader("Authorization") String authToken,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        return cartService.updateCartItemQuantity(authToken.substring(7), productId, quantity);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(
            @RequestHeader("Authorization") String authToken,
            @RequestParam Long productId) {
        return cartService.removeFromCart(authToken.substring(7), productId);
    }
}
