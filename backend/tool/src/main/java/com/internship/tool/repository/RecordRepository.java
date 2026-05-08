package com.internship.tool.repository;

import com.internship.tool.entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    // ✅ SEARCH
    @Query("SELECT r FROM Record r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Record> searchByTitle(@Param("keyword") String keyword);

    // ✅ FILTER
    @Query("SELECT r FROM Record r WHERE " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:startDate IS NULL OR r.dueDate >= :startDate) AND " +
            "(:endDate IS NULL OR r.dueDate <= :endDate)")
    List<Record> filterRecords(
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ✅ PERFORMANCE OPTIMIZED QUERY
    @Query("SELECT r FROM Record r")
    Page<Record> findAllOptimized(Pageable pageable);

    // ✅ REQUIRED FOR SCHEDULER
    List<Record> findByDueDateBeforeAndStatusNot(LocalDate date, String status);

    List<Record> findByDueDateBetween(LocalDate start, LocalDate end);

    // ✅ OPTIONAL
    List<Record> findByStatus(String status);
}