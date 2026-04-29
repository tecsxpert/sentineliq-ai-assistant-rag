package com.internship.tool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.internship.tool.entity.User;
import com.internship.tool.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    // REGISTER
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return service.register(user);
    }

    // LOGIN
    @PostMapping("/login")
    public User login(@RequestBody User user) {
        return service.login(user.getUsername(), user.getPassword());
    }
}