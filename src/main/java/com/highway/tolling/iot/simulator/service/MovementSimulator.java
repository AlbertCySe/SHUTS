package com.highway.tolling.iot.simulator.service;

import com.highway.tolling.iot.simulator.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Movement Simulator Service
 * Simulates realistic vehicle movement
 */
@Service
public class MovementSimulator {

    private final GPSGenerator gpsGenerator;
    private final BackendClient backendClient;

    @Value("${simulation.gps.interval.seconds:30}")
    private int gpsIntervalSeconds;

    @Value("${simulation.speed.highway.kmh:80}")
    private double highwaySpeedKmh;

    @Value("${simulation.stoppage.probability:0.1}")
    private double stoppageProbability;

    private final Random random = new Random();

    @Autowired
    public MovementSimulator(GPSGenerator gpsGenerator, BackendClient backendClient) {
        this.gpsGenerator = gpsGenerator;
        this.backendClient = backendClient;
    }

    /**
     * Simulate vehicle movement along a route
     */
    public void simulateVehicle(SimulatedVehicle vehicle) {
        System.out.printf("%n=== Starting simulation for Vehicle %d ===%n", vehicle.getVehicleId());
        System.out.printf("Route: %s%n", vehicle.getAssignedRoute().getName());
        System.out.printf("Starting position: %s%n%n", vehicle.getCurrentPosition());

        Route route = vehicle.getAssignedRoute();
        GPSPoint target = route.getNextWaypoint();

        while (target != null && !Thread.currentThread().isInterrupted()) {
            try {
                // Update vehicle state based on movement
                updateVehicleState(vehicle, target);

                // Generate and send GPS data
                processGPSPoint(vehicle);

                // Check if reached current waypoint
                if (gpsGenerator.hasReachedTarget(vehicle.getCurrentPosition(), target)) {
                    System.out.printf("[Vehicle %d] Reached waypoint: %s%n",
                            vehicle.getVehicleId(), target);
                    target = route.getNextWaypoint();
                }

                // Random stoppage simulation
                if (shouldSimulateStoppage() && vehicle.getState() == VehicleState.CRUISING) {
                    simulateStoppage(vehicle);
                }

                // Wait for next GPS interval
                Thread.sleep(gpsIntervalSeconds * 1000L);

            } catch (InterruptedException e) {
                System.out.printf("[Vehicle %d] Simulation interrupted%n", vehicle.getVehicleId());
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.printf("%n=== Vehicle %d completed route ===%n%n", vehicle.getVehicleId());
    }

    /**
     * Update vehicle state based on current situation
     */
    private void updateVehicleState(SimulatedVehicle vehicle, GPSPoint target) {
        double distanceToTarget = gpsGenerator.calculateDistance(vehicle.getCurrentPosition(), target);

        switch (vehicle.getState()) {
            case STOPPED:
                vehicle.setState(VehicleState.ACCELERATING);
                vehicle.setCurrentSpeedKmh(20); // Start at 20 km/h
                break;

            case ACCELERATING:
                vehicle.setCurrentSpeedKmh(Math.min(vehicle.getCurrentSpeedKmh() + 10, highwaySpeedKmh));
                if (vehicle.getCurrentSpeedKmh() >= highwaySpeedKmh) {
                    vehicle.setState(VehicleState.CRUISING);
                }
                break;

            case CRUISING:
                // Check if need to slow down near target
                if (distanceToTarget < 0.5) { // Within 500m
                    vehicle.setState(VehicleState.DECELERATING);
                }
                break;

            case DECELERATING:
                vehicle.setCurrentSpeedKmh(Math.max(vehicle.getCurrentSpeedKmh() - 10, 20));
                if (distanceToTarget < 0.05) { // Within 50m
                    vehicle.setState(VehicleState.STOPPED);
                    vehicle.setCurrentSpeedKmh(0);
                }
                break;
        }
    }

    /**
     * Process current GPS point (generate, update position, send to backend)
     */
    private void processGPSPoint(SimulatedVehicle vehicle) {
        GPSPoint currentPosition = vehicle.getCurrentPosition();
        Route route = vehicle.getAssignedRoute();

        // Get next target waypoint
        GPSPoint target = route.getWaypoints().get(
                Math.min(route.getWaypoints().indexOf(currentPosition) + 1,
                        route.getWaypoints().size() - 1));

        // Generate next GPS point
        GPSPoint nextPoint;
        if (vehicle.getState() == VehicleState.STOPPED) {
            // Stay at same position
            nextPoint = new GPSPoint(currentPosition.getLatitude(), currentPosition.getLongitude());
        } else {
            // Move towards target
            nextPoint = gpsGenerator.moveTowards(
                    currentPosition,
                    target,
                    vehicle.getCurrentSpeedKmh(),
                    gpsIntervalSeconds);
        }

        // Update vehicle position
        vehicle.setCurrentPosition(nextPoint);

        // Send to backend
        backendClient.sendGPSData(
                vehicle.getVehicleId(),
                nextPoint.getLatitude(),
                nextPoint.getLongitude(),
                nextPoint.getTimestamp());
    }

    /**
     * Simulate vehicle stoppage
     */
    private void simulateStoppage(SimulatedVehicle vehicle) {
        try {
            int stopDurationSeconds = 60 + random.nextInt(240); // 1-5 minutes
            System.out.printf("[Vehicle %d] STOPPAGE for %d seconds at %s%n",
                    vehicle.getVehicleId(), stopDurationSeconds, vehicle.getCurrentPosition());

            vehicle.setState(VehicleState.STOPPED);
            vehicle.setCurrentSpeedKmh(0);

            Thread.sleep(stopDurationSeconds * 1000L);

            System.out.printf("[Vehicle %d] Resuming movement%n", vehicle.getVehicleId());
            vehicle.setState(VehicleState.ACCELERATING);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Determine if stoppage should occur
     */
    private boolean shouldSimulateStoppage() {
        return random.nextDouble() < stoppageProbability;
    }
}
