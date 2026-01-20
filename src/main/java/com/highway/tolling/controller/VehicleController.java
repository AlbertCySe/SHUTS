package com.highway.tolling.controller;

import com.highway.tolling.model.Vehicle;
import com.highway.tolling.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vehicle Controller
 * REST API endpoints for vehicle operations
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Register a new vehicle
     * POST /api/vehicles
     */
    @PostMapping
    public ResponseEntity<Vehicle> registerVehicle(@RequestBody Vehicle vehicle) {
        try {
            Vehicle registeredVehicle = vehicleService.registerVehicle(vehicle);
            return new ResponseEntity<>(registeredVehicle, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all vehicles
     * GET /api/vehicles
     */
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    /**
     * Get vehicle by ID
     * GET /api/vehicles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(vehicle -> new ResponseEntity<>(vehicle, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get vehicle by vehicle number
     * GET /api/vehicles/search?number={vehicleNumber}
     */
    @GetMapping("/search")
    public ResponseEntity<Vehicle> getVehicleByNumber(@RequestParam String number) {
        return vehicleService.getVehicleByNumber(number)
                .map(vehicle -> new ResponseEntity<>(vehicle, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Update vehicle information
     * PUT /api/vehicles/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicle);
            return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete a vehicle
     * DELETE /api/vehicles/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Health check endpoint
     * GET /api/vehicles/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("Tolling System API is running!", HttpStatus.OK);
    }
}
