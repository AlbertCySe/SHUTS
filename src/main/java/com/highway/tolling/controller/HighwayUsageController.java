package com.highway.tolling.controller;

import com.highway.tolling.model.HighwayUsage;
import com.highway.tolling.repository.HighwayUsageRepository;
import com.highway.tolling.service.HighwayUsageAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Highway Usage Controller
 * REST endpoints for querying aggregated highway usage data
 */
@RestController
@RequestMapping("/api/highway-usage")
public class HighwayUsageController {

    private final HighwayUsageAggregationService aggregationService;
    private final HighwayUsageRepository highwayUsageRepository;

    @Autowired
    public HighwayUsageController(HighwayUsageAggregationService aggregationService,
            HighwayUsageRepository highwayUsageRepository) {
        this.aggregationService = aggregationService;
        this.highwayUsageRepository = highwayUsageRepository;
    }

    /**
     * Get all usage records (Diagnostic View)
     * GET /api/highway-usage
     */
    @GetMapping
    public ResponseEntity<List<HighwayUsage>> getAllUsage() {
        return ResponseEntity.ok(highwayUsageRepository.findAll());
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
