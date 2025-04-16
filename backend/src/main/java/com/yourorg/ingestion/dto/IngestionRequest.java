package com.yourorg.ingestion.dto;

import lombok.Data;

@Data
public class IngestionRequest {
    private String sourceType; // CLICKHOUSE or FLATFILE
    private String targetType; // CLICKHOUSE or FLATFILE
    private ClickHouseConfig clickHouseConfig;
    private FlatFileConfig flatFileConfig;

    public String getSourceType() {
        return sourceType;
    }

    public String getTargetType() {
        return targetType;
    }

    public ClickHouseConfig getClickHouseConfig() {
        return clickHouseConfig;
    }

    public FlatFileConfig getFlatFileConfig() {
        return flatFileConfig;
    }
}