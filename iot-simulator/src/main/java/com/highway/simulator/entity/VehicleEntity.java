package com.highway.simulator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulated_vehicles")
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "core_vehicle_id", unique = true, nullable = false)
    private Long coreVehicleId;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "current_status")
    private String currentStatus; // RUNNING, STOPPED_FOR_BREAK, PARKED

    @Column(name = "last_active_timestamp")
    private LocalDateTime lastActiveTimestamp;

    // Constructors
    public VehicleEntity() {
    }

    public VehicleEntity(Long coreVehicleId, String currentStatus) {
        this.coreVehicleId = coreVehicleId;
        this.currentStatus = currentStatus;
        this.lastActiveTimestamp = LocalDateTime.now();
    }

    public VehicleEntity(Long coreVehicleId, String vehicleNumber, String vehicleType, String ownerName, String currentStatus) {
        this.coreVehicleId = coreVehicleId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.ownerName = ownerName;
        this.currentStatus = currentStatus;
        this.lastActiveTimestamp = LocalDateTime.now();
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

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public LocalDateTime getLastActiveTimestamp() {
        return lastActiveTimestamp;
    }

    public void setLastActiveTimestamp(LocalDateTime lastActiveTimestamp) {
        this.lastActiveTimestamp = lastActiveTimestamp;
    }
}

