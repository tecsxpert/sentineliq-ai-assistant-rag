package com.internship.tool.controller;

import com.internship.tool.entity.Record;
import com.internship.tool.repository.RecordRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

// ✅ IMPORTANT STATIC IMPORTS
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecordRepository recordRepository;

    @Test
    void testCreateRecord() throws Exception {
        String json = """
                {
                  "title": "Test Record",
                  "description": "Testing create",
                  "status": "OPEN",
                  "priority": "HIGH",
                  "dueDate": "2026-05-20"
                }
                """;

        mockMvc.perform(post("/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Record"));
    }

    @Test
    void testGetAllRecords() throws Exception {
        mockMvc.perform(get("/records"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateRecord() throws Exception {

        Record record = new Record();
        record.setTitle("Old Title");
        record.setDescription("Old Desc");
        record.setStatus("OPEN");
        record.setPriority("HIGH");
        record.setDueDate(LocalDate.now());

        Record saved = recordRepository.save(record);

        String updatedJson = """
                {
                  "title": "Updated Title",
                  "description": "Updated Desc",
                  "status": "CLOSED",
                  "priority": "LOW",
                  "dueDate": "2026-05-25"
                }
                """;

        mockMvc.perform(put("/records/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void testDeleteRecord() throws Exception {

        Record record = new Record();
        record.setTitle("Delete Test");
        record.setDescription("To be deleted");
        record.setStatus("OPEN");
        record.setPriority("HIGH");
        record.setDueDate(LocalDate.now());

        Record saved = recordRepository.save(record);

        mockMvc.perform(delete("/records/" + saved.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testExportCSV() throws Exception {

        Record record = new Record();
        record.setTitle("CSV Test");
        record.setDescription("Export working");
        record.setStatus("OPEN");
        record.setPriority("HIGH");
        record.setDueDate(LocalDate.now());

        recordRepository.save(record);

        mockMvc.perform(get("/records/export"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ID,Title")));
    }
}