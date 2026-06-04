package com.highway.tolling.controller;

import com.highway.tolling.model.LocationTracking;
import com.highway.tolling.repository.LocationTrackingRepository;
import com.highway.tolling.service.LocationTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Location Tracking Controller
 * Provides endpoints for location history retrieval
 */
@RestController
@RequestMapping("/api/locations")
public class LocationTrackingController {

    private final LocationTrackingRepository locationTrackingRepository;
    private final LocationTrackingService locationTrackingService;

    @Autowired
    public LocationTrackingController(LocationTrackingRepository locationTrackingRepository,
            LocationTrackingService locationTrackingService) {
        this.locationTrackingRepository = locationTrackingRepository;
        this.locationTrackingService = locationTrackingService;
    }

    /**
     * Get recent location records (Diagnostic View)
     * GET /api/locations
     * Limited to 200 records for performance
     */
    @GetMapping
    public ResponseEntity<List<LocationTracking>> getRecentLocations() {
        return ResponseEntity.ok(locationTrackingRepository.findAll(
                PageRequest.of(0, 200, Sort.by(Sort.Direction.DESC, "timestamp"))).getContent());
    }

    /**
     * Get latest tracking data for a vehicle
     * GET /api/locations/vehicle/{vehicleId}/latest
     */
    @GetMapping("/vehicle/{vehicleId}/latest")
    public ResponseEntity<LocationTracking> getLatestVehicleLocation(@PathVariable Long vehicleId) {
        return locationTrackingService.getLatestLocationByVehicleId(vehicleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get recent location history for a vehicle (last 20 records)
     * GET /api/locations/vehicle/{vehicleId}/history
     */
    @GetMapping("/vehicle/{vehicleId}/history")
    public ResponseEntity<List<LocationTracking>> getVehicleLocationHistory(@PathVariable Long vehicleId) {
        List<LocationTracking> history = locationTrackingRepository
                .findByVehicleIdOrderByTimestampDesc(vehicleId);
        // Return at most 20 records
        return ResponseEntity.ok(history.stream().limit(20).toList());
    }
}

