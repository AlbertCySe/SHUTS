package com.highway.tolling.controller;

import com.highway.tolling.model.Highway;
import com.highway.tolling.model.Vehicle;
import com.highway.tolling.model.VehicleType;
import com.highway.tolling.service.HighwayService;
import com.highway.tolling.service.TollCalculationService;
import com.highway.tolling.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Toll Calculation Controller
 * Simple REST endpoints for calculating toll prices
 */
@RestController
@RequestMapping("/api/toll")
public class TollCalculationController {

    private final TollCalculationService tollCalculationService;
    private final VehicleService vehicleService;
    private final HighwayService highwayService;

    @Autowired
    public TollCalculationController(TollCalculationService tollCalculationService,
            VehicleService vehicleService,
            HighwayService highwayService) {
        this.tollCalculationService = tollCalculationService;
        this.vehicleService = vehicleService;
        this.highwayService = highwayService;
    }

    /**
     * Calculate toll amount - Simple version
     * POST /api/toll/calculate
     * 
     * Body:
     * {
     * "vehicleType": "CAR",
     * "distanceKm": 45.5,
     * "ratePerKm": 2.50
     * }
     * 
     * Response:
     * {
     * "vehicleType": "CAR",
     * "distanceKm": 45.5,
     * "ratePerKm": 2.50,
     * "totalToll": 113.75
     * }
     */
    @PostMapping("/calculate")
    public ResponseEntity<?> calculateToll(@RequestBody TollCalculationRequest request) {
        try {
            // Validate input
            if (request.getVehicleType() == null) {
                return ResponseEntity.badRequest().body("Vehicle type is required");
            }
            if (request.getDistanceKm() == null || request.getDistanceKm() <= 0) {
                return ResponseEntity.badRequest().body("Distance must be greater than 0");
            }

            // Apply default rates if not provided
            double carRate = request.getRatePerKmForCar() != null ? request.getRatePerKmForCar() : 2.50;
            double bikeRate = request.getRatePerKmForBike() != null ? request.getRatePerKmForBike() : 1.50;
            double truckRate = request.getRatePerKmForTruck() != null ? request.getRatePerKmForTruck() : 5.00;

            // Calculate toll
            double totalToll = tollCalculationService.calculateToll(
                    request.getVehicleType(),
                    request.getDistanceKm(),
                    carRate,
                    bikeRate,
                    truckRate);

            // Get the rate used for this vehicle type
            double rateUsed = getRateForVehicleType(request.getVehicleType(), carRate, bikeRate, truckRate);

            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("vehicleType", request.getVehicleType());
            response.put("distanceKm", request.getDistanceKm());
            response.put("ratePerKm", rateUsed);
            response.put("totalToll", Math.round(totalToll * 100.0) / 100.0);
            response.put("currency", "INR");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating toll: " + e.getMessage());
        }
    }

    /**
     * Calculate toll for a specific vehicle and highway
     * GET /api/toll/calculate/{vehicleId}/{highwayId}/{distanceKm}
     * 
     * Example: GET /api/toll/calculate/1/1/45.5
     */
    @GetMapping("/calculate/{vehicleId}/{highwayId}/{distanceKm}")
    public ResponseEntity<?> calculateTollForVehicleAndHighway(
            @PathVariable Long vehicleId,
            @PathVariable Long highwayId,
            @PathVariable Double distanceKm) {
        try {
            // Get vehicle to determine type
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));

            // Get highway to get toll rates
            Highway highway = highwayService.getHighwayById(highwayId)
                    .orElseThrow(() -> new RuntimeException("Highway not found with ID: " + highwayId));

            // Calculate toll with details
            TollCalculationService.TollCalculationResult result = tollCalculationService.calculateTollWithDetails(
                    vehicle.getVehicleType(),
                    distanceKm,
                    highway);

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating toll: " + e.getMessage());
        }
    }

    /**
     * Helper method to get rate for vehicle type
     */
    private double getRateForVehicleType(VehicleType vehicleType,
            double carRate, double bikeRate, double truckRate) {
        // Basic conditional logic
        if (vehicleType == VehicleType.CAR) {
            return carRate;
        } else if (vehicleType == VehicleType.BIKE) {
            return bikeRate;
        } else if (vehicleType == VehicleType.BUS || vehicleType == VehicleType.TRUCK) {
            return truckRate;
        } else {
            return 0.0;
        }
    }

    /**
     * Inner class for request body
     */
    public static class TollCalculationRequest {
        private VehicleType vehicleType;
        private Double distanceKm;
        private Double ratePerKmForCar;
        private Double ratePerKmForBike;
        private Double ratePerKmForTruck;

        // Getters and Setters
        public VehicleType getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(VehicleType vehicleType) {
            this.vehicleType = vehicleType;
        }

        public Double getDistanceKm() {
            return distanceKm;
        }

        public void setDistanceKm(Double distanceKm) {
            this.distanceKm = distanceKm;
        }

        public Double getRatePerKmForCar() {
            return ratePerKmForCar;
        }

        public void setRatePerKmForCar(Double ratePerKmForCar) {
            this.ratePerKmForCar = ratePerKmForCar;
        }

        public Double getRatePerKmForBike() {
            return ratePerKmForBike;
        }

        public void setRatePerKmForBike(Double ratePerKmForBike) {
            this.ratePerKmForBike = ratePerKmForBike;
        }

        public Double getRatePerKmForTruck() {
            return ratePerKmForTruck;
        }

        public void setRatePerKmForTruck(Double ratePerKmForTruck) {
            this.ratePerKmForTruck = ratePerKmForTruck;
        }
    }
}
