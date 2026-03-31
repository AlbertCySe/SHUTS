package com.highway.tolling.service;

import com.highway.tolling.model.Highway;
import com.highway.tolling.model.HighwayUsage;
import com.highway.tolling.model.LocationTracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Highway State Processor
 * Encapsulates the state machine responsible for highway entry, exit, and distance accumulation.
 */
@Service
public class HighwayStateProcessor {

    private final HighwayService highwayService;
    private final DistanceCalculatorService distanceCalculatorService;
    private final HighwayUsageService highwayUsageService;

    private static final double MIN_DISTANCE_THRESHOLD_KM = 0.01;
    private static final double MAX_DISTANCE_THRESHOLD_KM = 5.0;

    @Autowired
    public HighwayStateProcessor(HighwayService highwayService,
            DistanceCalculatorService distanceCalculatorService,
            HighwayUsageService highwayUsageService) {
        this.highwayService = highwayService;
        this.distanceCalculatorService = distanceCalculatorService;
        this.highwayUsageService = highwayUsageService;
    }

    public void processHighwayDetectionAndDistance(
            LocationTracking currentLocation,
            LocationTracking previousLocation,
            Highway currentHighway,
            LocalDateTime timestamp) {

        Long previousHighwayId = previousLocation.getHighwayId();
        Highway previousHighway = null;
        if (previousHighwayId != null) {
            previousHighway = highwayService.getHighwayById(previousHighwayId).orElse(null);
        }

        double distance = distanceCalculatorService.calculateDistance(
                previousLocation.getLatitude(), previousLocation.getLongitude(),
                currentLocation.getLatitude(), currentLocation.getLongitude());

        currentLocation.setDistanceFromPrevious(distance);
        boolean isValidDistance = distance >= MIN_DISTANCE_THRESHOLD_KM && distance <= MAX_DISTANCE_THRESHOLD_KM;

        if (currentHighway != null && previousHighway != null
                && currentHighway.getHighwayId().equals(previousHighway.getHighwayId())) {
            handleSameHighway(currentLocation, currentHighway, distance, isValidDistance);
        } else if (currentHighway != null && previousHighway == null) {
            handleHighwayEntry(currentLocation, currentHighway, timestamp);
        } else if (currentHighway == null && previousHighway != null) {
            handleHighwayExit(currentLocation, previousHighway, timestamp);
        } else if (currentHighway != null && previousHighway != null
                && !currentHighway.getHighwayId().equals(previousHighway.getHighwayId())) {
            handleHighwaySwitch(currentLocation, previousHighway, currentHighway, timestamp);
        } else {
            currentLocation.setIsOnHighway(false);
        }
    }

    private void handleSameHighway(LocationTracking currentLocation, Highway highway,
            double distance, boolean isValidDistance) {
        currentLocation.setIsOnHighway(true);
        currentLocation.setHighwayId(highway.getHighwayId());

        if (isValidDistance) {
            Optional<HighwayUsage> activeSession = highwayUsageService
                    .getActiveSession(currentLocation.getVehicleId());
            activeSession.ifPresent(session -> highwayUsageService.addDistanceToSession(session, distance));
        }
    }

    private void handleHighwayEntry(LocationTracking currentLocation, Highway highway,
            LocalDateTime timestamp) {
        currentLocation.setIsOnHighway(true);
        currentLocation.setHighwayId(highway.getHighwayId());

        highwayUsageService.createHighwaySession(
                currentLocation.getVehicleId(),
                highway.getHighwayId(),
                timestamp,
                currentLocation.getLatitude(),
                currentLocation.getLongitude());
    }

    private void handleHighwayExit(LocationTracking currentLocation, Highway previousHighway,
            LocalDateTime timestamp) {
        currentLocation.setIsOnHighway(false);

        Optional<HighwayUsage> activeSession = highwayUsageService
                .getActiveSession(currentLocation.getVehicleId());
        activeSession.ifPresent(session -> highwayUsageService.closeSession(
                session,
                timestamp,
                currentLocation.getLatitude(),
                currentLocation.getLongitude()));
    }

    private void handleHighwaySwitch(LocationTracking currentLocation, Highway previousHighway,
            Highway currentHighway, LocalDateTime timestamp) {
        Optional<HighwayUsage> activeSession = highwayUsageService
                .getActiveSession(currentLocation.getVehicleId());
        activeSession.ifPresent(session -> highwayUsageService.closeSession(
                session,
                timestamp,
                currentLocation.getLatitude(),
                currentLocation.getLongitude()));

        currentLocation.setIsOnHighway(true);
        currentLocation.setHighwayId(currentHighway.getHighwayId());
        highwayUsageService.createHighwaySession(
                currentLocation.getVehicleId(),
                currentHighway.getHighwayId(),
                timestamp,
                currentLocation.getLatitude(),
                currentLocation.getLongitude());
    }
}
