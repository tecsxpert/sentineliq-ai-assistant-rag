package com.internship.tool.service;

import com.internship.tool.entity.Record;
import com.internship.tool.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordService {

    @Autowired
    private RecordRepository repository;

    // ✅ EMAIL SERVICE
    @Autowired
    private EmailService emailService;

    // ✅ CREATE
    public Record save(Record record) {

        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        Record saved = repository.save(record);

        // ✅ SEND EMAIL AFTER CREATE
        emailService.sendEmail(
                "ganabs0819@gmail.com",
                "New Record Created",
                "Record Created Successfully\n\n"
                        + "Title: " + saved.getTitle()
                        + "\nDescription: " + saved.getDescription()
                        + "\nStatus: " + saved.getStatus()
                        + "\nPriority: " + saved.getPriority()
        );

        return saved;
    }

    // ✅ UPDATE
    public Record update(Long id, Record newRecord) {

        Record existing = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Record not found"));

        existing.setTitle(newRecord.getTitle());
        existing.setDescription(newRecord.getDescription());
        existing.setStatus(newRecord.getStatus());
        existing.setPriority(newRecord.getPriority());
        existing.setDueDate(newRecord.getDueDate());
        existing.setUpdatedAt(LocalDateTime.now());

        Record updated = repository.save(existing);

        // ✅ SEND EMAIL AFTER UPDATE
        emailService.sendEmail(
                "ganabs0819@gmail.com",
                "Record Updated",
                "Record Updated Successfully\n\n"
                        + "Title: " + updated.getTitle()
                        + "\nNew Status: " + updated.getStatus()
                        + "\nPriority: " + updated.getPriority()
        );

        return updated;
    }

    // ✅ DELETE (Soft Delete)
    public Record softDelete(Long id) {

        Record record = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Record not found"));

        record.setStatus("DELETED");
        record.setUpdatedAt(LocalDateTime.now());

        Record deleted = repository.save(record);

        // ✅ SEND EMAIL AFTER DELETE
        emailService.sendEmail(
                "ganabs0819@gmail.com",
                "Record Deleted",
                "Record Soft Deleted Successfully\n\n"
                        + "Title: " + deleted.getTitle()
        );

        return deleted;
    }

    // ✅ SEARCH
    public List<Record> search(String keyword) {
        return repository.searchByTitle(keyword);
    }

    // ✅ FILTER
    public List<Record> filter(String status,
                               LocalDate startDate,
                               LocalDate endDate) {

        return repository.filterRecords(
                status,
                startDate,
                endDate
        );
    }

    // ✅ STATS
    public Map<String, Long> getStats() {

        List<Record> all = repository.findAll();

        long total = all.size();

        long open = all.stream()
                .filter(r ->
                        "OPEN".equalsIgnoreCase(r.getStatus()))
                .count();

        long closed = all.stream()
                .filter(r ->
                        "CLOSED".equalsIgnoreCase(r.getStatus()))
                .count();

        long deleted = all.stream()
                .filter(r ->
                        "DELETED".equalsIgnoreCase(r.getStatus()))
                .count();

        Map<String, Long> stats = new HashMap<>();

        stats.put("total", total);
        stats.put("open", open);
        stats.put("closed", closed);
        stats.put("deleted", deleted);

        return stats;
    }

    // ✅ PAGINATION + SORTING + PERFORMANCE
    public Page<Record> getAllRecords(
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        return repository.findAllOptimized(pageable);
    }

    // ✅ CSV EXPORT
    public List<Record> getAllRecordsForExport() {
        return repository.findAll();
    }
}