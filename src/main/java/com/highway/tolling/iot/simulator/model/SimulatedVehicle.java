package com.highway.tolling.iot.simulator.model;

/**
 * Simulated Vehicle
 */
public class SimulatedVehicle {
    private Long vehicleId;
    private GPSPoint currentPosition;
    private double currentSpeedKmh;
    private VehicleState state;
    private Route assignedRoute;

    public SimulatedVehicle(Long vehicleId, Route route) {
        this.vehicleId = vehicleId;
        this.assignedRoute = route;
        this.currentPosition = (route != null) ? route.getStartPoint() : null;
        this.currentSpeedKmh = 0.0;
        this.state = VehicleState.STOPPED;
    }

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public GPSPoint getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(GPSPoint currentPosition) {
        this.currentPosition = currentPosition;
    }

    public double getCurrentSpeedKmh() {
        return currentSpeedKmh;
    }

    public void setCurrentSpeedKmh(double currentSpeedKmh) {
        this.currentSpeedKmh = currentSpeedKmh;
    }

    public VehicleState getState() {
        return state;
    }

    public void setState(VehicleState state) {
        this.state = state;
    }

    public Route getAssignedRoute() {
        return assignedRoute;
    }

    public void setAssignedRoute(Route assignedRoute) {
        this.assignedRoute = assignedRoute;
    }

    @Override
    public String toString() {
        return String.format("Vehicle %d: %s at %s, Speed: %.1f km/h",
                vehicleId, state, currentPosition, currentSpeedKmh);
    }
}
