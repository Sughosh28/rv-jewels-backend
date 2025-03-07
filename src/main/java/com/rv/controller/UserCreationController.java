package com.rv.controller;

import com.rv.dto.AuthTokenDTO;
import com.rv.dto.LoginDTO;
import com.rv.dto.PasswordResetRequestDTO;
import com.rv.jwt.JwtService;
import com.rv.model.RefreshToken;
import com.rv.model.UserEntity;
import com.rv.repository.RefreshTokenRepository;
import com.rv.service.OrderService;
import com.rv.service.ProductService;
import com.rv.service.RefreshTokenService;
import com.rv.service.UserService;
import com.rv.userdetails.PlatformUserDetailsService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserCreationController {

    private final UserService userService;
    public UserCreationController(UserService userService) {
        this.userService = userService;
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
        return userService.refreshToken(request);
    }
}
