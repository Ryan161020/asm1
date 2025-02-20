package com.example.ASM1.config;

import com.example.ASM1.Entity.User;
import com.example.ASM1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        User user = optionalUser.get();

        // Đảm bảo vai trò có định dạng "ROLE_ADMIN" hoặc "ROLE_USER"
        String role = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole().toUpperCase();

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword()) // Mật khẩu phải được mã hóa bằng BCrypt
                .authorities(role) // Đảm bảo role đúng định dạng
                .build();
    }
}
