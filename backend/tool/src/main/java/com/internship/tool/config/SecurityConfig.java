package com.internship.tool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ✅ Disable CSRF
                .csrf(csrf -> csrf.disable())

                // ✅ Authorization
                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC APIs
                        .requestMatchers(
                                "/auth/**",
                                "/h2-console/**",
                                "/test/public",
                                "/records/**"
                        ).permitAll()

                        // ✅ ROLE BASED TESTING
                        .requestMatchers("/test/admin").hasRole("ADMIN")
                        .requestMatchers("/test/manager").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/test/viewer").hasAnyRole("VIEWER", "MANAGER", "ADMIN")

                        // ✅ Allow everything else
                        .anyRequest().permitAll()
                )

                // ✅ H2 Console Fix
                .headers(headers ->
                        headers.frameOptions(frame ->
                                frame.disable()
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}