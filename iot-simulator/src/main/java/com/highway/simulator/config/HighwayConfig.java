package com.highway.simulator.config;

import com.highway.simulator.model.GPSPoint;
import com.highway.simulator.model.Route;
import org.springframework.stereotype.Component;

/**
 * Highway Route Configuration
 * Predefined routes for simulation
 */
@Component
public class HighwayConfig {

    /**
     * NH-44 Route (Bangalore to North)
     */
    public Route getNH44Route() {
        Route route = new Route("NH-44", 80.0);

        // Entry point (Bangalore outskirts)
        route.addWaypoint(new GPSPoint(12.9716, 77.5946));

        // Mid-points along highway
        route.addWaypoint(new GPSPoint(13.0216, 77.6446));
        route.addWaypoint(new GPSPoint(13.0716, 77.6946));
        route.addWaypoint(new GPSPoint(13.1216, 77.7446));

        // Exit point
        route.addWaypoint(new GPSPoint(13.1716, 77.7946));

        return route;
    }

    /**
     * NH-75 Loop Route
     */
    public Route getNH75Route() {
        Route route = new Route("NH-75", 75.0);

        // Start
        route.addWaypoint(new GPSPoint(13.0000, 77.6000));

        // Waypoints
        route.addWaypoint(new GPSPoint(13.0250, 77.6250));
        route.addWaypoint(new GPSPoint(13.0500, 77.6500));
        route.addWaypoint(new GPSPoint(13.0250, 77.6750));

        // End
        route.addWaypoint(new GPSPoint(13.0000, 77.7000));

        return route;
    }

    /**
     * Short Test Route
     */
    public Route getTestRoute() {
        Route route = new Route("Test-Route", 60.0);

        route.addWaypoint(new GPSPoint(12.9700, 77.5900));
        route.addWaypoint(new GPSPoint(12.9750, 77.5950));
        route.addWaypoint(new GPSPoint(12.9800, 77.6000));

        return route;
    }
}
