package com.rv.service;

import com.rv.dto.LoginDTO;
import com.rv.dto.PasswordResetRequestDTO;
import com.rv.jwt.JwtService;
import com.rv.model.UserEntity;
import com.rv.repository.UserRepository;
import com.rv.userdetails.PlatformUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final PlatformUserDetailsService platformUserDetailsService;

    private final JwtService jwtService;
    private final OTPService otpService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, PlatformUserDetailsService platformUserDetailsService, JwtService jwtService, OTPService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.platformUserDetailsService = platformUserDetailsService;
        this.jwtService = jwtService;
        this.otpService = otpService;
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
            return new ResponseEntity<>("Registration Successful!", HttpStatusCode.valueOf(201));

        } catch (Exception e) {
            return new ResponseEntity<>("Registration Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> loginUser(LoginDTO loginDTO) {
        try {

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.password()));

            if (authentication != null && authentication.isAuthenticated()) {
                String token = jwtService.generateToken(platformUserDetailsService.loadUserByUsername(loginDTO.username()));
                String role = platformUserDetailsService.loadUserByUsername(loginDTO.username()).getAuthorities().iterator().next().getAuthority();
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("role", role);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found or authentication failed.", HttpStatus.NOT_FOUND);
            }
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Invalid credentials!", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> initiatePasswordReset(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("This email is not registered with us!", HttpStatus.NOT_FOUND);
        }

        String otp = generateOTP();
        otpService.storeOTP(user, otp);

        System.out.println(otp);
        return new ResponseEntity<>("OTP sent to registered email", HttpStatus.OK);
    }

    public ResponseEntity<?> resetPassword(PasswordResetRequestDTO passwordResetRequestDTO) {
        UserEntity user = userRepository.findByEmail(passwordResetRequestDTO.getEmail());
        if (user == null) {
            return new ResponseEntity<>("This email is not registered with us!", HttpStatus.NOT_FOUND);
        }

        if (!otpService.validateOTP(user, passwordResetRequestDTO.getOtp())) {
            return new ResponseEntity<>("Invalid or expired OTP", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(passwordResetRequestDTO.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        otpService.clearOTP(user);

        return new ResponseEntity<>("Password updated successfully!", HttpStatus.OK);
    }

    private String generateOTP() {
        return String.format("%06d", new Random().nextInt(999999));
    }

}
