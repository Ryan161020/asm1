package com.example.ASM1.config;

import com.example.ASM1.config.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ❌ Có thể bật lại sau khi kiểm tra
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/test-password","/auth/**", "/login", "/logout", "/home", "/product/**", "/cart/**").permitAll() // ✅ Cho phép truy cập trang không cần đăng nhập
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 🔒 Chỉ ADMIN mới vào được Admin
                        .anyRequest().permitAll() // ✅ Các trang khác cần đăng nhập
                )
                .formLogin(form -> form
                        .loginPage("/auth/login") // ✅ Trang đăng nhập tùy chỉnh
                        .loginProcessingUrl("/login") // ✅ URL xử lý đăng nhập, form cần submit đúng đường dẫn này
                        .usernameParameter("email") // ✅ Dùng email thay vì username
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler()) // ✅ Chuyển hướng sau đăng nhập
                        .failureUrl("/auth/login?error=true") // ✅ Nếu sai mật khẩu, quay lại login với lỗi
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            authentication.getAuthorities().forEach(authority -> {
                try {
                    if (authority.getAuthority().equals("ROLE_ADMIN")) {
                        response.sendRedirect("/product/admin"); // ✅ Nếu là Admin → Trang Admin
                    } else {
                        response.sendRedirect("/product/findAll"); // ✅ Nếu là User → Trang chính
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration

                .getAuthenticationManager();
    }
}
