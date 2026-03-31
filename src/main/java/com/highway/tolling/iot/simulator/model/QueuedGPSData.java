package com.highway.tolling.iot.simulator.model;

import java.time.LocalDateTime;

/**
 * Queued GPS Data
 * Represents GPS data waiting to be sent to backend
 */
public class QueuedGPSData {
    private Long vehicleId;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
    private int retryCount;

    public QueuedGPSData(Long vehicleId, double latitude, double longitude, LocalDateTime timestamp) {
        this.vehicleId = vehicleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.retryCount = 0;
    }

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

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

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    @Override
    public String toString() {
        return String.format("QueuedGPSData{vehicleId=%d, lat=%.6f, lon=%.6f, timestamp=%s, retries=%d}",
                vehicleId, latitude, longitude, timestamp, retryCount);
    }
}
