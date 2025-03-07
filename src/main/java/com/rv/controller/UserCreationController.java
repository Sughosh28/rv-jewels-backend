package com.rv.controller;

import com.rv.dto.AuthTokenDTO;
import com.rv.dto.LoginDTO;
import com.rv.dto.PasswordResetRequestDTO;
import com.rv.jwt.JwtService;
import com.rv.model.Products;
import com.rv.model.RefreshToken;
import com.rv.model.UserEntity;
import com.rv.repository.RefreshTokenRepository;
import com.rv.service.OrderService;
import com.rv.service.ProductService;
import com.rv.service.RefreshTokenService;
import com.rv.service.UserService;
import com.rv.userdetails.PlatformUserDetails;
import com.rv.userdetails.PlatformUserDetailsService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final PlatformUserDetailsService platformUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OrderService orderService;
    private final ProductService productService;

    public UserController(UserService userService, RefreshTokenService refreshTokenService, JwtService jwtService, PlatformUserDetailsService platformUserDetailsService, RefreshTokenRepository refreshTokenRepository, OrderService orderService,ProductService productService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.platformUserDetailsService = platformUserDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.orderService = orderService;
        this.productService = productService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserEntity user) {
        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        return new ResponseEntity<>(userService.loginUser(loginDTO), HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException {
        return ResponseEntity.ok(userService.initiatePasswordReset(email));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequestDTO passwordResetRequestDTO) throws MessagingException {
        return ResponseEntity.ok(userService.resetPassword(passwordResetRequestDTO));
    }

    @PostMapping("/refresh-token")
    public AuthTokenDTO refreshToken(@RequestBody RefreshToken request) {
        String requestRefreshToken = request.getToken();
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(requestRefreshToken);

        if (tokenOptional.isPresent()) {
            RefreshToken storedToken = tokenOptional.get();
            RefreshToken validRefreshToken = refreshTokenService.verifyExpiration(storedToken);

            UserDetails userDetails = platformUserDetailsService.loadUserByUsername(validRefreshToken.getUser().getUsername());
            String newAccessToken = jwtService.generateToken(userDetails);
            RefreshToken newRefreshToken = refreshTokenService.generateRefreshToken(validRefreshToken.getUser().getUsername());
            AuthTokenDTO authTokenDto = new AuthTokenDTO();
            authTokenDto.setAccessToken(newAccessToken);
            authTokenDto.setRefreshToken(newRefreshToken.getToken());
            authTokenDto.setRole(userDetails.getAuthorities().iterator().next().getAuthority());

            return authTokenDto;
        }
        throw new RuntimeException("Refresh token not found in database");
    }

    @PostMapping("/place-order")
    public ResponseEntity<?> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {

        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"); // ✅ Let GlobalExceptionHandler handle it
        }

        String authToken = token.substring(7);
        return ResponseEntity.ok(orderService.createOrder(authToken, productId, quantity));
    }

    @DeleteMapping("/cancel-order/{orderId}")
    public ResponseEntity<?> cancelOrder(@RequestHeader("Authorization") String token, @PathVariable UUID orderId) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(orderService.cancelOrder(authToken, orderId), HttpStatus.OK);

    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }

        String authToken = token.substring(7);
        return new ResponseEntity<>(orderService.getMyOrders(authToken), HttpStatus.OK);

    }


}
