package com.highway.iot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.highway.iot.model.VehicleSimulator;
import com.highway.simulator.entity.VehicleEntity;
import com.highway.simulator.repository.VehicleEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Simulator Persistence Service
 * Periodically stores coordinates in JSON to allow graceful shutdown without losing vehicle routes.
 */
@Service
public class SimulatorPersistenceService {

    private final ActiveVehicleRegistry registry;
    private final VehicleEntityRepository vehicleRepo;
    private final RouteFetchService routeFetchService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String STATE_FILE = "./data/vehicle-state.json";
    private final Random random = new Random();

    @Autowired
    public SimulatorPersistenceService(ActiveVehicleRegistry registry,
                                       VehicleEntityRepository vehicleRepo,
                                       RouteFetchService routeFetchService) {
        this.registry = registry;
        this.vehicleRepo = vehicleRepo;
        this.routeFetchService = routeFetchService;
    }

    @Scheduled(fixedDelayString = "#{@simulatorSettingsService.settings.movement.stateSaveIntervalMs}")
    public void saveState() {
        try {
            File dataDir = new File("./data");
            if (!dataDir.exists()) dataDir.mkdirs();

            List<Map<String, Object>> states = new ArrayList<>();
            for (VehicleSimulator v : registry.getAll().values()) {
                Map<String, Object> s = new HashMap<>();
                s.put("vehicleId", v.getVehicleId());
                s.put("waypointIndex", v.getCurrentWaypointIndex());
                s.put("progress", v.getProgressToNext());
                states.add(s);
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(STATE_FILE), states);
        } catch (IOException e) {
            System.err.println("Failed to save vehicle state: " + e.getMessage());
        }
    }

    public void loadSavedState() {
        File file = new File(STATE_FILE);
        if (!file.exists()) return;

        try {
            List<Map<String, Object>> states = objectMapper.readValue(file, 
                    new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> state : states) {
                int id = ((Number) state.get("vehicleId")).intValue();
                int wpIdx = ((Number) state.get("waypointIndex")).intValue();
                double progress = ((Number) state.get("progress")).doubleValue();

                Optional<VehicleEntity> v = vehicleRepo.findByCoreVehicleId((long) id);
                if (v.isPresent() && "RUNNING".equals(v.get().getCurrentStatus())) {
                    VehicleSimulator sim = routeFetchService.fetchAndAssignRouteForVehicle(id, random.nextInt(routeFetchService.getSelectableRouteCount()) + 1);
                    if (sim != null) {
                        sim.restoreState(wpIdx, progress);
                        registry.add((long) id, sim);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to restore state: " + e.getMessage());
        }
    }
}
