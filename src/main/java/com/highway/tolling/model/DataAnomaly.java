package com.highway.tolling.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * DataAnomaly Entity
 * Tracks detected anomalies in IoT GPS data for review
 */
@Entity
@Table(name = "data_anomalies")
public class DataAnomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AnomalyType anomalyType;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AnomalySeverity severity;

    @Column(nullable = false)
    private LocalDateTime detectedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ReviewStatus reviewStatus;

    @Column(length = 1000)
    private String reviewNotes;

    @Column
    private LocalDateTime reviewedAt;

    @Column
    private Long relatedLocationId; // Link to specific GPS point if applicable

    // Constructors
    public DataAnomaly() {
        this.detectedAt = LocalDateTime.now();
        this.reviewStatus = ReviewStatus.PENDING;
    }

    public DataAnomaly(Long vehicleId, AnomalyType anomalyType, String description, AnomalySeverity severity) {
        this.vehicleId = vehicleId;
        this.anomalyType = anomalyType;
        this.description = description;
        this.severity = severity;
        this.detectedAt = LocalDateTime.now();
        this.reviewStatus = ReviewStatus.PENDING;
    }

    public DataAnomaly(Long vehicleId, AnomalyType anomalyType, String description,
            AnomalySeverity severity, Long relatedLocationId) {
        this(vehicleId, anomalyType, description, severity);
        this.relatedLocationId = relatedLocationId;
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

    public AnomalyType getAnomalyType() {
        return anomalyType;
    }

    public void setAnomalyType(AnomalyType anomalyType) {
        this.anomalyType = anomalyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AnomalySeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AnomalySeverity severity) {
        this.severity = severity;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Long getRelatedLocationId() {
        return relatedLocationId;
    }

    public void setRelatedLocationId(Long relatedLocationId) {
        this.relatedLocationId = relatedLocationId;
    }

    @Override
    public String toString() {
        return "DataAnomaly{" +
                "id=" + id +
                ", vehicleId=" + vehicleId +
                ", anomalyType=" + anomalyType +
                ", description='" + description + '\'' +
                ", severity=" + severity +
                ", detectedAt=" + detectedAt +
                ", reviewStatus=" + reviewStatus +
                ", reviewedAt=" + reviewedAt +
                '}';
    }
}
