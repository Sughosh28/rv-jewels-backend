package com.rv.service;

import com.rv.dto.AuthTokenDTO;
import com.rv.dto.LoginDTO;
import com.rv.dto.PasswordResetRequestDTO;
import com.rv.jwt.JwtService;
import com.rv.model.RefreshToken;
import com.rv.model.UserEntity;
import com.rv.repository.RefreshTokenRepository;
import com.rv.repository.UserRepository;
import com.rv.userdetails.PlatformUserDetailsService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final PlatformUserDetailsService platformUserDetailsService;
    private final MailService mailService;
    private final JwtService jwtService;
    private final OTPService otpService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, PlatformUserDetailsService platformUserDetailsService, MailService mailService, JwtService jwtService, OTPService otpService, RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.platformUserDetailsService = platformUserDetailsService;
        this.mailService = mailService;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public ResponseEntity<?> registerUser(UserEntity user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        try {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(user.getUsername());
            userEntity.setPassword(encodedPassword);
            userEntity.setAddress(user.getAddress());
            userEntity.setPhoneNumber(user.getPhoneNumber());
            userEntity.setEmail(user.getEmail());
            userEntity.setRole(user.getRole());
            userEntity.setRegistrationDate(LocalDate.now());
            userEntity.setPinCode(user.getPinCode());
            userRepository.save(userEntity);
            mailService.sendRegistrationEmail(user.getEmail(), user.getUsername(), "Thank you for registering with us. Your registration was successful.");
            return new ResponseEntity<>("Registration Successful!", HttpStatusCode.valueOf(201));

        } catch (Exception e) {
            return new ResponseEntity<>("Registration Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AuthTokenDTO loginUser(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.password())
            );

            if (authentication != null && authentication.isAuthenticated()) {
                RefreshToken refreshToken = refreshTokenService.generateRefreshToken(loginDTO.username());
                String token = jwtService.generateToken(platformUserDetailsService.loadUserByUsername(loginDTO.username()));
                String role = platformUserDetailsService.loadUserByUsername(loginDTO.username())
                        .getAuthorities().iterator().next().getAuthority();

                return new AuthTokenDTO(token, refreshToken.getToken(), role);
            } else {
                throw new BadCredentialsException("Invalid credentials!");
            }
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials!");
        }
    }

    public ResponseEntity<?> initiatePasswordReset(String email) throws MessagingException {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("This email is not registered with us!", HttpStatus.NOT_FOUND);
        }

        String otp = generateOTP();
        otpService.storeOTP(user, otp);

        mailService.sendMailForOtp(user.getEmail(), otp);
        return new ResponseEntity<>("OTP sent to registered email", HttpStatus.OK);
    }

    public ResponseEntity<?> resetPassword(PasswordResetRequestDTO passwordResetRequestDTO) throws MessagingException {
        UserEntity user = userRepository.findByEmail(passwordResetRequestDTO.getEmail());
        if (user == null) {
            return new ResponseEntity<>("This email is not registered with us!", HttpStatus.NOT_FOUND);
        }
        if (user.getOtp() == null || user.getOtp().isEmpty() || !otpService.validateOTP(user, passwordResetRequestDTO.getOtp())) {
            return new ResponseEntity<>("OTP is expired, please request a new one", HttpStatus.BAD_REQUEST);
        }

        if (!otpService.validateOTP(user, passwordResetRequestDTO.getOtp())) {
            return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
        }


        String encodedPassword = passwordEncoder.encode(passwordResetRequestDTO.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        otpService.clearOTP(user);
        mailService.sendMailForPasswordReset(user.getEmail(), user.getUsername(), "Your password has been reset successfully.");

        return new ResponseEntity<>("Password updated successfully!", HttpStatus.OK);
    }

    private String generateOTP() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public AuthTokenDTO refreshToken(RefreshToken request) {
        String requestRefreshToken = request.getToken();
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(requestRefreshToken);

        if (tokenOptional.isPresent()) {
            RefreshToken storedToken = tokenOptional.get();
            RefreshToken validRefreshToken = refreshTokenService.verifyExpiration(storedToken);

            UserDetails userDetails = platformUserDetailsService.loadUserByUsername(validRefreshToken.getUser().getUsername());
            String newAccessToken = jwtService.generateToken(userDetails);
            RefreshToken newRefreshToken = refreshTokenService.generateRefreshToken(validRefreshToken.getUser().getUsername());
            return new AuthTokenDTO(newAccessToken, newRefreshToken.getToken(), userDetails.getAuthorities().iterator().next().getAuthority());
        }
        throw new RuntimeException("Refresh token not found in database");

    }
}
