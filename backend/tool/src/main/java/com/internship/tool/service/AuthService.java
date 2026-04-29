package com.internship.tool.service;

import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ REGISTER USER
    public User register(User user) {

        // 🔐 Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 🔐 Assign default role
        user.setRole("ROLE_VIEWER");

        return repo.save(user);
    }

    // ✅ LOGIN USER
    public User login(String username, String password) {

        System.out.println("======== LOGIN DEBUG ========");
        System.out.println("Entered Username: " + username);
        System.out.println("Entered Password: " + password);

        // 🔍 Find user
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("DB Password: " + user.getPassword());

        // 🔐 Validate password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
}