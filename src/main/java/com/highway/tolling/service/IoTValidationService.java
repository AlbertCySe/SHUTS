package com.highway.tolling.service;

import com.highway.tolling.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * IoT Validation Service
 * Extracts and handles data validation logic to keep the main orchestrator clean.
 */
@Service
public class IoTValidationService {

    private final VehicleRepository vehicleRepository;
    private static final int MAX_PAST_HOURS = 24;

    @Autowired
    public IoTValidationService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void validateVehicleExists(Long vehicleId) {
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException(
                        "Validation Failed: Vehicle with ID " + vehicleId + " does not exist."));
    }

    public LocalDateTime parseAndValidateTimestamp(String timestampStr) {
        LocalDateTime timestamp;
        try {
            try {
                timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                timestamp = LocalDateTime.parse(timestampStr.replace(" ", "T"));
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Validation Failed: Invalid timestamp format.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (timestamp.isAfter(now)) {
            throw new RuntimeException("Validation Failed: Timestamp cannot be in the future.");
        }
        LocalDateTime oldestAllowed = now.minusHours(MAX_PAST_HOURS);
        if (timestamp.isBefore(oldestAllowed)) {
            throw new RuntimeException("Validation Failed: Timestamp is too old.");
        }
        return timestamp;
    }

    public Double normalizeCoordinate(Double coordinate) {
        return BigDecimal.valueOf(coordinate)
                .setScale(6, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
