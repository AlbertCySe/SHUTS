package com.highway.tolling.controller;

import com.highway.tolling.service.HighwayUsageAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Highway Usage Controller
 * REST endpoints for querying aggregated highway usage data
 */
@RestController
@RequestMapping("/api/highway-usage")
public class HighwayUsageController {

    private final HighwayUsageAggregationService aggregationService;

    @Autowired
    public HighwayUsageController(HighwayUsageAggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    /**
     * Get total highway distance for a vehicle
     * GET /api/highway-usage/total/{vehicleId}
     * 
     * Example: GET /api/highway-usage/total/1
     * Response: { "vehicleId": 1, "totalDistance": 45.67 }
     */
    @GetMapping("/total/{vehicleId}")
    public ResponseEntity<?> getTotalDistance(@PathVariable Long vehicleId) {
        try {
            Double totalDistance = aggregationService.getTotalHighwayDistance(vehicleId);
            Map<String, Object> response = Map.of(
                    "vehicleId", vehicleId,
                    "totalDistance", totalDistance,
                    "unit", "km");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving total distance: " + e.getMessage());
        }
    }

    /**
     * Get distance breakdown by highway for a vehicle
     * GET /api/highway-usage/breakdown/{vehicleId}
     * 
     * Example: GET /api/highway-usage/breakdown/1
     * Response: { "NH-44": 25.5, "NH-75": 20.17 }
     */
    @GetMapping("/breakdown/{vehicleId}")
    public ResponseEntity<?> getDistanceBreakdown(@PathVariable Long vehicleId) {
        try {
            Map<String, Double> breakdown = aggregationService.getDistanceByHighway(vehicleId);
            return ResponseEntity.ok(breakdown);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving distance breakdown: " + e.getMessage());
        }
    }

    /**
     * Get complete usage summary for a vehicle
     * GET /api/highway-usage/summary/{vehicleId}
     * 
     * Example: GET /api/highway-usage/summary/1
     * Response: {
     * "vehicleId": 1,
     * "totalDistance": 45.67,
     * "distanceByHighway": { "NH-44": 25.5, "NH-75": 20.17 },
     * "totalSessions": 8
     * }
     */
    @GetMapping("/summary/{vehicleId}")
    public ResponseEntity<?> getUsageSummary(@PathVariable Long vehicleId) {
        try {
            HighwayUsageAggregationService.VehicleUsageSummary summary = aggregationService
                    .getVehicleUsageSummary(vehicleId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving usage summary: " + e.getMessage());
        }
    }
}
