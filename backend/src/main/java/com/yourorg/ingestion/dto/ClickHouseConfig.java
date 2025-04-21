package com.yourorg.ingestion.dto;

import lombok.Data;
import java.util.List;

@Data
public class ClickHouseConfig {
    private String host;
    private int port;
    private String database;
    private String user;
    private String jwtToken;
    private String table;
    private List<String> columns;

    public ClickHouseConfig() {
        // Default constructor for Jackson deserialization
    }

    public ClickHouseConfig(String host, int port, String database, String user, String jwtToken, String table, List<String> columns) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.jwtToken = jwtToken;
        this.table = table;
        this.columns = columns;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUser() {
        return user;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public String getTable() {
        return table;
    }

    public List<String> getColumns() {
        return columns;
    }
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}