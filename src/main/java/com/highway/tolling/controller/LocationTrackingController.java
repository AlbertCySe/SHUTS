package com.highway.tolling.controller;

import com.highway.tolling.model.LocationTracking;
import com.highway.tolling.service.LocationTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LocationTracking Controller
 * REST API endpoints for vehicle location tracking (IoT GPS data)
 */
@RestController
@RequestMapping("/api/locations")
public class LocationTrackingController {

    private final LocationTrackingService locationTrackingService;

    @Autowired
    public LocationTrackingController(LocationTrackingService locationTrackingService) {
        this.locationTrackingService = locationTrackingService;
    }

    /**
     * Save location data sent from vehicle IoT device
     * POST /api/locations
     * Request body: {"vehicleId": 1, "latitude": 19.0760, "longitude": 72.8777}
     */
    @PostMapping
    public ResponseEntity<LocationTracking> saveLocation(@RequestBody LocationTracking locationTracking) {
        try {
            LocationTracking savedLocation = locationTrackingService.saveLocation(locationTracking);
            return new ResponseEntity<>(savedLocation, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all location records for a specific vehicle
     * GET /api/locations/vehicle/{vehicleId}
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<LocationTracking>> getVehicleLocations(@PathVariable Long vehicleId) {
        List<LocationTracking> locations = locationTrackingService.getLocationsByVehicleId(vehicleId);
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    /**
     * Get a single location record by ID
     * GET /api/locations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<LocationTracking> getLocationById(@PathVariable Long id) {
        return locationTrackingService.getLocationById(id)
                .map(location -> new ResponseEntity<>(location, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get all location records
     * GET /api/locations
     */
    @GetMapping
    public ResponseEntity<List<LocationTracking>> getAllLocations() {
        List<LocationTracking> locations = locationTrackingService.getAllLocations();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }
}
