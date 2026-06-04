package com.highway.tolling.service;

import com.highway.tolling.model.Highway;
import com.highway.tolling.model.HighwayUsage;
import com.highway.tolling.model.Vehicle;
import com.highway.tolling.model.VehicleType;
import com.highway.tolling.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Highway Usage Aggregation Service
 * Provides methods to aggregate highway usage data for statistics and billing
 */
@Service
public class HighwayUsageAggregationService {

    private final HighwayUsageService highwayUsageService;
    private final HighwayService highwayService;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public HighwayUsageAggregationService(HighwayUsageService highwayUsageService,
            HighwayService highwayService,
            VehicleRepository vehicleRepository) {
        this.highwayUsageService = highwayUsageService;
        this.highwayService = highwayService;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Get complete usage summary for a vehicle.
     * Used by HighwayUsageController.
     */
    public VehicleUsageSummary getVehicleUsageSummary(Long vehicleId) {
        List<HighwayUsage> usageList = highwayUsageService.getVehicleHighwayUsage(vehicleId);
        double totalDistance = 0.0;
        Map<String, Double> distanceByHighway = new HashMap<>();

        for (HighwayUsage usage : usageList) {
            totalDistance += usage.getDistanceTraveled();
            final Double dist = usage.getDistanceTraveled();
            highwayService.getHighwayById(usage.getHighwayId()).ifPresent(h -> {
                distanceByHighway.merge(h.getHighwayName(), dist, Double::sum);
            });
        }

        return new VehicleUsageSummary(vehicleId, totalDistance, distanceByHighway, usageList.size());
    }

    /**
     * Calculate total toll for a single vehicle in a specific month using highway-specific rates.
     */
    public double calculateTollForMonth(Long vehicleId, YearMonth month) {
        List<HighwayUsage> usageRecords = highwayUsageService.getVehicleHighwayUsage(vehicleId);
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle == null) return 0.0;

        double totalToll = 0.0;
        for (HighwayUsage usage : usageRecords) {
            if (usage.getEntryTimestamp() != null &&
                    YearMonth.from(usage.getEntryTimestamp()).equals(month)) {
                Highway highway = highwayService.getHighwayById(usage.getHighwayId()).orElse(null);
                if (highway != null) {
                    double rate = getRateForVehicleType(highway, vehicle.getVehicleType());
                    totalToll += usage.getDistanceTraveled() * rate;
                }
            }
        }
        return totalToll;
    }

    /**
     * Calculate total consolidated toll for all vehicles of a user for a specific month.
     */
    public double calculateTotalUserTollForMonth(Long userId, YearMonth month) {
        List<Vehicle> vehicles = vehicleRepository.findByUser_UserId(userId);
        double totalToll = 0.0;
        for (Vehicle vehicle : vehicles) {
            totalToll += calculateTollForMonth(vehicle.getVehicleId(), month);
        }
        return totalToll;
    }

    private double getRateForVehicleType(Highway highway, VehicleType type) {
        if (type == null) return highway.getRatePerKmForCar();
        switch (type) {
            case CAR:  return highway.getRatePerKmForCar();
            case BIKE: return highway.getRatePerKmForBike();
            case TRUCK:
            case BUS:  return highway.getRatePerKmForTruck();
            default:   return highway.getRatePerKmForCar();
        }
    }

    /**
     * Inner class for vehicle usage summary response (used by HighwayUsageController).
     */
    public static class VehicleUsageSummary {
        private Long vehicleId;
        private double totalDistance;
        private Map<String, Double> distanceByHighway;
        private int totalSessions;

        public VehicleUsageSummary(Long vehicleId, double totalDistance,
                Map<String, Double> distanceByHighway, int totalSessions) {
            this.vehicleId = vehicleId;
            this.totalDistance = totalDistance;
            this.distanceByHighway = distanceByHighway;
            this.totalSessions = totalSessions;
        }

        public Long getVehicleId() { return vehicleId; }
        public double getTotalDistance() { return totalDistance; }
        public Map<String, Double> getDistanceByHighway() { return distanceByHighway; }
        public int getTotalSessions() { return totalSessions; }
    }
}
