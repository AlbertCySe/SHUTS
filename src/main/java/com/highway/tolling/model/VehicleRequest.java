package com.highway.tolling.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * VehicleRequest Entity
 * Stores user-submitted vehicle action requests for admin review.
 */
@Entity
@Table(name = "vehicle_requests")
public class VehicleRequest {

    public enum RequestType { ADD, DEACTIVATE, SELL, SCRAP, MODIFY }
    public enum RequestStatus { PENDING, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;                  // Requesting user

    private Long vehicleId;               // Null for ADD requests

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    // ADD / MODIFY fields
    private String requestedVehicleNumber;
    private String requestedVehicleType;  // CAR / BIKE / TRUCK

    // SELL field — target owner
    private Long newOwnerUserId;

    // Common
    @Column(length = 500)
    private String reason;

    @Column(length = 500)
    private String adminNotes;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public RequestType getRequestType() { return requestType; }
    public void setRequestType(RequestType requestType) { this.requestType = requestType; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public String getRequestedVehicleNumber() { return requestedVehicleNumber; }
    public void setRequestedVehicleNumber(String v) { this.requestedVehicleNumber = v; }

    public String getRequestedVehicleType() { return requestedVehicleType; }
    public void setRequestedVehicleType(String v) { this.requestedVehicleType = v; }

    public Long getNewOwnerUserId() { return newOwnerUserId; }
    public void setNewOwnerUserId(Long newOwnerUserId) { this.newOwnerUserId = newOwnerUserId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
