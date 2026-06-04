package com.highway.tolling.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Bill Entity
 * Represents a monthly toll bill for a user
 */
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @Column(nullable = false)
    private Long userId;

    @Column
    private Long vehicleId; // Optional: specified if bill is for a single vehicle

    @Column(nullable = false)
    private Double totalDistance;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false, length = 7)
    private String billMonth; // Format: "2026-01" (YYYY-MM)

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean autoDeductAttempted = false;

    // Constructors
    public Bill() {
        this.totalDistance = 0.0;
        this.totalAmount = 0.0;
        this.status = BillStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.autoDeductAttempted = false;
    }

    public Bill(Long userId, Double totalDistance, Double totalAmount,
            String billMonth, LocalDate dueDate) {
        this.userId = userId;
        this.totalDistance = totalDistance;
        this.totalAmount = totalAmount;
        this.billMonth = billMonth;
        this.dueDate = dueDate;
        this.status = BillStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.autoDeductAttempted = false;
    }

    public Bill(Long userId, Long vehicleId, Double totalDistance, Double totalAmount,
            String billMonth, LocalDate dueDate) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.totalDistance = totalDistance;
        this.totalAmount = totalAmount;
        this.billMonth = billMonth;
        this.dueDate = dueDate;
        this.status = BillStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.autoDeductAttempted = false;
    }

    // Getters and Setters
    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBillMonth() {
        return billMonth;
    }

    public void setBillMonth(String billMonth) {
        this.billMonth = billMonth;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAutoDeductAttempted() {
        return autoDeductAttempted;
    }

    public void setAutoDeductAttempted(boolean autoDeductAttempted) {
        this.autoDeductAttempted = autoDeductAttempted;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", userId=" + userId +
                ", totalDistance=" + totalDistance + " km" +
                ", totalAmount=₹" + totalAmount +
                ", billMonth='" + billMonth + '\'' +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
