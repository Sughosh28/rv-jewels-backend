package com.rv.controller;

import com.rv.dto.AuthTokenDTO;
import com.rv.dto.LoginDTO;
import com.rv.dto.PasswordResetRequestDTO;
import com.rv.jwt.JwtService;
import com.rv.model.RefreshToken;
import com.rv.model.UserEntity;
import com.rv.repository.RefreshTokenRepository;
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

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PlatformUserDetailsService platformUserDetailsService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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
}
