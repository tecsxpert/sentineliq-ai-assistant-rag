package com.internship.tool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity   // ✅ Enables @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ❌ Disable CSRF (for testing APIs)
            .csrf(csrf -> csrf.disable())

            // 🔐 Authorization rules
            .authorizeHttpRequests(auth -> auth

                // ✅ Public APIs (NO LOGIN REQUIRED)
                .requestMatchers(
                        "/auth/**",
                        "/h2-console/**",
                        "/test/public"
                ).permitAll()

                // 🔐 ROLE BASED ACCESS
                .requestMatchers("/test/admin").hasRole("ADMIN")
                .requestMatchers("/test/manager").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("/test/viewer").hasAnyRole("VIEWER", "MANAGER", "ADMIN")

                // 🔒 Everything else needs login
                .anyRequest().authenticated()
            )

            // ✅ H2 Console fix (very important)
            .headers(headers -> headers
                    .frameOptions(frame -> frame.disable())
            );

        return http.build();
    }

    // 🔐 Password Encoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}