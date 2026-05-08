package com.internship.tool.controller;

import com.internship.tool.entity.Record;
import com.internship.tool.repository.RecordRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class RecordIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("postgres")
                    .withPassword("password");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RecordRepository recordRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/records";
    }

    @Test
    void testFullCrudFlow() {

        // CREATE
        Record record = new Record();
        record.setTitle("Integration Test");
        record.setDescription("Testing PostgreSQL");
        record.setStatus("OPEN");
        record.setPriority("HIGH");
        record.setDueDate(LocalDate.now());

        ResponseEntity<Record> createResponse =
                restTemplate.postForEntity(getBaseUrl(), record, Record.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Record created = createResponse.getBody();

        assertNotNull(created);
        assertNotNull(created.getId());

        // READ
        ResponseEntity<Record> getResponse =
                restTemplate.getForEntity(
                        getBaseUrl() + "/" + created.getId(),
                        Record.class
                );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        // UPDATE
        created.setTitle("Updated Integration");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Record> entity = new HttpEntity<>(created, headers);

        ResponseEntity<Record> updateResponse =
                restTemplate.exchange(
                        getBaseUrl() + "/" + created.getId(),
                        HttpMethod.PUT,
                        entity,
                        Record.class
                );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        // DELETE
        restTemplate.delete(getBaseUrl() + "/" + created.getId());

        boolean exists =
                recordRepository.findById(created.getId()).isPresent();

        assertFalse(exists);
    }
}