package com.highway.tolling.controller;

import com.highway.tolling.model.DataAnomaly;
import com.highway.tolling.model.ReviewStatus;
import com.highway.tolling.service.AnomalyDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Anomaly Review Controller
 * REST endpoints for reviewing flagged IoT data anomalies
 */
@RestController
@RequestMapping("/api/anomalies")
public class AnomalyReviewController {

    private final AnomalyDetectionService anomalyDetectionService;

    @Autowired
    public AnomalyReviewController(AnomalyDetectionService anomalyDetectionService) {
        this.anomalyDetectionService = anomalyDetectionService;
    }

    /**
     * Get all pending anomalies
     * GET /api/anomalies/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingAnomalies() {
        try {
            List<DataAnomaly> anomalies = anomalyDetectionService.getPendingAnomalies();
            return ResponseEntity.ok(anomalies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving anomalies: " + e.getMessage());
        }
    }

    /**
     * Get anomalies for a specific vehicle
     * GET /api/anomalies/vehicle/{vehicleId}
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<?> getVehicleAnomalies(@PathVariable Long vehicleId) {
        try {
            List<DataAnomaly> anomalies = anomalyDetectionService.getVehicleAnomalies(vehicleId);
            return ResponseEntity.ok(anomalies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving anomalies: " + e.getMessage());
        }
    }

    /**
     * Mark anomaly as reviewed
     * POST /api/anomalies/{id}/review
     * 
     * Body:
     * {
     * "notes": "Reviewed - false positive, GPS device malfunction",
     * "status": "RESOLVED"
     * }
     */
    @PostMapping("/{id}/review")
    public ResponseEntity<?> reviewAnomaly(
            @PathVariable Long id,
            @RequestBody ReviewRequest request) {
        try {
            if (request.getNotes() == null || request.getNotes().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Review notes are required");
            }

            ReviewStatus newStatus = request.getStatus() != null ? request.getStatus() : ReviewStatus.REVIEWED;

            DataAnomaly updatedAnomaly = anomalyDetectionService.markAsReviewed(
                    id, request.getNotes(), newStatus);

            return ResponseEntity.ok(updatedAnomaly);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reviewing anomaly: " + e.getMessage());
        }
    }

    /**
     * Manually trigger missing data check for a vehicle
     * POST /api/anomalies/check-missing/{vehicleId}
     */
    @PostMapping("/check-missing/{vehicleId}")
    public ResponseEntity<?> checkMissingData(@PathVariable Long vehicleId) {
        try {
            anomalyDetectionService.detectMissingData(vehicleId);
            return ResponseEntity.ok(Map.of(
                    "message", "Missing data check completed",
                    "vehicleId", vehicleId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking for missing data: " + e.getMessage());
        }
    }

    /**
     * Inner class for review request body
     */
    public static class ReviewRequest {
        private String notes;
        private ReviewStatus status;

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public ReviewStatus getStatus() {
            return status;
        }

        public void setStatus(ReviewStatus status) {
            this.status = status;
        }
    }
}
