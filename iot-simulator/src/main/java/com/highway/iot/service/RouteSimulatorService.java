package com.highway.iot.service;

import com.highway.iot.model.VehicleSimulator;
import com.highway.simulator.entity.VehicleEntity;
import com.highway.simulator.entity.VehicleHistory;
import com.highway.simulator.repository.VehicleEntityRepository;
import com.highway.simulator.repository.VehicleHistoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Route Simulator Service
 * On startup: fetches all real vehicle IDs from the main backend, seeds the local DB,
 * and immediately starts simulation for EVERY vehicle.
 * Runs movement ticks on a fixed schedule.
 */
@Service
public class RouteSimulatorService {

    private final ActiveVehicleRegistry registry;
    private final VehicleEntityRepository vehicleRepo;
    private final VehicleHistoryRepository historyRepo;
    private final SimulatorPersistenceService persistenceService;
    private final SimulatorSettingsService settingsService;
    private final RouteFetchService routeFetchService;
    private final Random random = new Random();

    @Value("${core.api.base:http://localhost:8080}")
    private String coreApiBase;

    @Autowired
    public RouteSimulatorService(ActiveVehicleRegistry registry,
                                 VehicleEntityRepository vehicleRepo,
                                 VehicleHistoryRepository historyRepo,
                                 SimulatorPersistenceService persistenceService,
                                 SimulatorSettingsService settingsService,
                                 RouteFetchService routeFetchService) {
        this.registry = registry;
        this.vehicleRepo = vehicleRepo;
        this.historyRepo = historyRepo;
        this.persistenceService = persistenceService;
        this.settingsService = settingsService;
        this.routeFetchService = routeFetchService;
    }

    @PostConstruct
    public void initializeAllVehicles() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  IoT SIMULATOR - Syncing vehicles from Main Backend  ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        // Step 1: Fetch actual vehicle info from the main backend
        List<Map<String, Object>> mainVehicles = fetchVehiclesFromMainBackend();
        List<Long> vehicleIds = new ArrayList<>();

        if (mainVehicles.isEmpty()) {
            System.out.println("⚠️  Could not reach main backend. Using existing local DB.");
            // Fall back to local DB vehicles
            for (VehicleEntity v : vehicleRepo.findAll()) {
                vehicleIds.add(v.getCoreVehicleId());
            }
            // If still empty, seed with default 1-32
            if (vehicleIds.isEmpty()) {
                System.out.println("Seeding local DB with default 32 vehicle slots...");
                for (long i = 1; i <= 32; i++) {
                    vehicleRepo.save(new VehicleEntity(i, "VH-" + i, "CAR", "User #" + i, "PARKED"));
                    vehicleIds.add(i);
                }
            }
        } else {
            System.out.println("✅ Fetched " + mainVehicles.size() + " vehicles from main backend.");
            syncLocalDbWithMainBackend(mainVehicles);
            for (Map<String, Object> mv : mainVehicles) {
                Object idObj = mv.get("vehicleId");
                if (idObj instanceof Number) {
                    vehicleIds.add(((Number) idObj).longValue());
                }
            }
        }

        // Step 2: Load previously saved positions (state persistence)
        persistenceService.loadSavedState();

        // Step 3: Start ALL vehicles immediately
        System.out.println("🚀 Starting simulation for all " + vehicleIds.size() + " vehicles...");
        int routeCount = routeFetchService.getSelectableRouteCount();
        int started = 0;

        List<VehicleEntity> allEntities = vehicleRepo.findAll();
        for (VehicleEntity v : allEntities) {
            // Skip if already restored by persistence service
            if (registry.containsKey(v.getCoreVehicleId())) {
                System.out.println("  Vehicle " + v.getCoreVehicleId() + " restored from saved state.");
                continue;
            }
            // Assign a route cycling through available routes
            int routeId = (int) ((v.getCoreVehicleId() - 1) % routeCount) + 1;
            VehicleSimulator sim = routeFetchService.fetchAndAssignRouteForVehicle(
                    v.getCoreVehicleId().intValue(), routeId);
            if (sim != null) {
                v.setCurrentStatus("RUNNING");
                v.setLastActiveTimestamp(LocalDateTime.now());
                vehicleRepo.save(v);
                historyRepo.save(new VehicleHistory(v.getCoreVehicleId(), "TRIP_START",
                        "Auto-started on simulator boot. Route: " + sim.getRouteName()));
                registry.add(v.getCoreVehicleId(), sim);
                started++;
            }
        }

        System.out.println("✅ " + started + " vehicles started. " + registry.getAll().size() + " total active.");
        System.out.println("📡 Broadcasting to: " + coreApiBase + "/api/iot/data");
    }

    /**
     * Fetch all vehicles from the main backend's /api/vehicles endpoint.
     * Returns empty list if the backend is unreachable.
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchVehiclesFromMainBackend() {
        try {
            RestTemplate rt = new RestTemplate();
            Object[] vehicles = rt.getForObject(coreApiBase + "/api/vehicles", Object[].class);
            if (vehicles == null) return Collections.emptyList();

            List<Map<String, Object>> list = new ArrayList<>();
            for (Object v : vehicles) {
                if (v instanceof Map) {
                    list.add((Map<String, Object>) v);
                }
            }
            return list;
        } catch (Exception e) {
            System.out.println("⚠️  Could not fetch vehicles from main backend: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Sync local H2 DB so it has exactly the vehicles that exist in the main backend,
     * including user/owner name, vehicle number, and vehicle type.
     */
    @SuppressWarnings("unchecked")
    private void syncLocalDbWithMainBackend(List<Map<String, Object>> mainVehicles) {
        Set<Long> mainIdSet = new HashSet<>();

        for (Map<String, Object> mv : mainVehicles) {
            Object idObj = mv.get("vehicleId");
            if (!(idObj instanceof Number)) continue;
            Long id = ((Number) idObj).longValue();
            mainIdSet.add(id);

            String number = (String) mv.get("vehicleNumber");
            String type = (String) mv.get("vehicleType");
            String owner = "Unassigned";

            Object userObj = mv.get("user");
            if (userObj instanceof Map) {
                owner = (String) ((Map<String, Object>) userObj).get("name");
            } else {
                Object ownerIdObj = mv.get("ownerId");
                if (ownerIdObj instanceof Number) {
                    owner = "User #" + ownerIdObj;
                }
            }

            if (number == null) number = "VH-" + id;
            if (type == null) type = "CAR";

            Optional<VehicleEntity> existing = vehicleRepo.findByCoreVehicleId(id);
            if (existing.isPresent()) {
                // Update metadata if changed
                VehicleEntity v = existing.get();
                v.setVehicleNumber(number);
                v.setVehicleType(type);
                v.setOwnerName(owner);
                vehicleRepo.save(v);
            } else {
                // Add new vehicle
                VehicleEntity v = new VehicleEntity(id, number, type, owner, "PARKED");
                vehicleRepo.save(v);
                historyRepo.save(new VehicleHistory(id, "REGISTRATION", "Synced metadata from main backend."));
                System.out.println("  Added vehicle ID " + id + " (" + number + ") to local DB.");
            }
        }

        // Remove vehicles that no longer exist in main backend
        for (VehicleEntity v : vehicleRepo.findAll()) {
            if (!mainIdSet.contains(v.getCoreVehicleId())) {
                vehicleRepo.delete(v);
                System.out.println("  Removed stale vehicle ID " + v.getCoreVehicleId() + " from local DB.");
            }
        }
    }


    /**
     * Tick all RUNNING vehicles every movementTickIntervalMs.
     */
    @Scheduled(fixedDelayString = "#{@simulatorSettingsService.settings.movement.movementTickIntervalMs}")
    public void simulateMovement() {
        for (Map.Entry<Long, VehicleSimulator> entry : registry.getAll().entrySet()) {
            Optional<VehicleEntity> dbV = vehicleRepo.findByCoreVehicleId(entry.getKey());
            if (dbV.isPresent() && "RUNNING".equals(dbV.get().getCurrentStatus())) {
                entry.getValue().tick();

                // Log red flags for excessive speed (1% chance per tick)
                if (entry.getValue().getCurrentSpeedKmH() > settingsService.getSettings().getSpeedStatus().getDrivingSpeedMaxKmH()) {
                    if (random.nextDouble() < 0.01) {
                        historyRepo.save(new VehicleHistory(entry.getKey(), "RED_FLAG",
                                "Excessive speed detected: " + entry.getValue().getCurrentSpeedKmH() + " km/h"));
                    }
                }
            }
        }
    }

    // Direct proxy delegating UI API calls to Registry
    public List<Map<String, Object>> getAllCurrentLocations() {
        return registry.getAllCurrentLocations();
    }

    public Map<String, Object> getCurrentLocation() {
        return registry.getCurrentLocation();
    }
}
