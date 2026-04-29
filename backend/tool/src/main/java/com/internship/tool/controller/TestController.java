package com.internship.tool.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/test")
public class TestController {

    // ✅ Public API
    @GetMapping("/public")
    public String publicApi() {
        return "Public API working";
    }

    // 🔐 ADMIN ONLY
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "Admin access granted";
    }

    // 🔐 MANAGER + ADMIN
    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public String manager() {
        return "Manager access granted";
    }

    // 🔐 ALL USERS
    @GetMapping("/viewer")
    @PreAuthorize("hasAnyRole('VIEWER','MANAGER','ADMIN')")
    public String viewer() {
        return "Viewer access granted";
    }
}