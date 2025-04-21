package com.yourorg.ingestion.dto;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class FlatFileConfig {
    private String filePath;
    private String delimiter;
    private List<String> columns;

    public FlatFileConfig() {}

    public FlatFileConfig(String filePath, String delimiter, List<String> columns) {
        this.filePath = filePath;
        this.delimiter = delimiter;
        this.columns = columns != null ? columns : new ArrayList<>();
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}