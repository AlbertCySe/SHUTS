package com.highway.tolling.service;

import com.highway.tolling.model.Highway;
import com.highway.tolling.model.VehicleType;
import org.springframework.stereotype.Service;

/**
 * Toll Calculation Service
 * Calculates toll amount based on vehicle type and distance traveled
 */
@Service
public class TollCalculationService {

    /**
     * Calculate toll amount based on vehicle type and distance
     * 
     * @param vehicleType       The type of vehicle (CAR, BIKE, BUS, TRUCK)
     * @param distanceKm        The distance traveled in kilometers
     * @param ratePerKmForCar   Rate per km for cars
     * @param ratePerKmForBike  Rate per km for bikes
     * @param ratePerKmForTruck Rate per km for trucks/buses
     * @return Total toll amount
     */
    public double calculateToll(VehicleType vehicleType, double distanceKm,
            double ratePerKmForCar, double ratePerKmForBike,
            double ratePerKmForTruck) {

        double ratePerKm;

        // Determine rate based on vehicle type using switch-case
        switch (vehicleType) {
            case CAR:
                ratePerKm = ratePerKmForCar;
                break;
            case BIKE:
                ratePerKm = ratePerKmForBike;
                break;
            case BUS:
            case TRUCK:
                ratePerKm = ratePerKmForTruck;
                break;
            default:
                throw new IllegalArgumentException("Invalid vehicle type: " + vehicleType);
        }

        // Calculate total toll: distance × rate per km
        double totalToll = distanceKm * ratePerKm;

        return totalToll;
    }

    /**
     * Calculate toll amount using highway's toll rates
     * 
     * @param vehicleType The type of vehicle
     * @param distanceKm  The distance traveled in kilometers
     * @param highway     The highway with toll rate information
     * @return Total toll amount
     */
    public double calculateToll(VehicleType vehicleType, double distanceKm, Highway highway) {
        return calculateToll(
                vehicleType,
                distanceKm,
                highway.getRatePerKmForCar(),
                highway.getRatePerKmForBike(),
                highway.getRatePerKmForTruck());
    }

    /**
     * Calculate toll amount and round to 2 decimal places
     * 
     * @param vehicleType The type of vehicle
     * @param distanceKm  The distance traveled in kilometers
     * @param highway     The highway with toll rate information
     * @return Total toll amount rounded to 2 decimal places
     */
    public double calculateTollRounded(VehicleType vehicleType, double distanceKm, Highway highway) {
        double toll = calculateToll(vehicleType, distanceKm, highway);
        return Math.round(toll * 100.0) / 100.0;
    }

    /**
     * Get detailed toll calculation breakdown
     * 
     * @param vehicleType The type of vehicle
     * @param distanceKm  The distance traveled in kilometers
     * @param highway     The highway with toll rate information
     * @return TollCalculationResult with detailed breakdown
     */
    public TollCalculationResult calculateTollWithDetails(VehicleType vehicleType,
            double distanceKm, Highway highway) {
        double ratePerKm = getRateForVehicleType(vehicleType, highway);
        double totalToll = distanceKm * ratePerKm;

        return new TollCalculationResult(
                vehicleType,
                distanceKm,
                ratePerKm,
                totalToll,
                highway.getHighwayName());
    }

    /**
     * Get the rate per km for a specific vehicle type
     * 
     * @param vehicleType The type of vehicle
     * @param highway     The highway with toll rate information
     * @return Rate per km for the vehicle type
     */
    private double getRateForVehicleType(VehicleType vehicleType, Highway highway) {
        // Using if-else logic as an alternative to switch-case
        if (vehicleType == VehicleType.CAR) {
            return highway.getRatePerKmForCar();
        } else if (vehicleType == VehicleType.BIKE) {
            return highway.getRatePerKmForBike();
        } else if (vehicleType == VehicleType.BUS || vehicleType == VehicleType.TRUCK) {
            return highway.getRatePerKmForTruck();
        } else {
            throw new IllegalArgumentException("Invalid vehicle type: " + vehicleType);
        }
    }

    /**
     * Inner class to hold toll calculation results
     */
    public static class TollCalculationResult {
        private VehicleType vehicleType;
        private double distanceKm;
        private double ratePerKm;
        private double totalToll;
        private String highwayName;

        public TollCalculationResult(VehicleType vehicleType, double distanceKm,
                double ratePerKm, double totalToll, String highwayName) {
            this.vehicleType = vehicleType;
            this.distanceKm = distanceKm;
            this.ratePerKm = ratePerKm;
            this.totalToll = totalToll;
            this.highwayName = highwayName;
        }

        // Getters
        public VehicleType getVehicleType() {
            return vehicleType;
        }

        public double getDistanceKm() {
            return distanceKm;
        }

        public double getRatePerKm() {
            return ratePerKm;
        }

        public double getTotalToll() {
            return totalToll;
        }

        public String getHighwayName() {
            return highwayName;
        }

        @Override
        public String toString() {
            return "TollCalculationResult{" +
                    "vehicleType=" + vehicleType +
                    ", distanceKm=" + distanceKm + " km" +
                    ", ratePerKm=₹" + ratePerKm + "/km" +
                    ", totalToll=₹" + totalToll +
                    ", highwayName='" + highwayName + '\'' +
                    '}';
        }
    }
}
