package com.highway.iot.service;

import com.highway.iot.model.SimulatorSettings;
import org.springframework.stereotype.Service;

@Service
public class SimulatorSettingsService {

    private SimulatorSettings settings = new SimulatorSettings();

    public synchronized SimulatorSettings getSettings() {
        return settings;
    }

    public synchronized SimulatorSettings updateSettings(SimulatorSettings updatedSettings) {
        if (updatedSettings == null) {
            throw new IllegalArgumentException("Settings payload is required.");
        }
        normalize(updatedSettings);
        validate(updatedSettings);
        settings = updatedSettings;
        return settings;
    }

    public synchronized SimulatorSettings resetSettings() {
        settings = new SimulatorSettings();
        return settings;
    }

    private void normalize(SimulatorSettings value) {
        if (value.getLifecycle() == null) value.setLifecycle(new SimulatorSettings.LifecycleSettings());
        if (value.getMovement() == null) value.setMovement(new SimulatorSettings.MovementSettings());
        if (value.getRoute() == null) value.setRoute(new SimulatorSettings.RouteSettings());
        if (value.getDrivingBehavior() == null) value.setDrivingBehavior(new SimulatorSettings.DrivingBehaviorSettings());
        if (value.getSpeedStatus() == null) value.setSpeedStatus(new SimulatorSettings.SpeedStatusSettings());
        if (value.getHighwayDetection() == null) value.setHighwayDetection(new SimulatorSettings.HighwayDetectionSettings());
    }

    private void validate(SimulatorSettings value) {
        SimulatorSettings.LifecycleSettings lifecycle = value.getLifecycle();
        requirePositive(lifecycle.getLifecycleIntervalMs(), "lifecycle.lifecycleIntervalMs");
        requireProbability(lifecycle.getParkedStartProbability(), "lifecycle.parkedStartProbability");
        requireProbability(lifecycle.getRunningPauseProbability(), "lifecycle.runningPauseProbability");
        requireProbability(lifecycle.getBreakResumeProbability(), "lifecycle.breakResumeProbability");

        SimulatorSettings.MovementSettings movement = value.getMovement();
        requirePositive(movement.getMovementTickIntervalMs(), "movement.movementTickIntervalMs");
        requirePositive(movement.getBackendBroadcastIntervalMs(), "movement.backendBroadcastIntervalMs");
        requirePositive(movement.getStateSaveIntervalMs(), "movement.stateSaveIntervalMs");

        SimulatorSettings.RouteSettings route = value.getRoute();
        requirePositive(route.getAvailableRouteCount(), "route.availableRouteCount");
        requireNonNegative(route.getCompletionWaypointOffset(), "route.completionWaypointOffset");
        requireText(route.getRouteSelectionMode(), "route.routeSelectionMode");

        SimulatorSettings.DrivingBehaviorSettings driving = value.getDrivingBehavior();
        requireProbability(driving.getWanderOffRouteProbability(), "drivingBehavior.wanderOffRouteProbability");
        requireRange(driving.getHighwayStayTicksMin(), driving.getHighwayStayTicksMax(), "drivingBehavior.highwayStayTicks");
        requireRange(driving.getWanderTicksMin(), driving.getWanderTicksMax(), "drivingBehavior.wanderTicks");
        requireRange(driving.getReturnHighwayTicksMin(), driving.getReturnHighwayTicksMax(), "drivingBehavior.returnHighwayTicks");
        requireNonNegative(driving.getWanderMaxOffsetDegrees(), "drivingBehavior.wanderMaxOffsetDegrees");
        requireRange(driving.getWanderSpeedMinKmH(), driving.getWanderSpeedMaxKmH(), "drivingBehavior.wanderSpeedKmH");

        SimulatorSettings.SpeedStatusSettings speed = value.getSpeedStatus();
        requireProbability(speed.getDrivingProbability(), "speedStatus.drivingProbability");
        requireProbability(speed.getTrafficProbability(), "speedStatus.trafficProbability");
        requireProbability(speed.getStoppedProbability(), "speedStatus.stoppedProbability");
        double stateTotal = speed.getDrivingProbability() + speed.getTrafficProbability() + speed.getStoppedProbability();
        if (Math.abs(stateTotal - 1.0) > 0.0001) {
            throw new IllegalArgumentException("speedStatus probabilities must add up to 1.0.");
        }
        requireRange(speed.getDrivingSpeedMinKmH(), speed.getDrivingSpeedMaxKmH(), "speedStatus.drivingSpeedKmH");
        requireRange(speed.getDrivingStartSpeedMinKmH(), speed.getDrivingStartSpeedMaxKmH(), "speedStatus.drivingStartSpeedKmH");
        requireRange(speed.getTrafficSpeedMinKmH(), speed.getTrafficSpeedMaxKmH(), "speedStatus.trafficSpeedKmH");
        requireRange(speed.getTrafficStartSpeedMinKmH(), speed.getTrafficStartSpeedMaxKmH(), "speedStatus.trafficStartSpeedKmH");
        requireNonNegative(speed.getStoppedSpeedKmH(), "speedStatus.stoppedSpeedKmH");
        requireRange(speed.getDrivingStateTicksMin(), speed.getDrivingStateTicksMax(), "speedStatus.drivingStateTicks");
        requireRange(speed.getTrafficStateTicksMin(), speed.getTrafficStateTicksMax(), "speedStatus.trafficStateTicks");
        requireRange(speed.getStoppedStateTicksMin(), speed.getStoppedStateTicksMax(), "speedStatus.stoppedStateTicks");

        requirePositive(value.getHighwayDetection().getHighwayToleranceKm(), "highwayDetection.highwayToleranceKm");
    }

    private void requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }
    }

    private void requireProbability(Double value, String field) {
        if (value == null || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(field + " must be between 0.0 and 1.0.");
        }
    }

    private void requirePositive(Integer value, String field) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(field + " must be greater than zero.");
        }
    }

    private void requirePositive(Double value, String field) {
        if (value == null || value <= 0.0) {
            throw new IllegalArgumentException(field + " must be greater than zero.");
        }
    }

    private void requireNonNegative(Integer value, String field) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException(field + " must be zero or greater.");
        }
    }

    private void requireNonNegative(Double value, String field) {
        if (value == null || value < 0.0) {
            throw new IllegalArgumentException(field + " must be zero or greater.");
        }
    }

    private void requireRange(Integer min, Integer max, String field) {
        requireNonNegative(min, field + "Min");
        requireNonNegative(max, field + "Max");
        if (min > max) {
            throw new IllegalArgumentException(field + " minimum cannot be greater than maximum.");
        }
    }

    private void requireRange(Double min, Double max, String field) {
        requireNonNegative(min, field + "Min");
        requireNonNegative(max, field + "Max");
        if (min > max) {
            throw new IllegalArgumentException(field + " minimum cannot be greater than maximum.");
        }
    }
}
