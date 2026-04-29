package com.internship.tool.repository;

import com.internship.tool.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    // 🔍 Search
    @Query("SELECT r FROM Record r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Record> searchByTitle(@Param("keyword") String keyword);

    // 📊 Filter
    List<Record> findByStatus(String status);

    // 📅 Date range
    @Query("SELECT r FROM Record r WHERE r.createdAt BETWEEN :start AND :end")
    List<Record> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}