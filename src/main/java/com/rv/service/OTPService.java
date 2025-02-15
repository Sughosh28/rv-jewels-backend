package com.rv.service;

import com.rv.model.UserEntity;
import com.rv.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OTPService {
    private final UserRepository userRepository;

    public OTPService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void storeOTP(UserEntity user, String otp) {
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
    }

    public boolean validateOTP(UserEntity user, String otp) {
        return user.getOtp().equals(otp) &&
                LocalDateTime.now().isBefore(user.getOtpExpiry());
    }

    @Transactional
    public void clearOTP(UserEntity user) {
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }
}
