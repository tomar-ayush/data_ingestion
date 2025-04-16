package com.yourorg.ingestion.controller;

import com.yourorg.ingestion.dto.IngestionRequest;
import com.yourorg.ingestion.model.RecordCountResponse;
import com.yourorg.ingestion.service.IngestionService;
import com.opencsv.exceptions.CsvException;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import com.yourorg.ingestion.dto.ClickHouseConfig;
import com.yourorg.ingestion.dto.FlatFileConfig;
import com.yourorg.ingestion.service.ClickHouseService;
import com.yourorg.ingestion.service.FlatFileService;

@RestController
@RequestMapping("/api/ingest")
@RequiredArgsConstructor
public class IngestionController {

    private static final Logger logger = LoggerFactory.getLogger(IngestionController.class);
    private final IngestionService ingestionService;
    private final FlatFileService flatFileService;
    private final ClickHouseService clickHouseService;

    // Ensure Lombok annotations are processed correctly by verifying IDE and Maven configurations.
    public IngestionController(IngestionService ingestionService, FlatFileService flatFileService, ClickHouseService clickHouseService) {
        this.ingestionService = ingestionService;
        this.flatFileService = flatFileService;
        this.clickHouseService = clickHouseService;
    }

    @PostMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<RecordCountResponse> ingest(@RequestBody IngestionRequest request) throws IOException, SQLException, CsvException {
        logger.info("Received ingestion request: {}", request);
        try {
            RecordCountResponse response = ingestionService.process(request);
            logger.info("Returning response: {}", response);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (IOException | SQLException | CsvException e) {
            logger.error("Error during ingestion", e);
            throw e;
        }
    }

    @GetMapping("/test")
    @ResponseBody
    public RecordCountResponse testEndpoint() {
        return new RecordCountResponse(42);
    }

    @GetMapping("/preview/flatfile")
    @ResponseBody
    public Map<String, Object> previewFlatFile(@RequestParam String filePath, @RequestParam String delimiter) throws IOException, CsvException {
        FlatFileConfig config = new FlatFileConfig(filePath, delimiter, new ArrayList<>());
        List<Map<String, Object>> rows = flatFileService.readFile(config);
        Map<String, Object> response = new HashMap<>();
        if (!rows.isEmpty()) {
            response.put("columns", new ArrayList<>(rows.get(0).keySet()));
        } else {
            response.put("columns", new ArrayList<>());
        }
        response.put("data", rows);
        return response;
    }

    @GetMapping("/preview/clickhouse")
    @ResponseBody
    public Map<String, Object> previewClickHouse(@RequestParam String host, @RequestParam int port, @RequestParam String database, @RequestParam String user, @RequestParam String jwtToken, @RequestParam String table) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Received ClickHouse preview request: host={}, port={}, database={}, table={}", host, port, database, table);
            ClickHouseConfig config = new ClickHouseConfig(host, port, database, user, jwtToken, table, new ArrayList<>());
            List<Map<String, Object>> rows = clickHouseService.fetchData(config);

            if (!rows.isEmpty()) {
                response.put("columns", new ArrayList<>(rows.get(0).keySet()));
            } else {
                response.put("columns", new ArrayList<>());
            }
            response.put("data", rows);
        } catch (Exception e) {
            logger.error("Error during ClickHouse preview", e);
            response.put("error", "Failed to fetch data: " + e.getMessage());
        }
        return response;
    }
}