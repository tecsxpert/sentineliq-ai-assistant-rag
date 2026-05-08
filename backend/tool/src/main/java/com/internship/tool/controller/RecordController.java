package com.internship.tool.controller;

import com.internship.tool.entity.Record;
import com.internship.tool.service.RecordService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/records")
public class RecordController {

    @Autowired
    private RecordService service;

    // ✅ CREATE
    @PostMapping
    public Record create(@RequestBody Record record) {
        return service.save(record);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public Record update(@PathVariable Long id, @RequestBody Record record) {
        return service.update(id, record);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public Record delete(@PathVariable Long id) {
        return service.softDelete(id);
    }

    // ✅ SEARCH
    @GetMapping("/search")
    public List<Record> search(@RequestParam String keyword) {
        return service.search(keyword);
    }

    // ✅ FILTER (Day 9 Dev 3)
    @GetMapping("/filter")
    public List<Record> filter(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return service.filter(
                status,
                startDate != null ? LocalDate.parse(startDate) : null,
                endDate != null ? LocalDate.parse(endDate) : null
        );
    }

    // ✅ STATS
    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return service.getStats();
    }

    // ✅ PAGINATION
    @GetMapping
    public Page<Record> getAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return service.getAllRecords(page, size, sortBy, sortDir);
    }

    // ✅ CSV EXPORT
    @GetMapping("/export")
    public void exportToCSV(HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=records.csv");

        List<Record> records = service.getAllRecordsForExport();
        PrintWriter writer = response.getWriter();

        writer.println("ID,Title,Description,Status,Priority,DueDate");

        for (Record record : records) {
            writer.println(
                    record.getId() + ",\"" +
                    clean(record.getTitle()) + "\",\"" +
                    clean(record.getDescription()) + "\"," +
                    record.getStatus() + "," +
                    record.getPriority() + "," +
                    record.getDueDate()
            );
        }

        writer.flush();
        writer.close();
    }

    // ✅ CSV clean helper
    private String clean(String value) {
        if (value == null) return "";
        return value.replace("\n", " ").replace("\r", " ").replace("\"", "\"\"");
    }
}