package com.yourorg.ingestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourorg.ingestion.model.RecordCountResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IngestionControllerTest {

    @Test
    public void testRecordCountResponseSerialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RecordCountResponse response = new RecordCountResponse(42);
        String json = objectMapper.writeValueAsString(response);
        assertNotNull(json);
        assertTrue(json.contains("42"));
    }
}