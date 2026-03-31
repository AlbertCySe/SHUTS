package com.highway.tolling.iot.simulator.service;

import com.highway.tolling.iot.simulator.model.GPSPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * GPS Generator Service
 * Generates realistic GPS coordinates with movement and noise
 */
@Service
public class GPSGenerator {

    private final Random random = new Random();

    // Earth radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Generate next GPS point based on current position, speed, and direction
     *
     * @param current         GPS point
     * @param speedKmh        Current speed in km/h
     * @param intervalSeconds Time interval since last point
     * @param bearing         Direction in degrees (0 = North, 90 = East, 180 =
     *                        South, 270 = West)
     * @return New GPS point
     */
    public GPSPoint generateNextPoint(GPSPoint current, double speedKmh, int intervalSeconds, double bearing) {
        // Convert speed to distance traveled
        double distanceKm = (speedKmh * intervalSeconds) / 3600.0;

        // Calculate new position
        double lat1 = Math.toRadians(current.getLatitude());
        double lon1 = Math.toRadians(current.getLongitude());
        double bearingRad = Math.toRadians(bearing);

        double lat2 = Math.asin(
                Math.sin(lat1) * Math.cos(distanceKm / EARTH_RADIUS_KM) +
                        Math.cos(lat1) * Math.sin(distanceKm / EARTH_RADIUS_KM) * Math.cos(bearingRad));

        double lon2 = lon1 + Math.atan2(
                Math.sin(bearingRad) * Math.sin(distanceKm / EARTH_RADIUS_KM) * Math.cos(lat1),
                Math.cos(distanceKm / EARTH_RADIUS_KM) - Math.sin(lat1) * Math.sin(lat2));

        double newLat = Math.toDegrees(lat2);
        double newLon = Math.toDegrees(lon2);

        // Add GPS noise (±5 meters ≈ ±0.00005 degrees)
        newLat = addNoise(newLat, 0.00005);
        newLon = addNoise(newLon, 0.00005);

        return new GPSPoint(newLat, newLon, LocalDateTime.now());
    }

    /**
     * Move towards a target GPS point
     */
    public GPSPoint moveTowards(GPSPoint current, GPSPoint target, double speedKmh, int intervalSeconds) {
        // Calculate bearing to target
        double bearing = calculateBearing(current, target);

        // Generate next point in that direction
        return generateNextPoint(current, speedKmh, intervalSeconds, bearing);
    }

    /**
     * Calculate bearing from point A to point B
     */
    public double calculateBearing(GPSPoint from, GPSPoint to) {
        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());
        double lon1 = Math.toRadians(from.getLongitude());
        double lon2 = Math.toRadians(to.getLongitude());

        double dLon = lon2 - lon1;

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        double bearingRad = Math.atan2(y, x);
        double bearingDeg = Math.toDegrees(bearingRad);

        return (bearingDeg + 360) % 360; // Normalize to 0-360
    }

    /**
     * Calculate distance between two GPS points in kilometers
     */
    public double calculateDistance(GPSPoint from, GPSPoint to) {
        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());
        double lon1 = Math.toRadians(from.getLongitude());
        double lon2 = Math.toRadians(to.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Add random noise to simulate GPS inaccuracy
     */
    private double addNoise(double value, double maxNoise) {
        double noise = (random.nextDouble() - 0.5) * 2 * maxNoise;
        return value + noise;
    }

    /**
     * Check if vehicle has reached target (within 50 meters)
     */
    public boolean hasReachedTarget(GPSPoint current, GPSPoint target) {
        return calculateDistance(current, target) < 0.05; // 50 meters
    }
}
