package com.highway.tolling.dto;

import jakarta.validation.constraints.*;

/**
 * IoT Data Request DTO
 * Represents incoming GPS data from an IoT device.
 */
public class IoTDataRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotNull(message = "Timestamp is required")
    private String timestamp;

    private Boolean isHighway;

    @DecimalMin(value = "0.0", message = "Speed must be zero or greater")
    private Double speedKmH;

    @Size(max = 50, message = "Status must be 50 characters or fewer")
    private String status;

    @Size(max = 150, message = "Route name must be 150 characters or fewer")
    private String routeName;

    // Default Constructor
    public IoTDataRequest() {
    }

    // Parameterized Constructor
    public IoTDataRequest(Long vehicleId, Double latitude, Double longitude, String timestamp) {
        this.vehicleId = vehicleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getIsHighway() {
        return isHighway;
    }

    public void setIsHighway(Boolean isHighway) {
        this.isHighway = isHighway;
    }

    public Double getSpeedKmH() {
        return speedKmH;
    }

    public void setSpeedKmH(Double speedKmH) {
        this.speedKmH = speedKmH;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public String toString() {
        return "IoTDataRequest{" +
                "vehicleId=" + vehicleId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp='" + timestamp + '\'' +
                ", isHighway=" + isHighway +
                ", speedKmH=" + speedKmH +
                ", status='" + status + '\'' +
                ", routeName='" + routeName + '\'' +
                '}';
    }
}
