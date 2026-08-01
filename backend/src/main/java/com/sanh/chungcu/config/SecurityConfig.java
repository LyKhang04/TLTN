package com.sanh.chungcu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Chỉ khai báo PasswordEncoder (BCrypt) để băm/so khớp mật khẩu.
 * Dự án KHÔNG dùng spring-boot-starter-security đầy đủ để tránh
 * việc mọi endpoint /api/** bị khoá lại bởi filter chain mặc định.
 * Khi triển khai thật, nên thêm Spring Security + JWT filter riêng.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
