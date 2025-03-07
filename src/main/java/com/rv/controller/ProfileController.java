package com.rv.controller;

import com.rv.dto.*;
import com.rv.service.ProductService;
import com.rv.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v2/user")
public class ProfileController {

    private final ProfileService profileService;
    private final ProductService productService;

    public ProfileController(ProfileService profileService, ProductService productService) {
        this.profileService = profileService;
        this.productService = productService;

    }

    @GetMapping("/profile")
    public UserProfileResponseDTO getUserProfile(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return profileService.getUserProfile(authToken);
    }


    @PatchMapping("/update-address")
    public ResponseEntity<?> updateAddress(@RequestHeader("Authorization") String token,
                                           @RequestBody AddressUpdateDTO addressUpdateDTO) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(profileService.updateAddress(authToken, addressUpdateDTO), HttpStatus.OK);
    }

    @PutMapping("/username-update")
    public ResponseEntity<?> updateProfileName(@RequestHeader("Authorization") String token, @RequestBody UserProfileDTO userProfileDTO) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(profileService.updateProfileName(authToken, userProfileDTO), HttpStatus.OK);
    }

    @PutMapping("/email-update")
    public ResponseEntity<?> updateEmailAddress(@RequestHeader("Authorization") String token, @RequestBody EmailUpdateRequestDTO emailUpdateRequestDTO) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(profileService.updateEmailAddress(authToken, emailUpdateRequestDTO), HttpStatus.OK);
    }

    @PatchMapping("/update-phone")
    public ResponseEntity<?> updatePhoneNumber(@RequestHeader("Authorization") String token, @RequestBody PhoneNumberUpdateRequestDTO phoneUpdateDTO) {

        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(profileService.updatePhoneNumber(authToken, phoneUpdateDTO), HttpStatus.OK);
    }

    @GetMapping("/all-products")
    public ResponseEntity<?> getAllProducts(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @PostMapping("/profile-picture")
    public ResponseEntity<?> updateProfilePicture(@RequestHeader("Authorization") String token, @RequestPart("image") MultipartFile image) throws IOException {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Authentication is missing", HttpStatus.BAD_REQUEST);
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(profileService.uploadProfilePicture(authToken, image), HttpStatus.OK);
    }

}
