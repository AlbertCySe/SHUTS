package com.highway.tolling.service;

import com.highway.tolling.dto.IoTDataRequest;
import com.highway.tolling.model.Highway;
import com.highway.tolling.model.LocationTracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IoT Identification Service
 * Handles the flow of IoT Request by delegating validation and state processing to helpers.
 */
@Service
public class IoTIdentificationService {

    private final LocationTrackingService locationTrackingService;
    private final HighwayService highwayService;
    private final HighwayDetectionService highwayDetectionService;
    private final HighwayUsageService highwayUsageService;
    private final AnomalyDetectionService anomalyDetectionService;
    private final IoTValidationService validationService;
    private final HighwayStateProcessor stateProcessor;

    @Autowired
    public IoTIdentificationService(LocationTrackingService locationTrackingService,
            HighwayService highwayService,
            HighwayDetectionService highwayDetectionService,
            HighwayUsageService highwayUsageService,
            AnomalyDetectionService anomalyDetectionService,
            IoTValidationService validationService,
            HighwayStateProcessor stateProcessor) {
        this.locationTrackingService = locationTrackingService;
        this.highwayService = highwayService;
        this.highwayDetectionService = highwayDetectionService;
        this.highwayUsageService = highwayUsageService;
        this.anomalyDetectionService = anomalyDetectionService;
        this.validationService = validationService;
        this.stateProcessor = stateProcessor;
    }

    public LocationTracking processIoTData(IoTDataRequest request) {
        validationService.validateVehicleExists(request.getVehicleId());

        LocalDateTime timestamp = validationService.parseAndValidateTimestamp(request.getTimestamp());
        Double normalizedLatitude = validationService.normalizeCoordinate(request.getLatitude());
        Double normalizedLongitude = validationService.normalizeCoordinate(request.getLongitude());

        List<LocationTracking> previousLocations = locationTrackingService
                .getLocationsByVehicleId(request.getVehicleId());

        Highway currentHighway = null;
        Boolean isHighwayFlag = request.getIsHighway();
        if (isHighwayFlag == null || isHighwayFlag) {
            currentHighway = detectHighway(normalizedLatitude, normalizedLongitude);
        }

        LocationTracking locationTracking = new LocationTracking(
                request.getVehicleId(),
                normalizedLatitude,
                normalizedLongitude,
                timestamp);

        if (!previousLocations.isEmpty()) {
            LocationTracking previousLocation = previousLocations.get(0);
            stateProcessor.processHighwayDetectionAndDistance(
                    locationTracking,
                    previousLocation,
                    currentHighway,
                    timestamp);
        } else {
            if (currentHighway != null) {
                locationTracking.setIsOnHighway(true);
                locationTracking.setHighwayId(currentHighway.getHighwayId());
                highwayUsageService.createHighwaySession(
                        request.getVehicleId(),
                        currentHighway.getHighwayId(),
                        timestamp,
                        normalizedLatitude,
                        normalizedLongitude);
            }
        }

        LocationTracking savedLocation = locationTrackingService.saveLocation(locationTracking);

        try {
            anomalyDetectionService.runAllChecks(savedLocation);
        } catch (Exception e) {
            System.err.println("Anomaly detection error (non-critical): " + e.getMessage());
        }

        return savedLocation;
    }

    private Highway detectHighway(double latitude, double longitude) {
        List<Highway> allHighways = highwayService.getAllHighways();
        for (Highway highway : allHighways) {
            if (highwayDetectionService.isWithinHighwayRange(latitude, longitude, highway)) {
                return highway;
            }
        }
        return null;
    }
}
