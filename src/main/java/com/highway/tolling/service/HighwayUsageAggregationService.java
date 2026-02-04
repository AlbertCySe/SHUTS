package com.highway.tolling.service;

import com.highway.tolling.model.Highway;
import com.highway.tolling.model.HighwayUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Highway Usage Aggregation Service
 * Provides simple methods to aggregate highway usage data for billing
 */
@Service
public class HighwayUsageAggregationService {

    private final HighwayUsageService highwayUsageService;
    private final HighwayService highwayService;

    @Autowired
    public HighwayUsageAggregationService(HighwayUsageService highwayUsageService,
            HighwayService highwayService) {
        this.highwayUsageService = highwayUsageService;
        this.highwayService = highwayService;
    }

    /**
     * Get total highway distance for a vehicle (all highways combined)
     * Simple one-line call for billing
     */
    public Double getTotalHighwayDistance(Long vehicleId) {
        return highwayUsageService.getTotalDistanceByVehicle(vehicleId);
    }

    /**
     * Get detailed breakdown by highway
     * Returns map of highway name -> distance traveled
     */
    public Map<String, Double> getDistanceByHighway(Long vehicleId) {
        List<HighwayUsage> usageRecords = highwayUsageService.getVehicleHighwayUsage(vehicleId);
        Map<String, Double> breakdown = new HashMap<>();

        for (HighwayUsage usage : usageRecords) {
            Highway highway = highwayService.getHighwayById(usage.getHighwayId())
                    .orElse(null);

            if (highway != null) {
                String highwayName = highway.getHighwayName();
                breakdown.put(highwayName,
                        breakdown.getOrDefault(highwayName, 0.0) + usage.getDistanceTraveled());
            }
        }

        return breakdown;
    }

    /**
     * Get aggregation summary for a vehicle
     * All information needed for billing in one call
     */
    public VehicleUsageSummary getVehicleUsageSummary(Long vehicleId) {
        Double totalDistance = getTotalHighwayDistance(vehicleId);
        Map<String, Double> breakdown = getDistanceByHighway(vehicleId);
        int totalSessions = highwayUsageService.getVehicleHighwayUsage(vehicleId).size();

        return new VehicleUsageSummary(vehicleId, totalDistance, breakdown, totalSessions);
    }

    /**
     * Inner class for usage summary
     */
    public static class VehicleUsageSummary {
        private Long vehicleId;
        private Double totalDistance;
        private Map<String, Double> distanceByHighway;
        private int totalSessions;

        public VehicleUsageSummary(Long vehicleId, Double totalDistance,
                Map<String, Double> distanceByHighway, int totalSessions) {
            this.vehicleId = vehicleId;
            this.totalDistance = totalDistance;
            this.distanceByHighway = distanceByHighway;
            this.totalSessions = totalSessions;
        }

        // Getters
        public Long getVehicleId() {
            return vehicleId;
        }

        public Double getTotalDistance() {
            return totalDistance;
        }

        public Map<String, Double> getDistanceByHighway() {
            return distanceByHighway;
        }

        public int getTotalSessions() {
            return totalSessions;
        }

        @Override
        public String toString() {
            return "VehicleUsageSummary{" +
                    "vehicleId=" + vehicleId +
                    ", totalDistance=" + totalDistance + " km" +
                    ", distanceByHighway=" + distanceByHighway +
                    ", totalSessions=" + totalSessions +
                    '}';
        }
    }
}
