package com.yourorg.ingestion.service;

import com.yourorg.ingestion.dto.ClickHouseConfig;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ClickHouseService {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseService.class);

    public List<Map<String, Object>> fetchData(ClickHouseConfig config) throws SQLException {
        logger.info("Fetching data from ClickHouse with config: {}", config);
        try {
            String url = String.format("jdbc:clickhouse://%s:%d/%s?ssl=true", config.getHost(), config.getPort(), config.getDatabase());
            logger.info("Connecting to ClickHouse with URL: {}", url);
            Properties props = new Properties();
            props.setProperty("user", config.getUser());
            props.setProperty("password", config.getJwtToken());

            // Fetch column names dynamically if not provided
            if (config.getColumns() == null || config.getColumns().isEmpty()) {
                String columnQuery = String.format("DESCRIBE TABLE `%s`", config.getTable());
                logger.info("Fetching column names with query: {}", columnQuery);
                try (Connection conn = DriverManager.getConnection(url, props);
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(columnQuery)) {
                    List<String> columns = new ArrayList<>();
                    while (rs.next()) {
                        columns.add(rs.getString("name"));
                    }
                    config.setColumns(columns);
                }
            }

            // Escape table and column names
            String table = String.format("`%s`", config.getTable());
            String columns = String.join(",", config.getColumns().stream().map(col -> "`" + col + "`").toArray(String[]::new));
            String limit = "LIMIT 20";
            String query = String.format("SELECT %s FROM %s %s", columns, table, limit);
            logger.info("Executing query: {}", query);

            try (Connection conn = DriverManager.getConnection(url, props);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                List<Map<String, Object>> result = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String col : config.getColumns()) {
                        Object value = rs.getObject(col);
                        // Convert LocalDateTime or similar to a proper string format
                        if (value instanceof LocalDateTime) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            row.put(col, ((LocalDateTime) value).format(formatter));
                        } else {
                            row.put(col, value);
                        }
                    }
                    result.add(row);
                }
                logger.info("Fetched {} rows from ClickHouse", result.size());
                System.out.println(result);
                return result;
            }
        } catch (SQLException e) {
            logger.error("Error fetching data from ClickHouse", e);
            throw e;
        }
    }

    public int insertData(List<Map<String, Object>> rows, ClickHouseConfig config) throws SQLException {
        if (rows.isEmpty()) return 0;
        // jdbc:clickhouse://u4g38ntpwh.ap-south-1.aws.clickhouse.cloud:8443?user=default&password=ESJH1Hb~_gr5y&ssl=true
        String url = String.format("jdbc:clickhouse://%s:%d/%s", config.getHost(), config.getPort(), config.getDatabase());
        System.out.println(url);
        url = "jdbc:clickhouse://u4g38ntpwh.ap-south-1.aws.clickhouse.cloud:8443?user=default&password=ESJH1Hb~_gr5y&ssl=true";
        Properties props = new Properties();
        props.setProperty("user", config.getUser());
        props.setProperty("password", config.getJwtToken());

        String insertSQL = "INSERT INTO " + config.getTable() + " (" +
                String.join(",", config.getColumns()) + ") VALUES (" +
                String.join(",", Collections.nCopies(config.getColumns().size(), "?")) + ")";

        try (Connection conn = DriverManager.getConnection(url, props);
             PreparedStatement ps = conn.prepareStatement(insertSQL)) {
            for (Map<String, Object> row : rows) {
                for (int i = 0; i < config.getColumns().size(); i++) {
                    ps.setObject(i + 1, row.get(config.getColumns().get(i)));
                }
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            return Arrays.stream(results).sum();
        }
    }
}