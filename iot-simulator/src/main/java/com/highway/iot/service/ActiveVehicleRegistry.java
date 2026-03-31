package com.highway.iot.service;

import com.highway.iot.model.VehicleSimulator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Active Vehicle Registry
 * Central, dependency-free state container for all currently active VehicleSimulators.
 */
@Component
public class ActiveVehicleRegistry {
    
    private final Map<Long, VehicleSimulator> vehicles = new ConcurrentHashMap<>();

    public Map<Long, VehicleSimulator> getAll() { return vehicles; }
    
    public void add(Long id, VehicleSimulator sim) { vehicles.put(id, sim); }
    
    public void remove(Long id) { vehicles.remove(id); }
    
    public boolean isEmpty() { return vehicles.isEmpty(); }
    
    public boolean containsKey(Long id) { return vehicles.containsKey(id); }
    
    public VehicleSimulator get(Long id) { return vehicles.get(id); }

    // ═══════════════════════ API Access Data Generation ═══════════════════════

    public List<Map<String, Object>> getAllCurrentLocations() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        List<Map<String, Object>> locations = new ArrayList<>();

        for (VehicleSimulator v : vehicles.values()) {
            if (!v.isRouteReady()) continue;
            Map<String, Object> loc = new HashMap<>();
            loc.put("vehicleId", v.getVehicleId());
            loc.put("routeName", v.getRouteName());
            loc.put("latitude", v.getCurrentLat());
            loc.put("longitude", v.getCurrentLng());
            loc.put("speedKmH", v.getCurrentSpeedKmH());
            loc.put("status", v.getCurrentState());
            loc.put("timestamp", LocalDateTime.now().format(formatter));
            locations.add(loc);
        }
        return locations;
    }

    public Map<String, Object> getCurrentLocation() {
        if (vehicles.isEmpty()) return new HashMap<>();
        VehicleSimulator v = vehicles.values().iterator().next();
        Map<String, Object> loc = new HashMap<>();
        loc.put("vehicleId", v.getVehicleId());
        loc.put("latitude", v.getCurrentLat());
        loc.put("longitude", v.getCurrentLng());
        loc.put("speedKmH", v.getCurrentSpeedKmH());
        loc.put("status", v.getCurrentState());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        loc.put("timestamp", LocalDateTime.now().format(formatter));
        return loc;
    }
}
