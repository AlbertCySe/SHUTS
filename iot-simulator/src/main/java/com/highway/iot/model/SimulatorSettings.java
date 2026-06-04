package com.highway.iot.model;

/**
 * Editable simulator tuning values exposed to the dashboard.
 * These defaults mirror the current hardcoded simulator behavior.
 */
public class SimulatorSettings {

    private LifecycleSettings lifecycle = new LifecycleSettings();
    private MovementSettings movement = new MovementSettings();
    private RouteSettings route = new RouteSettings();
    private DrivingBehaviorSettings drivingBehavior = new DrivingBehaviorSettings();
    private SpeedStatusSettings speedStatus = new SpeedStatusSettings();
    private HighwayDetectionSettings highwayDetection = new HighwayDetectionSettings();

    public LifecycleSettings getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(LifecycleSettings lifecycle) {
        this.lifecycle = lifecycle;
    }

    public MovementSettings getMovement() {
        return movement;
    }

    public void setMovement(MovementSettings movement) {
        this.movement = movement;
    }

    public RouteSettings getRoute() {
        return route;
    }

    public void setRoute(RouteSettings route) {
        this.route = route;
    }

    public DrivingBehaviorSettings getDrivingBehavior() {
        return drivingBehavior;
    }

    public void setDrivingBehavior(DrivingBehaviorSettings drivingBehavior) {
        this.drivingBehavior = drivingBehavior;
    }

    public SpeedStatusSettings getSpeedStatus() {
        return speedStatus;
    }

    public void setSpeedStatus(SpeedStatusSettings speedStatus) {
        this.speedStatus = speedStatus;
    }

    public HighwayDetectionSettings getHighwayDetection() {
        return highwayDetection;
    }

    public void setHighwayDetection(HighwayDetectionSettings highwayDetection) {
        this.highwayDetection = highwayDetection;
    }

    public static class LifecycleSettings {
        private Integer lifecycleIntervalMs = 30000;
        private Double parkedStartProbability = 0.10;
        private Double runningPauseProbability = 0.05;
        private Double breakResumeProbability = 0.20;

        public Integer getLifecycleIntervalMs() { return lifecycleIntervalMs; }
        public void setLifecycleIntervalMs(Integer lifecycleIntervalMs) { this.lifecycleIntervalMs = lifecycleIntervalMs; }
        public Double getParkedStartProbability() { return parkedStartProbability; }
        public void setParkedStartProbability(Double parkedStartProbability) { this.parkedStartProbability = parkedStartProbability; }
        public Double getRunningPauseProbability() { return runningPauseProbability; }
        public void setRunningPauseProbability(Double runningPauseProbability) { this.runningPauseProbability = runningPauseProbability; }
        public Double getBreakResumeProbability() { return breakResumeProbability; }
        public void setBreakResumeProbability(Double breakResumeProbability) { this.breakResumeProbability = breakResumeProbability; }
    }

    public static class MovementSettings {
        private Integer movementTickIntervalMs = 500;
        private Integer backendBroadcastIntervalMs = 2000;
        private Integer stateSaveIntervalMs = 5000;

        public Integer getMovementTickIntervalMs() { return movementTickIntervalMs; }
        public void setMovementTickIntervalMs(Integer movementTickIntervalMs) { this.movementTickIntervalMs = movementTickIntervalMs; }
        public Integer getBackendBroadcastIntervalMs() { return backendBroadcastIntervalMs; }
        public void setBackendBroadcastIntervalMs(Integer backendBroadcastIntervalMs) { this.backendBroadcastIntervalMs = backendBroadcastIntervalMs; }
        public Integer getStateSaveIntervalMs() { return stateSaveIntervalMs; }
        public void setStateSaveIntervalMs(Integer stateSaveIntervalMs) { this.stateSaveIntervalMs = stateSaveIntervalMs; }
    }

    public static class RouteSettings {
        private Integer availableRouteCount = 6;
        private String routeSelectionMode = "RANDOM";
        private Boolean useOsrmRouteGeometry = true;
        private Boolean fallbackToWaypointsOnOsrmFailure = true;
        private Integer completionWaypointOffset = 2;

        public Integer getAvailableRouteCount() { return availableRouteCount; }
        public void setAvailableRouteCount(Integer availableRouteCount) { this.availableRouteCount = availableRouteCount; }
        public String getRouteSelectionMode() { return routeSelectionMode; }
        public void setRouteSelectionMode(String routeSelectionMode) { this.routeSelectionMode = routeSelectionMode; }
        public Boolean getUseOsrmRouteGeometry() { return useOsrmRouteGeometry; }
        public void setUseOsrmRouteGeometry(Boolean useOsrmRouteGeometry) { this.useOsrmRouteGeometry = useOsrmRouteGeometry; }
        public Boolean getFallbackToWaypointsOnOsrmFailure() { return fallbackToWaypointsOnOsrmFailure; }
        public void setFallbackToWaypointsOnOsrmFailure(Boolean fallbackToWaypointsOnOsrmFailure) { this.fallbackToWaypointsOnOsrmFailure = fallbackToWaypointsOnOsrmFailure; }
        public Integer getCompletionWaypointOffset() { return completionWaypointOffset; }
        public void setCompletionWaypointOffset(Integer completionWaypointOffset) { this.completionWaypointOffset = completionWaypointOffset; }
    }

    public static class DrivingBehaviorSettings {
        private Double wanderOffRouteProbability = 0.20;
        private Integer highwayStayTicksMin = 60;
        private Integer highwayStayTicksMax = 180;
        private Integer wanderTicksMin = 40;
        private Integer wanderTicksMax = 120;
        private Integer returnHighwayTicksMin = 80;
        private Integer returnHighwayTicksMax = 240;
        private Double wanderMaxOffsetDegrees = 0.04;
        private Double wanderSpeedMinKmH = 20.0;
        private Double wanderSpeedMaxKmH = 40.0;

        public Double getWanderOffRouteProbability() { return wanderOffRouteProbability; }
        public void setWanderOffRouteProbability(Double wanderOffRouteProbability) { this.wanderOffRouteProbability = wanderOffRouteProbability; }
        public Integer getHighwayStayTicksMin() { return highwayStayTicksMin; }
        public void setHighwayStayTicksMin(Integer highwayStayTicksMin) { this.highwayStayTicksMin = highwayStayTicksMin; }
        public Integer getHighwayStayTicksMax() { return highwayStayTicksMax; }
        public void setHighwayStayTicksMax(Integer highwayStayTicksMax) { this.highwayStayTicksMax = highwayStayTicksMax; }
        public Integer getWanderTicksMin() { return wanderTicksMin; }
        public void setWanderTicksMin(Integer wanderTicksMin) { this.wanderTicksMin = wanderTicksMin; }
        public Integer getWanderTicksMax() { return wanderTicksMax; }
        public void setWanderTicksMax(Integer wanderTicksMax) { this.wanderTicksMax = wanderTicksMax; }
        public Integer getReturnHighwayTicksMin() { return returnHighwayTicksMin; }
        public void setReturnHighwayTicksMin(Integer returnHighwayTicksMin) { this.returnHighwayTicksMin = returnHighwayTicksMin; }
        public Integer getReturnHighwayTicksMax() { return returnHighwayTicksMax; }
        public void setReturnHighwayTicksMax(Integer returnHighwayTicksMax) { this.returnHighwayTicksMax = returnHighwayTicksMax; }
        public Double getWanderMaxOffsetDegrees() { return wanderMaxOffsetDegrees; }
        public void setWanderMaxOffsetDegrees(Double wanderMaxOffsetDegrees) { this.wanderMaxOffsetDegrees = wanderMaxOffsetDegrees; }
        public Double getWanderSpeedMinKmH() { return wanderSpeedMinKmH; }
        public void setWanderSpeedMinKmH(Double wanderSpeedMinKmH) { this.wanderSpeedMinKmH = wanderSpeedMinKmH; }
        public Double getWanderSpeedMaxKmH() { return wanderSpeedMaxKmH; }
        public void setWanderSpeedMaxKmH(Double wanderSpeedMaxKmH) { this.wanderSpeedMaxKmH = wanderSpeedMaxKmH; }
    }

    public static class SpeedStatusSettings {
        private Double drivingProbability = 0.80;
        private Double trafficProbability = 0.15;
        private Double stoppedProbability = 0.05;
        private Double drivingSpeedMinKmH = 40.0;
        private Double drivingSpeedMaxKmH = 80.0;
        private Double drivingStartSpeedMinKmH = 60.0;
        private Double drivingStartSpeedMaxKmH = 70.0;
        private Double trafficSpeedMinKmH = 5.0;
        private Double trafficSpeedMaxKmH = 30.0;
        private Double trafficStartSpeedMinKmH = 15.0;
        private Double trafficStartSpeedMaxKmH = 25.0;
        private Double stoppedSpeedKmH = 0.0;
        private Integer drivingStateTicksMin = 40;
        private Integer drivingStateTicksMax = 120;
        private Integer trafficStateTicksMin = 20;
        private Integer trafficStateTicksMax = 50;
        private Integer stoppedStateTicksMin = 10;
        private Integer stoppedStateTicksMax = 30;

        public Double getDrivingProbability() { return drivingProbability; }
        public void setDrivingProbability(Double drivingProbability) { this.drivingProbability = drivingProbability; }
        public Double getTrafficProbability() { return trafficProbability; }
        public void setTrafficProbability(Double trafficProbability) { this.trafficProbability = trafficProbability; }
        public Double getStoppedProbability() { return stoppedProbability; }
        public void setStoppedProbability(Double stoppedProbability) { this.stoppedProbability = stoppedProbability; }
        public Double getDrivingSpeedMinKmH() { return drivingSpeedMinKmH; }
        public void setDrivingSpeedMinKmH(Double drivingSpeedMinKmH) { this.drivingSpeedMinKmH = drivingSpeedMinKmH; }
        public Double getDrivingSpeedMaxKmH() { return drivingSpeedMaxKmH; }
        public void setDrivingSpeedMaxKmH(Double drivingSpeedMaxKmH) { this.drivingSpeedMaxKmH = drivingSpeedMaxKmH; }
        public Double getDrivingStartSpeedMinKmH() { return drivingStartSpeedMinKmH; }
        public void setDrivingStartSpeedMinKmH(Double drivingStartSpeedMinKmH) { this.drivingStartSpeedMinKmH = drivingStartSpeedMinKmH; }
        public Double getDrivingStartSpeedMaxKmH() { return drivingStartSpeedMaxKmH; }
        public void setDrivingStartSpeedMaxKmH(Double drivingStartSpeedMaxKmH) { this.drivingStartSpeedMaxKmH = drivingStartSpeedMaxKmH; }
        public Double getTrafficSpeedMinKmH() { return trafficSpeedMinKmH; }
        public void setTrafficSpeedMinKmH(Double trafficSpeedMinKmH) { this.trafficSpeedMinKmH = trafficSpeedMinKmH; }
        public Double getTrafficSpeedMaxKmH() { return trafficSpeedMaxKmH; }
        public void setTrafficSpeedMaxKmH(Double trafficSpeedMaxKmH) { this.trafficSpeedMaxKmH = trafficSpeedMaxKmH; }
        public Double getTrafficStartSpeedMinKmH() { return trafficStartSpeedMinKmH; }
        public void setTrafficStartSpeedMinKmH(Double trafficStartSpeedMinKmH) { this.trafficStartSpeedMinKmH = trafficStartSpeedMinKmH; }
        public Double getTrafficStartSpeedMaxKmH() { return trafficStartSpeedMaxKmH; }
        public void setTrafficStartSpeedMaxKmH(Double trafficStartSpeedMaxKmH) { this.trafficStartSpeedMaxKmH = trafficStartSpeedMaxKmH; }
        public Double getStoppedSpeedKmH() { return stoppedSpeedKmH; }
        public void setStoppedSpeedKmH(Double stoppedSpeedKmH) { this.stoppedSpeedKmH = stoppedSpeedKmH; }
        public Integer getDrivingStateTicksMin() { return drivingStateTicksMin; }
        public void setDrivingStateTicksMin(Integer drivingStateTicksMin) { this.drivingStateTicksMin = drivingStateTicksMin; }
        public Integer getDrivingStateTicksMax() { return drivingStateTicksMax; }
        public void setDrivingStateTicksMax(Integer drivingStateTicksMax) { this.drivingStateTicksMax = drivingStateTicksMax; }
        public Integer getTrafficStateTicksMin() { return trafficStateTicksMin; }
        public void setTrafficStateTicksMin(Integer trafficStateTicksMin) { this.trafficStateTicksMin = trafficStateTicksMin; }
        public Integer getTrafficStateTicksMax() { return trafficStateTicksMax; }
        public void setTrafficStateTicksMax(Integer trafficStateTicksMax) { this.trafficStateTicksMax = trafficStateTicksMax; }
        public Integer getStoppedStateTicksMin() { return stoppedStateTicksMin; }
        public void setStoppedStateTicksMin(Integer stoppedStateTicksMin) { this.stoppedStateTicksMin = stoppedStateTicksMin; }
        public Integer getStoppedStateTicksMax() { return stoppedStateTicksMax; }
        public void setStoppedStateTicksMax(Integer stoppedStateTicksMax) { this.stoppedStateTicksMax = stoppedStateTicksMax; }
    }

    public static class HighwayDetectionSettings {
        private Double highwayToleranceKm = 5.0;

        public Double getHighwayToleranceKm() { return highwayToleranceKm; }
        public void setHighwayToleranceKm(Double highwayToleranceKm) { this.highwayToleranceKm = highwayToleranceKm; }
    }
}
