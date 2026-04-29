package com.internship.tool.repository;

import com.internship.tool.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByStatus(String status);

    // ✅ SEARCH METHOD (FIXED)
    @Query("SELECT r FROM Record r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Record> searchByTitle(@Param("keyword") String keyword);

    // ✅ DAY 7 METHODS
    List<Record> findByDueDateBeforeAndStatusNot(LocalDate date, String status);

    List<Record> findByDueDateBetween(LocalDate start, LocalDate end);
}