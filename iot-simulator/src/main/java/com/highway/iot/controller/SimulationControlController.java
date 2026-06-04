package com.highway.iot.controller;

import com.highway.iot.service.ActiveVehicleRegistry;
import com.highway.iot.service.RouteFetchService;
import com.highway.iot.service.SimulatorSettingsService;
import com.highway.simulator.entity.VehicleEntity;
import com.highway.simulator.entity.VehicleHistory;
import com.highway.simulator.repository.VehicleEntityRepository;
import com.highway.simulator.repository.VehicleHistoryRepository;
import com.highway.iot.model.VehicleSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Simulation Control Controller
 * Exposes REST endpoints to start/stop ALL vehicle simulations from the frontend.
 * Runs on the standalone IoT Simulator (port 8082).
 *
 * Endpoints:
 *   POST /api/simulation/start-all   → Wake all PARKED/STOPPED vehicles
 *   POST /api/simulation/stop-all    → Park all RUNNING vehicles
 *   GET  /api/simulation/status      → Live count and vehicle IDs
 */
@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(origins = "*")
public class SimulationControlController {

    private final VehicleEntityRepository vehicleRepo;
    private final VehicleHistoryRepository historyRepo;
    private final ActiveVehicleRegistry registry;
    private final RouteFetchService routeFetchService;
    private final SimulatorSettingsService settingsService;

    @Autowired
    public SimulationControlController(VehicleEntityRepository vehicleRepo,
                                       VehicleHistoryRepository historyRepo,
                                       ActiveVehicleRegistry registry,
                                       RouteFetchService routeFetchService,
                                       SimulatorSettingsService settingsService) {
        this.vehicleRepo = vehicleRepo;
        this.historyRepo = historyRepo;
        this.registry = registry;
        this.routeFetchService = routeFetchService;
        this.settingsService = settingsService;
    }

    /**
     * Force ALL vehicles to RUNNING state immediately.
     * Assigns a random route to each vehicle that is not already running.
     */
    @PostMapping("/start-all")
    public ResponseEntity<Map<String, Object>> startAll() {
        List<VehicleEntity> all = vehicleRepo.findAll();
        int started = 0;
        int alreadyRunning = 0;

        Random rng = new Random();
        int routeCount = routeFetchService.getSelectableRouteCount();

        for (VehicleEntity v : all) {
            if ("RUNNING".equals(v.getCurrentStatus())) {
                alreadyRunning++;
                continue;
            }
            // Set to RUNNING
            v.setCurrentStatus("RUNNING");
            v.setLastActiveTimestamp(LocalDateTime.now());
            vehicleRepo.save(v);
            historyRepo.save(new VehicleHistory(v.getCoreVehicleId(), "TRIP_START",
                    "Manually started via Simulate All."));

            // Assign a random route
            int routeId = rng.nextInt(routeCount) + 1;
            VehicleSimulator sim = routeFetchService.fetchAndAssignRouteForVehicle(
                    v.getCoreVehicleId().intValue(), routeId);
            if (sim != null) {
                registry.add(v.getCoreVehicleId(), sim);
            }
            started++;
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("started", started);
        response.put("alreadyRunning", alreadyRunning);
        response.put("totalActive", registry.getAll().size());
        response.put("message", started + " vehicle(s) started, " + alreadyRunning + " already running.");
        return ResponseEntity.ok(response);
    }

    /**
     * Force ALL vehicles to PARKED state immediately (stop all simulations).
     */
    @PostMapping("/stop-all")
    public ResponseEntity<Map<String, Object>> stopAll() {
        List<VehicleEntity> all = vehicleRepo.findAll();
        int stopped = 0;

        for (VehicleEntity v : all) {
            if (!"PARKED".equals(v.getCurrentStatus())) {
                v.setCurrentStatus("PARKED");
                vehicleRepo.save(v);
                historyRepo.save(new VehicleHistory(v.getCoreVehicleId(), "TRIP_END",
                        "Manually stopped via Stop All."));
                registry.remove(v.getCoreVehicleId());
                stopped++;
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("stopped", stopped);
        response.put("totalActive", registry.getAll().size());
        response.put("message", stopped + " vehicle(s) stopped.");
        return ResponseEntity.ok(response);
    }

    /**
     * Get all vehicles with their current live simulation data.
     * Used by the IoT Simulator dashboard at http://localhost:8082
     */
    @GetMapping("/vehicles")
    public ResponseEntity<List<Map<String, Object>>> getAllVehiclesWithStatus() {
        List<VehicleEntity> all = vehicleRepo.findAll();
        DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        List<Map<String, Object>> result = new ArrayList<>();
        for (VehicleEntity v : all) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("vehicleId", v.getCoreVehicleId());
            row.put("vehicleNumber", v.getVehicleNumber() != null ? v.getVehicleNumber() : "VH-" + v.getCoreVehicleId());
            row.put("vehicleType", v.getVehicleType() != null ? v.getVehicleType() : "CAR");
            row.put("ownerName", v.getOwnerName() != null ? v.getOwnerName() : "Unassigned");
            row.put("iotStatus", v.getCurrentStatus());

            com.highway.iot.model.VehicleSimulator sim = registry.get(v.getCoreVehicleId());

            if (sim != null && sim.isRouteReady()) {
                row.put("latitude", sim.getCurrentLat());
                row.put("longitude", sim.getCurrentLng());
                row.put("speedKmH", sim.getCurrentSpeedKmH());
                row.put("status", sim.getCurrentState());
                row.put("routeName", sim.getRouteName());
                row.put("isOnHighway", "HIGHWAY".equalsIgnoreCase(sim.getTravelMode()));
                row.put("timestamp", java.time.LocalDateTime.now().format(fmt));
            } else {
                row.put("latitude", null);
                row.put("longitude", null);
                row.put("speedKmH", null);
                row.put("status", v.getCurrentStatus());
                row.put("routeName", "—");
                row.put("isOnHighway", false);
                row.put("timestamp", null);
            }

            result.add(row);
        }
        return ResponseEntity.ok(result);
    }


    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        List<VehicleEntity> all = vehicleRepo.findAll();
        List<Long> runningIds = new ArrayList<>();
        List<Long> parkedIds = new ArrayList<>();

        for (VehicleEntity v : all) {
            if ("RUNNING".equals(v.getCurrentStatus())) {
                runningIds.add(v.getCoreVehicleId());
            } else {
                parkedIds.add(v.getCoreVehicleId());
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("activeCount", runningIds.size());
        response.put("parkedCount", parkedIds.size());
        response.put("activeVehicleIds", runningIds);
        response.put("totalVehicles", all.size());
        return ResponseEntity.ok(response);
    }
}
