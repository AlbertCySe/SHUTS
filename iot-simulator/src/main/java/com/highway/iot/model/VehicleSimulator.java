package com.highway.iot.model;

import com.highway.iot.service.SimulatorSettingsService;

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
    private final SimulatorSettingsService settingsService;
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

    public VehicleSimulator(int vehicleId, String routeName, SimulatorSettingsService settingsService) {
        this.vehicleId = vehicleId;
        this.routeName = routeName;
        this.settingsService = settingsService;
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
        SimulatorSettings.DrivingBehaviorSettings settings = settingsService.getSettings().getDrivingBehavior();
        double driftSpeedKmH = randomDouble(settings.getWanderSpeedMinKmH(), settings.getWanderSpeedMaxKmH());
        double metersPerTick = driftSpeedKmH * (1000.0 / 3600.0) / 2.0;
        // 1 degree lat ≈ 111km => meters to degrees
        double degPerTick = metersPerTick / 111000.0;
        // Random direction drift
        double angle = Math.random() * 2 * Math.PI;
        wanderLatOffset += degPerTick * Math.cos(angle);
        wanderLngOffset += degPerTick * Math.sin(angle);
        double maxOffset = settings.getWanderMaxOffsetDegrees();
        wanderLatOffset = Math.max(-maxOffset, Math.min(maxOffset, wanderLatOffset));
        wanderLngOffset = Math.max(-maxOffset, Math.min(maxOffset, wanderLngOffset));
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
        SimulatorSettings.DrivingBehaviorSettings settings = settingsService.getSettings().getDrivingBehavior();
        double rand = Math.random();
        if (travelMode == TravelMode.HIGHWAY) {
            if (rand < settings.getWanderOffRouteProbability()) {
                travelMode = TravelMode.WANDERING;
                travelModeTicksRemaining = randomInt(settings.getWanderTicksMin(), settings.getWanderTicksMax());
                wanderLatOffset = 0;
                wanderLngOffset = 0;
            } else {
                travelModeTicksRemaining = randomInt(settings.getHighwayStayTicksMin(), settings.getHighwayStayTicksMax());
            }
        } else { // WANDERING → return to highway
            travelMode = TravelMode.HIGHWAY;
            travelModeTicksRemaining = randomInt(settings.getReturnHighwayTicksMin(), settings.getReturnHighwayTicksMax());
        }
    }

    private void updateSimulationState() {
        SimulatorSettings.SpeedStatusSettings settings = settingsService.getSettings().getSpeedStatus();
        if (stateTicksRemaining > 0) {
            stateTicksRemaining--;
            if (currentState == SimulationState.DRIVING) {
                currentSpeedKmH += (Math.random() * 2 - 1);
                currentSpeedKmH = clamp(currentSpeedKmH, settings.getDrivingSpeedMinKmH(), settings.getDrivingSpeedMaxKmH());
            } else if (currentState == SimulationState.TRAFFIC) {
                currentSpeedKmH += (Math.random() * 4 - 2);
                currentSpeedKmH = clamp(currentSpeedKmH, settings.getTrafficSpeedMinKmH(), settings.getTrafficSpeedMaxKmH());
            }
            return;
        }

        double rand = Math.random();
        if (rand < settings.getDrivingProbability()) {
            currentState = SimulationState.DRIVING;
            currentSpeedKmH = randomDouble(settings.getDrivingStartSpeedMinKmH(), settings.getDrivingStartSpeedMaxKmH());
            stateTicksRemaining = randomInt(settings.getDrivingStateTicksMin(), settings.getDrivingStateTicksMax());
        } else if (rand < settings.getDrivingProbability() + settings.getTrafficProbability()) {
            currentState = SimulationState.TRAFFIC;
            currentSpeedKmH = randomDouble(settings.getTrafficStartSpeedMinKmH(), settings.getTrafficStartSpeedMaxKmH());
            stateTicksRemaining = randomInt(settings.getTrafficStateTicksMin(), settings.getTrafficStateTicksMax());
        } else {
            currentState = SimulationState.STOPPED;
            currentSpeedKmH = settings.getStoppedSpeedKmH();
            stateTicksRemaining = randomInt(settings.getStoppedStateTicksMin(), settings.getStoppedStateTicksMax());
        }
    }

    private int randomInt(int minInclusive, int maxExclusive) {
        if (maxExclusive <= minInclusive) return minInclusive;
        return minInclusive + (int) (Math.random() * (maxExclusive - minInclusive));
    }

    private double randomDouble(double minInclusive, double maxExclusive) {
        if (maxExclusive <= minInclusive) return minInclusive;
        return minInclusive + (Math.random() * (maxExclusive - minInclusive));
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
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
