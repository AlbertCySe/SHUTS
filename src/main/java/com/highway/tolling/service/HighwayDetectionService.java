package com.highway.tolling.service;

import com.highway.tolling.model.Highway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Highway Detection Service
 * Checks whether a vehicle's GPS location lies within a highway's range
 * Uses simple bounding box logic for academic simulation
 */
@Service
public class HighwayDetectionService {

    // Tolerance in degrees (approximately 5-10 km buffer zone)
    // 0.05 degrees ≈ 5.5 km at the equator
    private static final double TOLERANCE_DEGREES = 0.05;

    private final DistanceCalculatorService distanceCalculator;

    @Autowired
    public HighwayDetectionService(DistanceCalculatorService distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    /**
     * Check if a GPS location is within a highway's geographical range
     * 
     * This method uses a simple bounding box approach:
     * 1. Creates a rectangular boundary around the highway (start to end
     * coordinates)
     * 2. Adds a tolerance buffer to account for highway width and GPS inaccuracy
     * 3. Checks if the vehicle's location falls within this expanded boundary
     * 
     * Note: This is a simplified approach suitable for academic purposes.
     * Real-world systems would use more complex algorithms (e.g., point-to-line
     * distance).
     * 
     * @param vehicleLat Vehicle's current latitude
     * @param vehicleLon Vehicle's current longitude
     * @param highway    The highway to check against
     * @return true if the vehicle is within the highway range, false otherwise
     */
    public boolean isWithinHighwayRange(double vehicleLat, double vehicleLon, Highway highway) {
        // Get highway start and end coordinates
        double startLat = highway.getStartLatitude();
        double startLon = highway.getStartLongitude();
        double endLat = highway.getEndLatitude();
        double endLon = highway.getEndLongitude();

        // Calculate bounding box (min and max coordinates)
        double minLat = Math.min(startLat, endLat) - TOLERANCE_DEGREES;
        double maxLat = Math.max(startLat, endLat) + TOLERANCE_DEGREES;
        double minLon = Math.min(startLon, endLon) - TOLERANCE_DEGREES;
        double maxLon = Math.max(startLon, endLon) + TOLERANCE_DEGREES;

        // Check if vehicle location is within the bounding box
        boolean withinBox = (vehicleLat >= minLat && vehicleLat <= maxLat)
                && (vehicleLon >= minLon && vehicleLon <= maxLon);

        return withinBox;
    }

    /**
     * Check if a GPS location is within a highway's range with custom tolerance
     * 
     * @param vehicleLat       Vehicle's current latitude
     * @param vehicleLon       Vehicle's current longitude
     * @param highway          The highway to check against
     * @param toleranceDegrees Custom tolerance in degrees
     * @return true if the vehicle is within the highway range, false otherwise
     */
    public boolean isWithinHighwayRange(double vehicleLat, double vehicleLon,
            Highway highway, double toleranceDegrees) {
        double startLat = highway.getStartLatitude();
        double startLon = highway.getStartLongitude();
        double endLat = highway.getEndLatitude();
        double endLon = highway.getEndLongitude();

        double minLat = Math.min(startLat, endLat) - toleranceDegrees;
        double maxLat = Math.max(startLat, endLat) + toleranceDegrees;
        double minLon = Math.min(startLon, endLon) - toleranceDegrees;
        double maxLon = Math.max(startLon, endLon) + toleranceDegrees;

        return (vehicleLat >= minLat && vehicleLat <= maxLat)
                && (vehicleLon >= minLon && vehicleLon <= maxLon);
    }

    /**
     * Get the distance of vehicle from the nearest highway point (start or end)
     * This can be used to verify highway proximity
     * 
     * @param vehicleLat Vehicle's current latitude
     * @param vehicleLon Vehicle's current longitude
     * @param highway    The highway to check against
     * @return Distance in kilometers to the nearest highway endpoint
     */
    public double getDistanceToNearestHighwayPoint(double vehicleLat, double vehicleLon, Highway highway) {
        // Calculate distance to start point
        double distanceToStart = distanceCalculator.calculateDistance(
                vehicleLat, vehicleLon,
                highway.getStartLatitude(), highway.getStartLongitude());

        // Calculate distance to end point
        double distanceToEnd = distanceCalculator.calculateDistance(
                vehicleLat, vehicleLon,
                highway.getEndLatitude(), highway.getEndLongitude());

        // Return the shorter distance
        return Math.min(distanceToStart, distanceToEnd);
    }

    /**
     * Create a detailed result object for highway detection
     * 
     * @param vehicleLat Vehicle's current latitude
     * @param vehicleLon Vehicle's current longitude
     * @param highway    The highway to check against
     * @return HighwayDetectionResult with detection details
     */
    public HighwayDetectionResult detectHighwayUsage(double vehicleLat, double vehicleLon, Highway highway) {
        boolean isWithinRange = isWithinHighwayRange(vehicleLat, vehicleLon, highway);
        double nearestDistance = getDistanceToNearestHighwayPoint(vehicleLat, vehicleLon, highway);

        return new HighwayDetectionResult(
                isWithinRange,
                highway.getHighwayId(),
                highway.getHighwayName(),
                nearestDistance);
    }

    /**
     * Inner class to hold highway detection results
     */
    public static class HighwayDetectionResult {
        private boolean onHighway;
        private Long highwayId;
        private String highwayName;
        private double distanceToHighway;

        public HighwayDetectionResult(boolean onHighway, Long highwayId,
                String highwayName, double distanceToHighway) {
            this.onHighway = onHighway;
            this.highwayId = highwayId;
            this.highwayName = highwayName;
            this.distanceToHighway = distanceToHighway;
        }

        // Getters
        public boolean isOnHighway() {
            return onHighway;
        }

        public Long getHighwayId() {
            return highwayId;
        }

        public String getHighwayName() {
            return highwayName;
        }

        public double getDistanceToHighway() {
            return distanceToHighway;
        }

        @Override
        public String toString() {
            return "HighwayDetectionResult{" +
                    "onHighway=" + onHighway +
                    ", highwayId=" + highwayId +
                    ", highwayName='" + highwayName + '\'' +
                    ", distanceToHighway=" + distanceToHighway + " km" +
                    '}';
        }
    }
}
