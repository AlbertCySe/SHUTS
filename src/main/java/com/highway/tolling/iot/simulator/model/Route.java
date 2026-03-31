package com.highway.tolling.iot.simulator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Route Model
 * Defines a highway route with waypoints
 */
public class Route {
    private String name;
    private List<GPSPoint> waypoints;
    private double averageSpeedKmh;
    private int currentWaypointIndex;

    public Route(String name, double averageSpeedKmh) {
        this.name = name;
        this.averageSpeedKmh = averageSpeedKmh;
        this.waypoints = new ArrayList<>();
        this.currentWaypointIndex = 0;
    }

    public void addWaypoint(GPSPoint point) {
        waypoints.add(point);
    }

    public GPSPoint getStartPoint() {
        return waypoints.isEmpty() ? null : waypoints.get(0);
    }

    public GPSPoint getEndPoint() {
        return waypoints.isEmpty() ? null : waypoints.get(waypoints.size() - 1);
    }

    public GPSPoint getNextWaypoint() {
        if (currentWaypointIndex < waypoints.size()) {
            return waypoints.get(currentWaypointIndex++);
        }
        return null;
    }

    public boolean isComplete() {
        return currentWaypointIndex >= waypoints.size();
    }

    public void reset() {
        currentWaypointIndex = 0;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GPSPoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<GPSPoint> waypoints) {
        this.waypoints = waypoints;
    }

    public double getAverageSpeedKmh() {
        return averageSpeedKmh;
    }

    public void setAverageSpeedKmh(double averageSpeedKmh) {
        this.averageSpeedKmh = averageSpeedKmh;
    }

    @Override
    public String toString() {
        return String.format("Route{name='%s', waypoints=%d, avgSpeed=%.1f km/h}",
                name, waypoints.size(), averageSpeedKmh);
    }
}
