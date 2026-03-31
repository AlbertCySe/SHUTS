package com.highway.tolling.iot.simulator.service;

import com.highway.tolling.iot.simulator.model.GPSPoint;
import com.highway.tolling.iot.simulator.model.Route;
import com.highway.tolling.iot.simulator.model.SimulatedVehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * IoT Streaming Service
 * Manages active vehicle simulations
 */
@Service
public class IoTStreamingService {

    private final MovementSimulator movementSimulator;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<Long, SimulatedVehicle> activeSimulations = new ConcurrentHashMap<>();

    @Autowired
    public IoTStreamingService(MovementSimulator movementSimulator) {
        this.movementSimulator = movementSimulator;
    }

    /**
     * Start simulation for a vehicle on a specific route
     */
    public String startSimulation(Long vehicleId, String routeName) {
        if (activeSimulations.containsKey(vehicleId)) {
            return "Simulation already running for Vehicle " + vehicleId;
        }

        Route route = createSampleRoute(routeName);
        SimulatedVehicle vehicle = new SimulatedVehicle(vehicleId, route);
        activeSimulations.put(vehicleId, vehicle);

        executorService.submit(() -> {
            try {
                movementSimulator.simulateVehicle(vehicle);
            } finally {
                activeSimulations.remove(vehicleId);
            }
        });

        return "Started simulation for Vehicle " + vehicleId + " on route " + routeName;
    }

    /**
     * Create a sample route (can be expanded to use DB highways)
     */
    private Route createSampleRoute(String name) {
        Route route = new Route(name, 80.0);
        // Sample coords for NH-44 (Tamil Nadu area)
        route.addWaypoint(new GPSPoint(12.9716, 77.5946)); // Bangalore (Start)
        route.addWaypoint(new GPSPoint(12.0000, 78.0000)); // Waypoint 1
        route.addWaypoint(new GPSPoint(11.0000, 78.5000)); // Waypoint 2
        route.addWaypoint(new GPSPoint(10.7905, 78.7047)); // Tiruchirappalli (End)
        return route;
    }

    public boolean isVehicleSimulating(Long vehicleId) {
        return activeSimulations.containsKey(vehicleId);
    }
}
