package com.rv.service;

import com.rv.model.RefreshToken;
import com.rv.model.UserEntity;
import com.rv.repository.RefreshTokenRepository;
import com.rv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken generateRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        refreshToken.setUser(user);
        refreshToken.setUsername(username);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    public void revokeRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshToken = refreshTokenOpt.get();
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        } else {
            throw new IllegalArgumentException("Refresh token not found: " + token);
        }
    }

    public void revokeAllRefreshTokensForUser(String username) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUsername(username);
        for (RefreshToken token : tokens) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        }
    }

    public RefreshToken verifyRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        if (refreshTokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid refresh token: " + token);
        }

        RefreshToken refreshToken = refreshTokenOpt.get();
        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token is revoked: " + token);
        }
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token has expired: " + token);
        }

        return refreshToken;
    }

    public Object refreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken){
        if(refreshToken.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException(refreshToken.getToken()+"Refresh token was expired. Please make a new signin request");
        }
        return refreshToken;
    }
}