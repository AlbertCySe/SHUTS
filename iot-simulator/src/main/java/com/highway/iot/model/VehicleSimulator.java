package com.highway.iot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents one simulated IoT vehicle with its own route, position, and driving state.
 */
public class VehicleSimulator {

    public enum SimulationState { DRIVING, TRAFFIC, STOPPED }
    public enum TravelMode      { HIGHWAY, WANDERING }

    private final int vehicleId;
    private final String routeName;
    private List<double[]> detailedRoute = new ArrayList<>();
    private AtomicInteger currentWaypointIndex = new AtomicInteger(0);
    private double progressToNext = 0.0;
    private boolean routeReady = false;

    private double currentLat;
    private double currentLng;
    private double currentSpeedKmH = 0.0;
    private int stateTicksRemaining = 0;
    private SimulationState currentState = SimulationState.DRIVING;

    // Travel mode: HIGHWAY = follow OSRM route, WANDERING = drift on local roads
    private TravelMode travelMode = TravelMode.HIGHWAY;
    private int travelModeTicksRemaining = 0;
    // Wander drift: accumulated offset from route anchor
    private double wanderLatOffset = 0.0;
    private double wanderLngOffset = 0.0;

    public VehicleSimulator(int vehicleId, String routeName) {
        this.vehicleId = vehicleId;
        this.routeName = routeName;
    }

    // --- Core Movement Logic (called every 500ms) ---
    public void tick() {
        if (!routeReady || detailedRoute.size() < 2) return;

        // Update travel mode
        updateTravelMode();

        if (travelMode == TravelMode.WANDERING) {
            tickWander();
            return;
        }

        // ── HIGHWAY mode: follow OSRM route ──
        int idx = currentWaypointIndex.get();
        if (idx >= detailedRoute.size() - 1) {
            currentWaypointIndex.set(0);
            progressToNext = 0;
            wanderLatOffset = 0;
            wanderLngOffset = 0;
            return;
        }

        updateSimulationState();

        double[] startPoint = detailedRoute.get(idx);
        double[] endPoint = detailedRoute.get(idx + 1);

        double segmentLengthMeters = calculateDistanceMeters(
                startPoint[0], startPoint[1], endPoint[0], endPoint[1]);

        if (segmentLengthMeters < 0.5) {
            progressToNext = 1.0;
        } else {
            double metersPerTick = currentSpeedKmH * (1000.0 / 3600.0) / 2.0;
            double progressIncrement = metersPerTick / segmentLengthMeters;
            progressToNext += progressIncrement;
        }

        if (progressToNext >= 1.0) {
            currentWaypointIndex.incrementAndGet();
            progressToNext = 0.0;
            if (currentWaypointIndex.get() >= detailedRoute.size() - 1) {
                currentLat = detailedRoute.get(detailedRoute.size() - 1)[0];
                currentLng = detailedRoute.get(detailedRoute.size() - 1)[1];
            } else {
                currentLat = endPoint[0];
                currentLng = endPoint[1];
            }
        } else {
            currentLat = startPoint[0] + (endPoint[0] - startPoint[0]) * progressToNext;
            currentLng = startPoint[1] + (endPoint[1] - startPoint[1]) * progressToNext;
        }
    }

    /** Wander mode: drift with small random offsets on local roads */
    private void tickWander() {
        double driftSpeedKmH = 20.0 + Math.random() * 20; // 20–40 km/h on local roads
        double metersPerTick = driftSpeedKmH * (1000.0 / 3600.0) / 2.0;
        // 1 degree lat ≈ 111km => meters to degrees
        double degPerTick = metersPerTick / 111000.0;
        // Random direction drift
        double angle = Math.random() * 2 * Math.PI;
        wanderLatOffset += degPerTick * Math.cos(angle);
        wanderLngOffset += degPerTick * Math.sin(angle);
        // Cap max wander drift to 0.04° ≈ 4.4km off route
        wanderLatOffset = Math.max(-0.04, Math.min(0.04, wanderLatOffset));
        wanderLngOffset = Math.max(-0.04, Math.min(0.04, wanderLngOffset));
        // Apply to anchor (current route point)
        int idx = Math.min(currentWaypointIndex.get(), detailedRoute.size() - 1);
        double[] anchor = detailedRoute.get(idx);
        currentLat = anchor[0] + wanderLatOffset;
        currentLng = anchor[1] + wanderLngOffset;
        currentSpeedKmH = driftSpeedKmH;
    }

    private void updateTravelMode() {
        if (travelModeTicksRemaining > 0) {
            travelModeTicksRemaining--;
            return;
        }
        // At expiry of travel mode, decide next mode
        double rand = Math.random();
        if (travelMode == TravelMode.HIGHWAY) {
            if (rand < 0.20) { // 20% chance to wander off
                travelMode = TravelMode.WANDERING;
                travelModeTicksRemaining = 40 + (int)(Math.random() * 80); // 20-60 seconds
                wanderLatOffset = 0;
                wanderLngOffset = 0;
            } else {
                travelModeTicksRemaining = 60 + (int)(Math.random() * 120); // stay on highway
            }
        } else { // WANDERING → return to highway
            travelMode = TravelMode.HIGHWAY;
            travelModeTicksRemaining = 80 + (int)(Math.random() * 160); // 40-120s on highway
        }
    }

    private void updateSimulationState() {
        if (stateTicksRemaining > 0) {
            stateTicksRemaining--;
            if (currentState == SimulationState.DRIVING) {
                currentSpeedKmH += (Math.random() * 2 - 1);
                currentSpeedKmH = Math.max(40.0, Math.min(80.0, currentSpeedKmH));
            } else if (currentState == SimulationState.TRAFFIC) {
                currentSpeedKmH += (Math.random() * 4 - 2);
                currentSpeedKmH = Math.max(5.0, Math.min(30.0, currentSpeedKmH));
            }
            return;
        }

        double rand = Math.random();
        if (rand < 0.80) {
            currentState = SimulationState.DRIVING;
            currentSpeedKmH = 60.0 + (Math.random() * 10);
            stateTicksRemaining = 40 + (int)(Math.random() * 80);
        } else if (rand < 0.95) {
            currentState = SimulationState.TRAFFIC;
            currentSpeedKmH = 15.0 + (Math.random() * 10);
            stateTicksRemaining = 20 + (int)(Math.random() * 30);
        } else {
            currentState = SimulationState.STOPPED;
            currentSpeedKmH = 0.0;
            stateTicksRemaining = 10 + (int)(Math.random() * 20);
        }
    }

    private double calculateDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;
    }

    // --- State Persistence ---

    /** Restore state from a previously saved snapshot */
    public void restoreState(int waypointIndex, double progress) {
        if (waypointIndex < detailedRoute.size()) {
            currentWaypointIndex.set(waypointIndex);
            progressToNext = progress;
            // Also update lat/lng to match
            if (waypointIndex < detailedRoute.size() - 1) {
                double[] s = detailedRoute.get(waypointIndex);
                double[] e = detailedRoute.get(waypointIndex + 1);
                currentLat = s[0] + (e[0] - s[0]) * progress;
                currentLng = s[1] + (e[1] - s[1]) * progress;
            } else {
                currentLat = detailedRoute.get(detailedRoute.size() - 1)[0];
                currentLng = detailedRoute.get(detailedRoute.size() - 1)[1];
            }
            System.out.println("  Vehicle " + vehicleId + " restored to waypoint " + waypointIndex 
                    + " (" + String.format("%.4f", currentLat) + ", " + String.format("%.4f", currentLng) + ")");
        }
    }

    // --- Getters & Setters ---

    public int getVehicleId() { return vehicleId; }
    public String getRouteName() { return routeName; }
    public double getCurrentLat() { return currentLat; }
    public double getCurrentLng() { return currentLng; }
    public double getCurrentSpeedKmH() { return Math.round(currentSpeedKmH * 10.0) / 10.0; }
    public String getCurrentState() { return currentState.name(); }
    public String getTravelMode() { return travelMode.name(); }
    public int getCurrentWaypointIndex() { return currentWaypointIndex.get(); }
    public double getProgressToNext() { return progressToNext; }
    public boolean isRouteReady() { return routeReady; }

    public List<double[]> getDetailedRoute() { return detailedRoute; }
    public void setDetailedRoute(List<double[]> route) {
        this.detailedRoute = route;
        if (!route.isEmpty()) {
            currentLat = route.get(0)[0];
            currentLng = route.get(0)[1];
            routeReady = true;
        }
    }
}
