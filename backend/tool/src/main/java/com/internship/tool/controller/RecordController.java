package com.internship.tool.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/records")
public class RecordController {

    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String create() {
        return "Created";
    }

    // ADMIN + MANAGER
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public String update(@PathVariable Long id) {
        return "Updated " + id;
    }

    // ALL roles
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @GetMapping("/all")
    public String getAll() {
        return "All records";
    }
}