package com.highway.tolling.service;

import com.highway.tolling.model.*;
import com.highway.tolling.repository.DataAnomalyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Anomaly Detection Service
 * Detects errors and malpractice in IoT GPS data
 */
@Service
public class AnomalyDetectionService {

    private final DataAnomalyRepository anomalyRepository;
    private final LocationTrackingService locationTrackingService;
    private final DistanceCalculatorService distanceCalculatorService;

    // Configurable thresholds
    private static final int MISSING_DATA_THRESHOLD_HOURS = 2;
    private static final int INACTIVITY_THRESHOLD_MINUTES = 30;
    private static final double INACTIVITY_DISTANCE_THRESHOLD_KM = 0.05; // 50 meters
    private static final int DISCONNECTION_THRESHOLD_HOURS = 1;
    private static final int REPEATED_PATTERN_COUNT = 3;
    private static final int REPEATED_PATTERN_DAYS = 30;

    @Autowired
    public AnomalyDetectionService(DataAnomalyRepository anomalyRepository,
            LocationTrackingService locationTrackingService,
            DistanceCalculatorService distanceCalculatorService) {
        this.anomalyRepository = anomalyRepository;
        this.locationTrackingService = locationTrackingService;
        this.distanceCalculatorService = distanceCalculatorService;
    }

    /**
     * Detect missing GPS data for a vehicle
     * Checks if no data received for extended period
     */
    public void detectMissingData(Long vehicleId) {
        List<LocationTracking> recentLocations = locationTrackingService.getLocationsByVehicleId(vehicleId);

        if (recentLocations.isEmpty()) {
            return; // First data point, no anomaly
        }

        LocationTracking lastLocation = recentLocations.get(0);
        LocalDateTime now = LocalDateTime.now();
        Duration timeSinceLastData = Duration.between(lastLocation.getTimestamp(), now);

        if (timeSinceLastData.toHours() > MISSING_DATA_THRESHOLD_HOURS) {
            String description = String.format(
                    "No GPS data received for %.1f hours. Last data at %s",
                    timeSinceLastData.toHours() / 1.0,
                    lastLocation.getTimestamp());

            flagAnomaly(vehicleId, AnomalyType.MISSING_DATA, description,
                    AnomalySeverity.MEDIUM, null);
        }
    }

    /**
     * Detect inactivity on highway
     * Checks if vehicle is stationary on highway for too long
     */
    public void detectInactivity(LocationTracking currentLocation, LocationTracking previousLocation) {
        // Only check if currently on highway
        if (!currentLocation.getIsOnHighway()) {
            return;
        }

        // Calculate distance between current and previous point
        double distance = distanceCalculatorService.calculateDistance(
                previousLocation.getLatitude(),
                previousLocation.getLongitude(),
                currentLocation.getLatitude(),
                currentLocation.getLongitude());

        // Check time difference
        Duration timeDifference = Duration.between(
                previousLocation.getTimestamp(),
                currentLocation.getTimestamp());

        // If vehicle hasn't moved much but time has passed
        if (distance < INACTIVITY_DISTANCE_THRESHOLD_KM &&
                timeDifference.toMinutes() > INACTIVITY_THRESHOLD_MINUTES) {

            String description = String.format(
                    "Vehicle stationary on highway for %d minutes. " +
                            "Distance moved: %.2f km. Highway ID: %d",
                    timeDifference.toMinutes(),
                    distance,
                    currentLocation.getHighwayId());

            flagAnomaly(currentLocation.getVehicleId(),
                    AnomalyType.INACTIVITY_ON_HIGHWAY,
                    description,
                    AnomalySeverity.MEDIUM,
                    currentLocation.getId());
        }
    }

    /**
     * Detect sudden disconnection
     * Checks for abrupt stop in GPS transmission
     */
    public void detectDisconnection(Long vehicleId, LocationTracking currentLocation) {
        List<LocationTracking> recentLocations = locationTrackingService.getLocationsByVehicleId(vehicleId);

        if (recentLocations.size() < 2) {
            return; // Not enough data
        }

        LocationTracking previousLocation = recentLocations.get(1); // Get second-to-last
        Duration gap = Duration.between(previousLocation.getTimestamp(), currentLocation.getTimestamp());

        // Check if there was a significant gap in transmission
        if (gap.toHours() > DISCONNECTION_THRESHOLD_HOURS) {
            String description = String.format(
                    "Sudden GPS disconnection detected. Gap of %.1f hours between " +
                            "%s and %s. Previous location: (%.4f, %.4f)",
                    gap.toHours() / 1.0,
                    previousLocation.getTimestamp(),
                    currentLocation.getTimestamp(),
                    previousLocation.getLatitude(),
                    previousLocation.getLongitude());

            flagAnomaly(vehicleId,
                    AnomalyType.SUDDEN_DISCONNECTION,
                    description,
                    AnomalySeverity.HIGH,
                    currentLocation.getId());
        }
    }

    /**
     * Detect repeated abnormal patterns
     * Checks if same anomaly type occurs multiple times
     */
    public void detectRepeatedPatterns(Long vehicleId, AnomalyType anomalyType) {
        LocalDateTime since = LocalDateTime.now().minusDays(REPEATED_PATTERN_DAYS);

        Long count = anomalyRepository.countAnomaliesByVehicleAndTypeSince(
                vehicleId, anomalyType, since);

        if (count >= REPEATED_PATTERN_COUNT) {
            String description = String.format(
                    "Repeated pattern detected: %s occurred %d times in the last %d days. " +
                            "Possible systematic issue or tampering.",
                    anomalyType.name(),
                    count,
                    REPEATED_PATTERN_DAYS);

            flagAnomaly(vehicleId,
                    AnomalyType.REPEATED_PATTERN,
                    description,
                    AnomalySeverity.HIGH,
                    null);
        }
    }

    /**
     * Run all anomaly checks for a new GPS data point
     */
    public void runAllChecks(LocationTracking currentLocation) {
        Long vehicleId = currentLocation.getVehicleId();
        List<LocationTracking> previousLocations = locationTrackingService.getLocationsByVehicleId(vehicleId);

        // Skip if this is the first location
        if (previousLocations.isEmpty() || previousLocations.size() < 2) {
            return;
        }

        LocationTracking previousLocation = previousLocations.get(1); // Second-to-last (current is already saved)

        // Run detection checks
        detectDisconnection(vehicleId, currentLocation);

        if (currentLocation.getIsOnHighway() && previousLocation.getIsOnHighway()) {
            detectInactivity(currentLocation, previousLocation);
        }

        // Check for repeated patterns of each anomaly type
        for (AnomalyType type : new AnomalyType[] {
                AnomalyType.SUDDEN_DISCONNECTION,
                AnomalyType.INACTIVITY_ON_HIGHWAY,
                AnomalyType.MISSING_DATA }) {
            detectRepeatedPatterns(vehicleId, type);
        }
    }

    /**
     * Flag an anomaly for review
     * Creates a DataAnomaly record without imposing immediate penalty
     */
    public DataAnomaly flagAnomaly(Long vehicleId, AnomalyType anomalyType,
            String description, AnomalySeverity severity, Long relatedLocationId) {

        DataAnomaly anomaly = new DataAnomaly(
                vehicleId,
                anomalyType,
                description,
                severity,
                relatedLocationId);

        return anomalyRepository.save(anomaly);
    }

    /**
     * Get all pending anomalies for review
     */
    public List<DataAnomaly> getPendingAnomalies() {
        return anomalyRepository.findByReviewStatusOrderByDetectedAtDesc(ReviewStatus.PENDING);
    }

    /**
     * Get anomalies for a specific vehicle
     */
    public List<DataAnomaly> getVehicleAnomalies(Long vehicleId) {
        return anomalyRepository.findByVehicleIdOrderByDetectedAtDesc(vehicleId);
    }

    /**
     * Mark anomaly as reviewed
     */
    public DataAnomaly markAsReviewed(Long anomalyId, String notes, ReviewStatus newStatus) {
        DataAnomaly anomaly = anomalyRepository.findById(anomalyId)
                .orElseThrow(() -> new RuntimeException("Anomaly not found with ID: " + anomalyId));

        anomaly.setReviewStatus(newStatus);
        anomaly.setReviewNotes(notes);
        anomaly.setReviewedAt(LocalDateTime.now());

        return anomalyRepository.save(anomaly);
    }
}
