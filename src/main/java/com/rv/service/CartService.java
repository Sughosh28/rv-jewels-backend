package com.rv.service;

import com.rv.dto.CartResponseDTO;
import com.rv.dto.UserBasicInfoDTO;
import com.rv.jwt.JwtService;
import com.rv.model.Cart;
import com.rv.model.Products;
import com.rv.model.UserEntity;
import com.rv.repository.CartRepository;
import com.rv.repository.ProductRepository;
import com.rv.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final JwtService jwtService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(JwtService jwtService, CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> addToCart(String authToken, Long productId) {
        Long userId = jwtService.extractUserId(authToken);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find the product by ID
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product already exists in user's cart
        Optional<Cart> existingCartItem = cartRepository.findByUserAndProduct(user, product);

        if (existingCartItem.isPresent()) {
            // Update quantity if product already in cart
            Cart cartItem = existingCartItem.get();
            cartRepository.save(cartItem);
        } else {
            // Create new cart item
            Cart newCartItem = new Cart();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setDate(LocalDate.now());
            newCartItem.setTime(LocalTime.now());
            cartRepository.save(newCartItem);
        }

        return ResponseEntity.ok("Product added to cart successfully");
    }


    public ResponseEntity<?> getCart(String authToken) {
        Long userId = jwtService.extractUserId(authToken);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Cart> cartItems = cartRepository.findByUser(user);
        List<CartResponseDTO> cartResponseDTOs = new ArrayList<>();

        for (Cart cartItem : cartItems) {
            CartResponseDTO cartResponseDTO = new CartResponseDTO();
            cartResponseDTO.setId(cartItem.getId());
            cartResponseDTO.setProduct(cartItem.getProduct());
            cartResponseDTO.setUser(new UserBasicInfoDTO(user.getUsername(), user.getEmail(), user.getId()));
            cartResponseDTOs.add(cartResponseDTO);
        }
        if(cartResponseDTOs.isEmpty()) {
            return new ResponseEntity<>("Cart is empty", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(cartResponseDTOs);
    }



    public ResponseEntity<?> removeFromCart(String substring, Long productId) {
        Long userId = jwtService.extractUserId(substring);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Cart> cartItemOpt = cartRepository.findByUserAndProduct(user, product);
        if (cartItemOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Item not found in cart");
        }

        Cart cartItem = cartItemOpt.get();
        cartRepository.delete(cartItem);
        return ResponseEntity.ok("Item removed from cart successfully");
    }
}
