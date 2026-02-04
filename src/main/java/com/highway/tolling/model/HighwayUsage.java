package com.highway.tolling.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * HighwayUsage Entity
 * Tracks individual highway usage sessions for vehicles
 * Records entry/exit points and accumulated distance
 */
@Entity
@Table(name = "highway_usage")
public class HighwayUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long vehicleId;

    @Column(nullable = false)
    private Long highwayId;

    @Column(nullable = false)
    private Double distanceTraveled; // in kilometers

    @Column(nullable = false)
    private LocalDateTime entryTimestamp;

    @Column
    private LocalDateTime exitTimestamp; // null if session is active

    @Column(nullable = false)
    private Double entryLatitude;

    @Column(nullable = false)
    private Double entryLongitude;

    @Column
    private Double exitLatitude;

    @Column
    private Double exitLongitude;

    // Constructors
    public HighwayUsage() {
        this.distanceTraveled = 0.0;
    }

    public HighwayUsage(Long vehicleId, Long highwayId, LocalDateTime entryTimestamp,
            Double entryLatitude, Double entryLongitude) {
        this.vehicleId = vehicleId;
        this.highwayId = highwayId;
        this.entryTimestamp = entryTimestamp;
        this.entryLatitude = entryLatitude;
        this.entryLongitude = entryLongitude;
        this.distanceTraveled = 0.0;
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

    public Long getHighwayId() {
        return highwayId;
    }

    public void setHighwayId(Long highwayId) {
        this.highwayId = highwayId;
    }

    public Double getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(Double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    public LocalDateTime getEntryTimestamp() {
        return entryTimestamp;
    }

    public void setEntryTimestamp(LocalDateTime entryTimestamp) {
        this.entryTimestamp = entryTimestamp;
    }

    public LocalDateTime getExitTimestamp() {
        return exitTimestamp;
    }

    public void setExitTimestamp(LocalDateTime exitTimestamp) {
        this.exitTimestamp = exitTimestamp;
    }

    public Double getEntryLatitude() {
        return entryLatitude;
    }

    public void setEntryLatitude(Double entryLatitude) {
        this.entryLatitude = entryLatitude;
    }

    public Double getEntryLongitude() {
        return entryLongitude;
    }

    public void setEntryLongitude(Double entryLongitude) {
        this.entryLongitude = entryLongitude;
    }

    public Double getExitLatitude() {
        return exitLatitude;
    }

    public void setExitLatitude(Double exitLatitude) {
        this.exitLatitude = exitLatitude;
    }

    public Double getExitLongitude() {
        return exitLongitude;
    }

    public void setExitLongitude(Double exitLongitude) {
        this.exitLongitude = exitLongitude;
    }

    /**
     * Check if this highway session is still active (no exit timestamp)
     */
    public boolean isActive() {
        return exitTimestamp == null;
    }

    /**
     * Add distance to the current session
     */
    public void addDistance(Double distance) {
        this.distanceTraveled += distance;
    }

    @Override
    public String toString() {
        return "HighwayUsage{" +
                "id=" + id +
                ", vehicleId=" + vehicleId +
                ", highwayId=" + highwayId +
                ", distanceTraveled=" + distanceTraveled + " km" +
                ", entryTimestamp=" + entryTimestamp +
                ", exitTimestamp=" + exitTimestamp +
                ", entryLatitude=" + entryLatitude +
                ", entryLongitude=" + entryLongitude +
                ", exitLatitude=" + exitLatitude +
                ", exitLongitude=" + exitLongitude +
                ", active=" + isActive() +
                '}';
    }
}
