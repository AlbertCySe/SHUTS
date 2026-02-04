package com.highway.tolling.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * LocationTracking Entity
 * Represents GPS location data from IoT devices on vehicles
 */
@Entity
@Table(name = "location_tracking")
public class LocationTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long vehicleId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private Long highwayId; // null if not on highway

    @Column
    private Double distanceFromPrevious; // distance from previous GPS point in km

    @Column(nullable = false)
    private Boolean isOnHighway; // whether this location is on a highway

    // Constructors
    public LocationTracking() {
        this.timestamp = LocalDateTime.now();
        this.isOnHighway = false;
    }

    public LocationTracking(Long vehicleId, Double latitude, Double longitude) {
        this.vehicleId = vehicleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = LocalDateTime.now();
        this.isOnHighway = false;
    }

    public LocationTracking(Long vehicleId, Double latitude, Double longitude, LocalDateTime timestamp) {
        this.vehicleId = vehicleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.isOnHighway = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getHighwayId() {
        return highwayId;
    }

    public void setHighwayId(Long highwayId) {
        this.highwayId = highwayId;
    }

    public Double getDistanceFromPrevious() {
        return distanceFromPrevious;
    }

    public void setDistanceFromPrevious(Double distanceFromPrevious) {
        this.distanceFromPrevious = distanceFromPrevious;
    }

    public Boolean getIsOnHighway() {
        return isOnHighway;
    }

    public void setIsOnHighway(Boolean isOnHighway) {
        this.isOnHighway = isOnHighway;
    }

    @Override
    public String toString() {
        return "LocationTracking{" +
                "id=" + id +
                ", vehicleId=" + vehicleId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                ", highwayId=" + highwayId +
                ", distanceFromPrevious=" + distanceFromPrevious + " km" +
                ", isOnHighway=" + isOnHighway +
                '}';
    }
}
