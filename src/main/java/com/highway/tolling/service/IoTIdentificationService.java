package com.highway.tolling.service;

import com.highway.tolling.dto.IoTDataRequest;
import com.highway.tolling.model.Highway;
import com.highway.tolling.model.HighwayUsage;
import com.highway.tolling.model.LocationTracking;
import com.highway.tolling.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * IoT Identification Service
 * Validates, normalizes, and processes GPS data from IoT devices.
 * Includes highway detection and distance accumulation.
 */
@Service
public class IoTIdentificationService {

    private final VehicleRepository vehicleRepository;
    private final LocationTrackingService locationTrackingService;
    private final HighwayService highwayService;
    private final HighwayDetectionService highwayDetectionService;
    private final DistanceCalculatorService distanceCalculatorService;
    private final HighwayUsageService highwayUsageService;
    private final AnomalyDetectionService anomalyDetectionService;

    // Maximum allowed time difference (in hours) - reject timestamps too far in the
    // past
    private static final int MAX_PAST_HOURS = 24;

    // Minimum distance to accumulate (meters) - avoid GPS noise
    private static final double MIN_DISTANCE_THRESHOLD_KM = 0.01; // 10 meters

    // Maximum distance threshold (km) - detect gaps/errors
    private static final double MAX_DISTANCE_THRESHOLD_KM = 5.0; // 5 km

    @Autowired
    public IoTIdentificationService(VehicleRepository vehicleRepository,
            LocationTrackingService locationTrackingService,
            HighwayService highwayService,
            HighwayDetectionService highwayDetectionService,
            DistanceCalculatorService distanceCalculatorService,
            HighwayUsageService highwayUsageService,
            AnomalyDetectionService anomalyDetectionService) {
        this.vehicleRepository = vehicleRepository;
        this.locationTrackingService = locationTrackingService;
        this.highwayService = highwayService;
        this.highwayDetectionService = highwayDetectionService;
        this.distanceCalculatorService = distanceCalculatorService;
        this.highwayUsageService = highwayUsageService;
        this.anomalyDetectionService = anomalyDetectionService;
    }

    /**
     * Process IoT Data
     * Validates Vehicle, GPS coordinates, timestamp, detects highway usage, and
     * saves location.
     *
     * @param request The data received from the IoT device.
     * @return Saved LocationTracking entity
     * @throws RuntimeException if validation fails
     */
    public LocationTracking processIoTData(IoTDataRequest request) {
        // 1. Check if Vehicle exists
        vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException(
                        "Validation Failed: Vehicle with ID " + request.getVehicleId() + " does not exist."));

        // 2. Parse and validate timestamp
        LocalDateTime timestamp = parseAndValidateTimestamp(request.getTimestamp());

        // 3. Normalize GPS coordinates (round to 6 decimal places for reasonable
        // precision)
        Double normalizedLatitude = normalizeCoordinate(request.getLatitude());
        Double normalizedLongitude = normalizeCoordinate(request.getLongitude());

        // 4. Get previous location for this vehicle
        List<LocationTracking> previousLocations = locationTrackingService
                .getLocationsByVehicleId(request.getVehicleId());

        // 5. Detect highway for current location
        Highway currentHighway = detectHighway(normalizedLatitude, normalizedLongitude);

        // 6. Create location tracking object
        LocationTracking locationTracking = new LocationTracking(
                request.getVehicleId(),
                normalizedLatitude,
                normalizedLongitude,
                timestamp);

        // 7. Process highway detection and distance accumulation
        if (!previousLocations.isEmpty()) {
            LocationTracking previousLocation = previousLocations.get(0);
            processHighwayDetectionAndDistance(
                    locationTracking,
                    previousLocation,
                    currentHighway,
                    timestamp);
        } else {
            // First GPS point - just mark highway status
            if (currentHighway != null) {
                locationTracking.setIsOnHighway(true);
                locationTracking.setHighwayId(currentHighway.getHighwayId());
                // Create initial highway session
                highwayUsageService.createHighwaySession(
                        request.getVehicleId(),
                        currentHighway.getHighwayId(),
                        timestamp,
                        normalizedLatitude,
                        normalizedLongitude);
            }
        }

        // 8. Save location tracking
        LocationTracking savedLocation = locationTrackingService.saveLocation(locationTracking);

        // 9. Run anomaly detection checks (non-blocking, no penalty)
        try {
            anomalyDetectionService.runAllChecks(savedLocation);
        } catch (Exception e) {
            // Log error but don't fail the request - anomaly detection is informational
            System.err.println("Anomaly detection error (non-critical): " + e.getMessage());
        }

        // 10. Return saved location
        return savedLocation;
    }

    /**
     * Process highway detection logic and distance accumulation
     */
    private void processHighwayDetectionAndDistance(
            LocationTracking currentLocation,
            LocationTracking previousLocation,
            Highway currentHighway,
            LocalDateTime timestamp) {

        // Get previous highway (if any)
        Long previousHighwayId = previousLocation.getHighwayId();
        Highway previousHighway = null;
        if (previousHighwayId != null) {
            previousHighway = highwayService.getHighwayById(previousHighwayId).orElse(null);
        }

        // Calculate distance from previous point
        double distance = distanceCalculatorService.calculateDistance(
                previousLocation.getLatitude(),
                previousLocation.getLongitude(),
                currentLocation.getLatitude(),
                currentLocation.getLongitude());

        currentLocation.setDistanceFromPrevious(distance);

        // Apply distance thresholds
        boolean isValidDistance = distance >= MIN_DISTANCE_THRESHOLD_KM && distance <= MAX_DISTANCE_THRESHOLD_KM;

        // Highway State Machine
        if (currentHighway != null && previousHighway != null
                && currentHighway.getHighwayId().equals(previousHighway.getHighwayId())) {
            // CASE: Both on SAME highway - accumulate distance
            handleSameHighway(currentLocation, currentHighway, distance, isValidDistance);

        } else if (currentHighway != null && previousHighway == null) {
            // CASE: Vehicle ENTERED highway
            handleHighwayEntry(currentLocation, currentHighway, timestamp);

        } else if (currentHighway == null && previousHighway != null) {
            // CASE: Vehicle EXITED highway
            handleHighwayExit(currentLocation, previousHighway, timestamp);

        } else if (currentHighway != null && previousHighway != null
                && !currentHighway.getHighwayId().equals(previousHighway.getHighwayId())) {
            // CASE: Switched highways
            handleHighwaySwitch(currentLocation, previousHighway, currentHighway, timestamp);

        } else {
            // CASE: Both OFF highway - do nothing
            currentLocation.setIsOnHighway(false);
        }
    }

    /**
     * Handle case: Vehicle traveling on same highway
     */
    private void handleSameHighway(LocationTracking currentLocation, Highway highway,
            double distance, boolean isValidDistance) {
        currentLocation.setIsOnHighway(true);
        currentLocation.setHighwayId(highway.getHighwayId());

        // Add distance to active session if valid
        if (isValidDistance) {
            Optional<HighwayUsage> activeSession = highwayUsageService
                    .getActiveSession(currentLocation.getVehicleId());
            activeSession.ifPresent(session -> highwayUsageService.addDistanceToSession(session, distance));
        }
    }

    /**
     * Handle case: Vehicle entered highway
     */
    private void handleHighwayEntry(LocationTracking currentLocation, Highway highway,
            LocalDateTime timestamp) {
        currentLocation.setIsOnHighway(true);
        currentLocation.setHighwayId(highway.getHighwayId());

        // Create new highway session
        highwayUsageService.createHighwaySession(
                currentLocation.getVehicleId(),
                highway.getHighwayId(),
                timestamp,
                currentLocation.getLatitude(),
                currentLocation.getLongitude());
    }

    /**
     * Handle case: Vehicle exited highway
     */
    private void handleHighwayExit(LocationTracking currentLocation, Highway previousHighway,
            LocalDateTime timestamp) {
        currentLocation.setIsOnHighway(false);

        // Close active highway session
        Optional<HighwayUsage> activeSession = highwayUsageService
                .getActiveSession(currentLocation.getVehicleId());
        activeSession.ifPresent(session -> highwayUsageService.closeSession(
                session,
                timestamp,
                currentLocation.getLatitude(),
                currentLocation.getLongitude()));
    }

    /**
     * Handle case: Vehicle switched from one highway to another
     */
    private void handleHighwaySwitch(LocationTracking currentLocation, Highway previousHighway,
            Highway currentHighway, LocalDateTime timestamp) {
        // Close session for previous highway
        Optional<HighwayUsage> activeSession = highwayUsageService
                .getActiveSession(currentLocation.getVehicleId());
        activeSession.ifPresent(session -> highwayUsageService.closeSession(
                session,
                timestamp,
                currentLocation.getLatitude(),
                currentLocation.getLongitude()));

        // Create session for new highway
        currentLocation.setIsOnHighway(true);
        currentLocation.setHighwayId(currentHighway.getHighwayId());
        highwayUsageService.createHighwaySession(
                currentLocation.getVehicleId(),
                currentHighway.getHighwayId(),
                timestamp,
                currentLocation.getLatitude(),
                currentLocation.getLongitude());
    }

    /**
     * Detect which highway the vehicle is on (if any)
     */
    private Highway detectHighway(double latitude, double longitude) {
        List<Highway> allHighways = highwayService.getAllHighways();

        for (Highway highway : allHighways) {
            if (highwayDetectionService.isWithinHighwayRange(latitude, longitude, highway)) {
                return highway;
            }
        }

        return null; // Not on any highway
    }

    /**
     * Parse and validate timestamp from ISO-8601 string format
     *
     * @param timestampStr ISO-8601 formatted timestamp string
     * @return Parsed LocalDateTime
     * @throws RuntimeException if parsing fails or timestamp is invalid
     */
    private LocalDateTime parseAndValidateTimestamp(String timestampStr) {
        LocalDateTime timestamp;
        try {
            // Support both with and without seconds
            try {
                timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                // Try parsing as ISO date time without T separator
                timestamp = LocalDateTime.parse(timestampStr.replace(" ", "T"));
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException(
                    "Validation Failed: Invalid timestamp format. Expected ISO-8601 format (e.g., '2026-02-04T13:18:00')");
        }

        LocalDateTime now = LocalDateTime.now();

        // Reject future timestamps
        if (timestamp.isAfter(now)) {
            throw new RuntimeException(
                    "Validation Failed: Timestamp cannot be in the future. Received: " + timestamp + ", Current: "
                            + now);
        }

        // Reject timestamps too far in the past
        LocalDateTime oldestAllowed = now.minusHours(MAX_PAST_HOURS);
        if (timestamp.isBefore(oldestAllowed)) {
            throw new RuntimeException(
                    "Validation Failed: Timestamp is too old. Must be within the last " + MAX_PAST_HOURS
                            + " hours. Received: " + timestamp);
        }

        return timestamp;
    }

    /**
     * Normalize GPS coordinate to 6 decimal places
     * (6 decimal places = ~0.11 meter precision)
     *
     * @param coordinate Latitude or Longitude
     * @return Normalized coordinate
     */
    private Double normalizeCoordinate(Double coordinate) {
        return BigDecimal.valueOf(coordinate)
                .setScale(6, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
