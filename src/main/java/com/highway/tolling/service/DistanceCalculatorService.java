package com.highway.tolling.service;

import org.springframework.stereotype.Service;

/**
 * Distance Calculator Service
 * Calculates the distance between two GPS coordinates using the Haversine
 * formula
 */
@Service
public class DistanceCalculatorService {

    // Earth's radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate distance between two GPS coordinates using Haversine formula
     * 
     * The Haversine formula calculates the great-circle distance between two points
     * on a sphere (Earth) given their latitude and longitude coordinates.
     * 
     * Formula:
     * a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlon/2)
     * c = 2 * atan2(√a, √(1−a))
     * distance = R * c
     * 
     * where:
     * - Δlat = lat2 - lat1 (difference in latitude)
     * - Δlon = lon2 - lon1 (difference in longitude)
     * - R = Earth's radius (6371 km)
     * 
     * @param lat1 Latitude of the first point (in decimal degrees)
     * @param lon1 Longitude of the first point (in decimal degrees)
     * @param lat2 Latitude of the second point (in decimal degrees)
     * @param lon2 Longitude of the second point (in decimal degrees)
     * @return Distance between the two points in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate differences in coordinates
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Apply Haversine formula
        // Step 1: Calculate 'a' (square of half the chord length)
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                        * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        // Step 2: Calculate 'c' (angular distance in radians)
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Step 3: Calculate the distance
        double distance = EARTH_RADIUS_KM * c;

        return distance;
    }

    /**
     * Calculate distance and round to specified decimal places
     * 
     * @param lat1          Latitude of the first point
     * @param lon1          Longitude of the first point
     * @param lat2          Latitude of the second point
     * @param lon2          Longitude of the second point
     * @param decimalPlaces Number of decimal places for rounding
     * @return Distance in kilometers, rounded to specified decimal places
     */
    public double calculateDistanceRounded(double lat1, double lon1, double lat2, double lon2, int decimalPlaces) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(distance * scale) / scale;
    }
}
