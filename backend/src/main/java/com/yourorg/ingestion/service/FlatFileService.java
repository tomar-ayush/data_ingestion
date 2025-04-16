package com.yourorg.ingestion.service;

import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import com.yourorg.ingestion.dto.FlatFileConfig;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class FlatFileService {

    public List<Map<String, Object>> readFile(FlatFileConfig config) throws IOException, CsvException {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(config.getFilePath())).build()) {
            List<String[]> lines = reader.readAll();
            List<Map<String, Object>> result = new ArrayList<>();

            if (lines.isEmpty()) {
                return result; // Return empty result if file is empty
            }

            // Extract column names from the first row if columns are not provided
            List<String> columns = config.getColumns();
            if (columns.isEmpty()) {
                columns = Arrays.asList(lines.get(0));
                config.setColumns(columns);
            }

            // Process the remaining rows
            for (int i = 1; i < lines.size(); i++) {
                String[] line = lines.get(i);
                Map<String, Object> row = new LinkedHashMap<>();
                for (int j = 0; j < columns.size(); j++) {
                    row.put(columns.get(j), j < line.length ? line[j] : null);
                }
                result.add(row);
            }

            return result;
        }
    }

    public void writeToFile(List<Map<String, Object>> rows, FlatFileConfig config) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(config.getFilePath()))) {
            String[] header = config.getColumns().toArray(new String[0]);
            writer.writeNext(header);

            for (Map<String, Object> row : rows) {
                String[] line = config.getColumns().stream()
                        .map(col -> Optional.ofNullable(row.get(col)).map(Object::toString).orElse(""))
                        .toArray(String[]::new);
                writer.writeNext(line);
            }
        }
    }
}