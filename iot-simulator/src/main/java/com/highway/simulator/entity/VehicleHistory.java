package com.highway.simulator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_history")
public class VehicleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "core_vehicle_id", nullable = false)
    private Long coreVehicleId;

    @Column(name = "event_type", nullable = false)
    private String eventType; // REGISTRATION, OWNERSHIP_CHANGE, RED_FLAG, MAINTENANCE

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Constructors
    public VehicleHistory() {
    }

    public VehicleHistory(Long coreVehicleId, String eventType, String description) {
        this.coreVehicleId = coreVehicleId;
        this.eventType = eventType;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCoreVehicleId() {
        return coreVehicleId;
    }

    public void setCoreVehicleId(Long coreVehicleId) {
        this.coreVehicleId = coreVehicleId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
