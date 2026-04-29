package com.internship.tool.service;

import com.internship.tool.entity.Record;
import com.internship.tool.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordService {

    @Autowired
    private RecordRepository repository;

    // ✅ UPDATE
    public Record update(Long id, Record newRecord) {
        Record existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        existing.setTitle(newRecord.getTitle());
        existing.setDescription(newRecord.getDescription());
        existing.setStatus(newRecord.getStatus());
        existing.setPriority(newRecord.getPriority());

        // ✅ IMPORTANT for Day 7
        existing.setDueDate(newRecord.getDueDate());

        existing.setUpdatedAt(java.time.LocalDateTime.now());

        return repository.save(existing);
    }

    // ✅ SOFT DELETE
    public void softDelete(Long id) {
        Record record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        record.setStatus("DELETED");
        record.setUpdatedAt(java.time.LocalDateTime.now());

        repository.save(record);
    }

    // ✅ SEARCH
    public List<Record> search(String keyword) {
        return repository.searchByTitle(keyword);
    }

    // ✅ STATS
    public Map<String, Long> getStats() {
        List<Record> all = repository.findAll();

        long total = all.size();
        long open = all.stream().filter(r -> "OPEN".equalsIgnoreCase(r.getStatus())).count();
        long closed = all.stream().filter(r -> "CLOSED".equalsIgnoreCase(r.getStatus())).count();
        long deleted = all.stream().filter(r -> "DELETED".equalsIgnoreCase(r.getStatus())).count();

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("open", open);
        stats.put("closed", closed);
        stats.put("deleted", deleted);

        return stats;
    }
}