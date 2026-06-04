package com.highway.tolling.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/db-explorer")
@CrossOrigin(origins = "*")
public class DbExplorerController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/tables")
    public List<Map<String, String>> getTables() {
        List<String> tables = jdbcTemplate.queryForList("SHOW TABLES", String.class);
        return tables.stream().map(tableName -> {
            String label = tableName.substring(0, 1).toUpperCase() + tableName.substring(1).replace("_", " ");
            return Map.of(
                "key", tableName,
                "label", "📊 " + label,
                "endpoint", "/db-explorer/table/" + tableName
            );
        }).collect(Collectors.toList());
    }

    @GetMapping("/table/{tableName}")
    public List<Map<String, Object>> getTableData(@PathVariable String tableName) {
        // Validate table name to prevent SQL injection
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid table name");
        }
        return jdbcTemplate.queryForList("SELECT * FROM " + tableName + " LIMIT 100");
    }
}
