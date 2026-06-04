package com.highway.tolling.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Wallet Recharge Request Entity
 * Represents a user's request to top-up their wallet via external payment
 */
@Entity
@Table(name = "wallet_recharge_requests")
public class WalletRechargeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RechargeStatus status;

    @Column(nullable = false)
    private java.time.LocalDateTime requestDate;

    private java.time.LocalDateTime processedDate;

    // Optional reference number simulation
    private String upiReference;

    public WalletRechargeRequest() {
    }

    public WalletRechargeRequest(User user, Double amount, String upiReference) {
        this.user = user;
        this.amount = amount;
        this.upiReference = upiReference;
        this.status = RechargeStatus.PENDING;
        this.requestDate = LocalDateTime.now();
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public RechargeStatus getStatus() {
        return status;
    }

    public void setStatus(RechargeStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }

    public String getUpiReference() {
        return upiReference;
    }

    public void setUpiReference(String upiReference) {
        this.upiReference = upiReference;
    }
}
