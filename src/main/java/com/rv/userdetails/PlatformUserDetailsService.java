package com.rv.userdetails;

import com.rv.model.UserEntity;
import com.rv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class PlatformUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public PlatformUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        String role = userEntity.getRole();
        if (role == null || role.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return new PlatformUserDetails(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getEmail(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
