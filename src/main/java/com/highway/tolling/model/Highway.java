package com.highway.tolling.model;

import jakarta.persistence.*;

/**
 * Highway Entity
 * Represents a national highway with toll rate details
 */
@Entity
@Table(name = "highways")
public class Highway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long highwayId;

    @Column(nullable = false, length = 100)
    private String highwayName;

    @Column(nullable = false)
    private Double startLatitude;

    @Column(nullable = false)
    private Double startLongitude;

    @Column(nullable = false)
    private Double endLatitude;

    @Column(nullable = false)
    private Double endLongitude;

    @Column(nullable = false)
    private Double ratePerKmForCar;

    @Column(nullable = false)
    private Double ratePerKmForBike;

    @Column(nullable = false)
    private Double ratePerKmForTruck;

    // Constructors
    public Highway() {
    }

    public Highway(String highwayName, Double startLatitude, Double startLongitude,
            Double endLatitude, Double endLongitude, Double ratePerKmForCar,
            Double ratePerKmForBike, Double ratePerKmForTruck) {
        this.highwayName = highwayName;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.ratePerKmForCar = ratePerKmForCar;
        this.ratePerKmForBike = ratePerKmForBike;
        this.ratePerKmForTruck = ratePerKmForTruck;
    }

    // Getters and Setters
    public Long getHighwayId() {
        return highwayId;
    }

    public void setHighwayId(Long highwayId) {
        this.highwayId = highwayId;
    }

    public String getHighwayName() {
        return highwayName;
    }

    public void setHighwayName(String highwayName) {
        this.highwayName = highwayName;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(Double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public Double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(Double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public Double getRatePerKmForCar() {
        return ratePerKmForCar;
    }

    public void setRatePerKmForCar(Double ratePerKmForCar) {
        this.ratePerKmForCar = ratePerKmForCar;
    }

    public Double getRatePerKmForBike() {
        return ratePerKmForBike;
    }

    public void setRatePerKmForBike(Double ratePerKmForBike) {
        this.ratePerKmForBike = ratePerKmForBike;
    }

    public Double getRatePerKmForTruck() {
        return ratePerKmForTruck;
    }

    public void setRatePerKmForTruck(Double ratePerKmForTruck) {
        this.ratePerKmForTruck = ratePerKmForTruck;
    }

    @Override
    public String toString() {
        return "Highway{" +
                "highwayId=" + highwayId +
                ", highwayName='" + highwayName + '\'' +
                ", startLatitude=" + startLatitude +
                ", startLongitude=" + startLongitude +
                ", endLatitude=" + endLatitude +
                ", endLongitude=" + endLongitude +
                ", ratePerKmForCar=" + ratePerKmForCar +
                ", ratePerKmForBike=" + ratePerKmForBike +
                ", ratePerKmForTruck=" + ratePerKmForTruck +
                '}';
    }
}
