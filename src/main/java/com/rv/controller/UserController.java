package com.rv.controller;

import com.rv.dto.LoginDTO;
import com.rv.dto.PasswordResetRequestDTO;
import com.rv.model.UserEntity;
import com.rv.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserEntity user){
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
}
