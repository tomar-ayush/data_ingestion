package com.yourorg.ingestion.service;

import com.yourorg.ingestion.dto.IngestionRequest;
import com.yourorg.ingestion.model.RecordCountResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

@Service
public class IngestionService {

    private final ClickHouseService clickHouseService;
    private final FlatFileService flatFileService;

    public IngestionService(ClickHouseService clickHouseService, FlatFileService flatFileService) {
        this.clickHouseService = clickHouseService;
        this.flatFileService = flatFileService;
    }

    public RecordCountResponse process(IngestionRequest req) throws IOException, SQLException, com.opencsv.exceptions.CsvException {
        if (req.getSourceType().equalsIgnoreCase("CLICKHOUSE") && req.getTargetType().equalsIgnoreCase("FLATFILE")) {
            List<Map<String, Object>> rows = clickHouseService.fetchData(req.getClickHouseConfig());
            flatFileService.writeToFile(rows, req.getFlatFileConfig());
            return new RecordCountResponse(rows.size());

        } else if (req.getSourceType().equalsIgnoreCase("FLATFILE") && req.getTargetType().equalsIgnoreCase("CLICKHOUSE")) {
            List<Map<String, Object>> rows = flatFileService.readFile(req.getFlatFileConfig());
            int count = clickHouseService.insertData(rows, req.getClickHouseConfig());
            return new RecordCountResponse(count);
        }
        throw new IllegalArgumentException("Invalid ingestion direction");
    }
}