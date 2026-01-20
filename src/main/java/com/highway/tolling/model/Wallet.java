package com.highway.tolling.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Wallet Entity
 * Represents a user's digital wallet for toll payments
 */
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false)
    private Double minimumBalance;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    // Constructors
    public Wallet() {
        this.balance = 0.0;
        this.minimumBalance = 0.0;
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public Wallet(User user, Double balance, Double minimumBalance) {
        this.user = user;
        this.balance = balance;
        this.minimumBalance = minimumBalance;
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
        this.lastUpdated = LocalDateTime.now();
    }

    public Double getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Check if wallet is in deficit (balance below minimum)
     * 
     * @return true if wallet is in deficit, false otherwise
     */
    public boolean isInDeficit() {
        return balance < minimumBalance;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "walletId=" + walletId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", balance=" + balance +
                ", minimumBalance=" + minimumBalance +
                ", isInDeficit=" + isInDeficit() +
                ", createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
