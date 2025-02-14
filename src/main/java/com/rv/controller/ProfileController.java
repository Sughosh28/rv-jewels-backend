package com.rv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class ProfileController {

//    @Autowired
//    private ProfileService profileService;
//
//    @GetMapping("/profile")
//    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.getUserProfile(token), HttpStatus.OK);
//    }
//
//    @GetMapping("/orders")
//    public ResponseEntity<?> getUserOrders(@RequestHeader("Authorization") String token) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.getUserOrders(token), HttpStatus.OK);
//    }
//
//    @GetMapping("/wishlist")
//    public ResponseEntity<?> getWishlist(@RequestHeader("Authorization") String token) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.getWishlist(token), HttpStatus.OK);
//    }
//
//    @GetMapping("/addresses")
//    public ResponseEntity<?> getShippingAddresses(@RequestHeader("Authorization") String token) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.getAddresses(token), HttpStatus.OK);
//    }
//
//    @GetMapping("/cart")
//    public ResponseEntity<?> getCart(@RequestHeader("Authorization") String token) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.getCart(token), HttpStatus.OK);
//    }
//
//    @GetMapping("/reviews")
//    public ResponseEntity<?> getUserReviews(@RequestHeader("Authorization") String token) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.getUserReviews(token), HttpStatus.OK);
//    }
//
//    @GetMapping("/notifications")
//    public ResponseEntity<?> getUserNotifications(@RequestHeader("Authorization") String token) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.getNotifications(token), HttpStatus.OK);
//    }
//
//    @GetMapping("/recent-views")
//    public ResponseEntity<?> getRecentlyViewedItems(@RequestHeader("Authorization") String token) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.getRecentViews(token), HttpStatus.OK);
//    }
//
//    @GetMapping("/saved-payments")
//    public ResponseEntity<?> getSavedPaymentMethods(@RequestHeader("Authorization") String token) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.getSavedPayments(token), HttpStatus.OK);
//    }
//
//    @GetMapping("/purchase-history/categories")
//    public ResponseEntity<?> getPurchaseHistoryByCategory(@RequestHeader("Authorization") String token) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.getPurchaseHistoryCategories(token), HttpStatus.OK);
//    }
//
//    @PostMapping("/reviews")
//    public ResponseEntity<?> addProductReview(
//            @RequestHeader("Authorization") String token,
//            @RequestBody ReviewDTO reviewDTO) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.addReview(token, reviewDTO), HttpStatus.CREATED);
//    }
//
//    @PostMapping("/cart")
//    public ResponseEntity<?> addToCart(
//            @RequestHeader("Authorization") String token,
//            @RequestBody CartItemDTO cartItem) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.addToCart(token, cartItem), HttpStatus.CREATED);
//    }
//
//    @PostMapping("/wishlist")
//    public ResponseEntity<?> addToWishlist(
//            @RequestHeader("Authorization") String token,
//            @RequestBody WishlistItemDTO wishlistItem) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.addToWishlist(token, wishlistItem), HttpStatus.CREATED);
//    }
//
//    @DeleteMapping("/addresses/{addressId}")
//    public ResponseEntity<?> removeAddress(@RequestHeader("Authorization") String token, @PathVariable Long addressId) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.removeAddress(token, addressId), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/order/{orderId}")
//    public ResponseEntity<?> removeAddress(@RequestHeader("Authorization") String token, @PathVariable Long orderId) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.removeOrder(token, orderId), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/cart/{productId}")
//    public ResponseEntity<?> removeFromCart(
//            @RequestHeader("Authorization") String token,
//            @PathVariable Long productId) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.removeFromCart(token, productId), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/wishlist/{productId}")
//    public ResponseEntity<?> removeFromWishlist(
//            @RequestHeader("Authorization") String token,
//            @PathVariable Long productId) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.removeFromWishlist(token, productId), HttpStatus.OK);
//    }
//
//    @PutMapping("/profile")
//    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token, @RequestBody UserDTO userDTO) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.updateProfile(token, userDTO), HttpStatus.OK);
//    }
//
//    @PutMapping("/addresses")
//    public ResponseEntity<?> addShippingAddress(@RequestHeader("Authorization") String token, @RequestBody AddressDTO address) {
//        if (token == null || token.isEmpty()) {
//            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(profileService.addAddress(token, address), HttpStatus.CREATED);
//    }
}
