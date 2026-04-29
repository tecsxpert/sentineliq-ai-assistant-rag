package com.internship.tool.repository;

import com.internship.tool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;   // ✅ ADD THIS

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}