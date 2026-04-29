package com.internship.tool.controller;

import com.internship.tool.entity.Record;
import com.internship.tool.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    @Autowired
    private RecordService service;

    // 1. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Record record) {
        Record updated = service.update(id, record);
        return ResponseEntity.ok(updated);
    }

    // 2. DELETE (SOFT DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    // 3. SEARCH
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q) {
        return ResponseEntity.ok(service.search(q));
    }

    // 4. STATS
    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        return ResponseEntity.ok(service.getStats());
    }
}