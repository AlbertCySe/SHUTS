package com.highway.tolling.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Vehicle Entity
 * Represents a vehicle registered in the tolling system
 */
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @Column(nullable = false, unique = true, length = 20)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleType vehicleType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    // Constructors
    public Vehicle() {
        this.registeredAt = LocalDateTime.now();
    }

    public Vehicle(String vehicleNumber, VehicleType vehicleType, User user) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.user = user;
        this.registeredAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", vehicleType=" + vehicleType +
                ", user=" + (user != null ? user.getUserId() : null) +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
