package com.highway.iot.service;

import com.highway.iot.model.VehicleSimulator;
import com.highway.simulator.entity.VehicleEntity;
import com.highway.simulator.entity.VehicleHistory;
import com.highway.simulator.repository.VehicleEntityRepository;
import com.highway.simulator.repository.VehicleHistoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Route Simulator Service
 * High-level orchestrator. Initializes the database and ticks vehicle movements.
 */
@Service
public class RouteSimulatorService {

    private final ActiveVehicleRegistry registry;
    private final VehicleEntityRepository vehicleRepo;
    private final VehicleHistoryRepository historyRepo;
    private final SimulatorPersistenceService persistenceService;
    private final Random random = new Random();

    @Autowired
    public RouteSimulatorService(ActiveVehicleRegistry registry,
                                 VehicleEntityRepository vehicleRepo,
                                 VehicleHistoryRepository historyRepo,
                                 SimulatorPersistenceService persistenceService) {
        this.registry = registry;
        this.vehicleRepo = vehicleRepo;
        this.historyRepo = historyRepo;
        this.persistenceService = persistenceService;
    }

    @PostConstruct
    public void initializeAllVehicles() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  IoT SIMULATOR - Initializing Local DB Components    ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        if (vehicleRepo.count() == 0) {
            System.out.println("Seeding local database with 32 simulated vehicles...");
            for (long i = 1; i <= 32; i++) {
                VehicleEntity entity = new VehicleEntity(i, "PARKED");
                vehicleRepo.save(entity);
                historyRepo.save(new VehicleHistory(i, "REGISTRATION", "IoT Device mapped and initially parked."));
            }
            System.out.println("Seeded database with 32 vehicles successfully.");
        } else {
            System.out.println("Database already initialized with " + vehicleRepo.count() + " vehicles.");
        }
        
        persistenceService.loadSavedState();
    }

    @Scheduled(fixedRate = 500)
    public void simulateMovement() {
        for (Map.Entry<Long, VehicleSimulator> entry : registry.getAll().entrySet()) {
            Optional<VehicleEntity> dbV = vehicleRepo.findByCoreVehicleId(entry.getKey());
            if (dbV.isPresent() && "RUNNING".equals(dbV.get().getCurrentStatus())) {
                entry.getValue().tick();
                
                // Randomly log red flags if speeding
                if (entry.getValue().getCurrentSpeedKmH() > 80.0) {
                    if (random.nextDouble() < 0.01) { // 1% chance every 500ms when speeding
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
