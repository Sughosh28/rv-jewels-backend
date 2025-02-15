package com.rv.service;

import com.rv.dto.*;
import com.rv.jwt.JwtService;
import com.rv.model.UserEntity;
import com.rv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ProfileService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Cacheable(value = "userProfiles", key = "#token")
    public UserProfileResponseDTO getUserProfile(String token) {
        Long userId = jwtService.extractUserId(token);
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User does not exist!");
        }
        System.out.println("Hit DB");
        return mapToDTO(user.get());
    }

    private UserProfileResponseDTO mapToDTO(UserEntity user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setPinCode(user.getPinCode());
        dto.setRegistrationDate(user.getRegistrationDate());
        return dto;
    }


    @CacheEvict(value = "userProfiles", key = "#token")
    public ResponseEntity<?> updateProfileName(String token, UserProfileDTO userProfileDTO) {

        try {
            Long userId = jwtService.extractUserId(token);
            Optional<UserEntity> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return new ResponseEntity<>("User does not exist!", HttpStatus.UNAUTHORIZED);
            }
            UserEntity userEntity = user.get();
            userEntity.setUsername(userProfileDTO.getUsername());
            userRepository.save(userEntity);
            return new ResponseEntity<>("Username updated successfully!", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @CacheEvict(value = "userProfiles", key = "#token")
    public ResponseEntity<?> updateEmailAddress(String token, EmailUpdateRequestDTO emailUpdateRequestDTO) {
        try {
            Long userId = jwtService.extractUserId(token);
            Optional<UserEntity> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return new ResponseEntity<>("User does not exist!", HttpStatus.UNAUTHORIZED);
            }
            UserEntity userEntity = user.get();
            userEntity.setEmail(emailUpdateRequestDTO.getEmail());
            userRepository.save(userEntity);
            return new ResponseEntity<>("Email updated successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @CacheEvict(value = "userProfiles", key = "#token")
    public ResponseEntity<?> updateAddress(String token, AddressUpdateDTO addressUpdateDTO) {
        try {
            Long userId = jwtService.extractUserId(token);
            Optional<UserEntity> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return new ResponseEntity<>("User does not exist!", HttpStatus.UNAUTHORIZED);
            }
            UserEntity userEntity = user.get();
            userEntity.setAddress(addressUpdateDTO.getAddress());
            userEntity.setPinCode(addressUpdateDTO.getPinCode());
            userRepository.save(userEntity);
            return new ResponseEntity<>("Address updated successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

    @CacheEvict(value = "userProfiles", key = "#token")
    public ResponseEntity<?> updatePhoneNumber(String token, PhoneNumberUpdateRequestDTO phoneUpdateDTO) {
        try {
            Long userId = jwtService.extractUserId(token);
            Optional<UserEntity> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return new ResponseEntity<>("User does not exist!", HttpStatus.UNAUTHORIZED);
            }
            UserEntity userEntity = user.get();
            userEntity.setPhoneNumber(phoneUpdateDTO.getPhoneNumber());
            userRepository.save(userEntity);
            return new ResponseEntity<>("Phone number updated successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }
}
