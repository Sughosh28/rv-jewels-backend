package com.rv.service;

import com.rv.dto.LoginDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PlatformUserDetailsService platformUserDetailsService;
    @Autowired
    private JwtService jwtService;

    public ResponseEntity<?> registerUser(UserEntity user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        try {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println(encodedPassword);
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
                String role= platformUserDetailsService.loadUserByUsername(loginDTO.username()).getAuthorities().iterator().next().getAuthority();
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

}
