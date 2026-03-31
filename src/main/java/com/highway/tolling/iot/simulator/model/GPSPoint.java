package com.highway.tolling.iot.simulator.model;

import java.time.LocalDateTime;

/**
 * GPS Point Model
 * Represents a single GPS coordinate with timestamp
 */
public class GPSPoint {
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;

    public GPSPoint() {
        this.timestamp = LocalDateTime.now();
    }

    public GPSPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = LocalDateTime.now();
    }

    public GPSPoint(double latitude, double longitude, LocalDateTime timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("GPSPoint(%.6f, %.6f) at %s", latitude, longitude, timestamp);
    }
}
