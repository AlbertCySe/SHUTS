package com.highway.iot.controller;

import com.highway.iot.model.SimulatorSettings;
import com.highway.iot.service.SimulatorSettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/iot/settings")
@CrossOrigin(origins = "*")
public class SimulatorSettingsController {

    private final SimulatorSettingsService settingsService;

    public SimulatorSettingsController(SimulatorSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public SimulatorSettings getSettings() {
        return settingsService.getSettings();
    }

    @PutMapping
    public SimulatorSettings updateSettings(@RequestBody SimulatorSettings settings) {
        return settingsService.updateSettings(settings);
    }

    @PostMapping("/reset")
    public SimulatorSettings resetSettings() {
        return settingsService.resetSettings();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSettings(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "success", false,
                        "message", ex.getMessage()));
    }
}
