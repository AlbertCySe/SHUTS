package com.highway.tolling.dto;

import java.time.LocalDateTime;

/**
 * IoT Data Response DTO
 * Standardized response for IoT data submission.
 */
public class IoTDataResponse {

    private boolean success;
    private String message;
    private Long locationId;
    private LocalDateTime processedTimestamp;

    // Default Constructor
    public IoTDataResponse() {
    }

    // Success Constructor
    public IoTDataResponse(boolean success, String message, Long locationId) {
        this.success = success;
        this.message = message;
        this.locationId = locationId;
        this.processedTimestamp = LocalDateTime.now();
    }

    // Error Constructor (no locationId)
    public IoTDataResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.processedTimestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public LocalDateTime getProcessedTimestamp() {
        return processedTimestamp;
    }

    public void setProcessedTimestamp(LocalDateTime processedTimestamp) {
        this.processedTimestamp = processedTimestamp;
    }

    @Override
    public String toString() {
        return "IoTDataResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", locationId=" + locationId +
                ", processedTimestamp=" + processedTimestamp +
                '}';
    }
}
