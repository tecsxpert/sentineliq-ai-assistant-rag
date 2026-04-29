package com.internship.tool.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import com.internship.tool.service.AuthService;
import com.internship.tool.entity.User;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return ResponseEntity.ok(service.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return ResponseEntity.ok(
                service.login(user.getUsername(), user.getPassword())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        return ResponseEntity.ok("Token refreshed (dummy)");
    }
}