package com.highway.iot.service;

import com.highway.iot.model.VehicleSimulator;
import com.highway.simulator.entity.VehicleEntity;
import com.highway.simulator.entity.VehicleHistory;
import com.highway.simulator.repository.VehicleEntityRepository;
import com.highway.simulator.repository.VehicleHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Vehicle Lifecycle Manager
 * Oversees realistic trip management: starting, pausing, and parking vehicles randomly.
 */
@Service
public class VehicleLifecycleManager {

    private final VehicleEntityRepository vehicleRepo;
    private final VehicleHistoryRepository historyRepo;
    private final RouteFetchService routeFetchService;
    private final ActiveVehicleRegistry registry;
    private final Random random = new Random();

    @Autowired
    public VehicleLifecycleManager(VehicleEntityRepository vehicleRepo,
                                   VehicleHistoryRepository historyRepo,
                                   RouteFetchService routeFetchService,
                                   ActiveVehicleRegistry registry) {
        this.vehicleRepo = vehicleRepo;
        this.historyRepo = historyRepo;
        this.routeFetchService = routeFetchService;
        this.registry = registry;
    }

    @Scheduled(fixedRate = 30000)
    public void executeLifecycleEvents() {
        List<VehicleEntity> allVehicles = vehicleRepo.findAll();
        
        for (VehicleEntity v : allVehicles) {
            String status = v.getCurrentStatus();
            
            // 10% chance to wake up a PARKED vehicle
            if ("PARKED".equals(status)) {
                if (random.nextDouble() < 0.10) {
                    startVehicle(v);
                }
            } 
            // 5% chance to pause a RUNNING vehicle for a break
            else if ("RUNNING".equals(status)) {
                if (random.nextDouble() < 0.05) {
                    pauseVehicle(v);
                } else if (registry.containsKey(v.getCoreVehicleId())) {
                    // Check if route finished
                    VehicleSimulator sim = registry.get(v.getCoreVehicleId());
                    if (sim.getCurrentWaypointIndex() >= sim.getDetailedRoute().size() - 2) {
                        stopVehicle(v);
                    }
                }
            }
            // 20% chance to resume a vehicle on a BREAK
            else if ("STOPPED_FOR_BREAK".equals(status)) {
                if (random.nextDouble() < 0.20) {
                    resumeVehicle(v);
                }
            }
        }
    }

    private void startVehicle(VehicleEntity v) {
        v.setCurrentStatus("RUNNING");
        v.setLastActiveTimestamp(LocalDateTime.now());
        vehicleRepo.save(v);
        
        historyRepo.save(new VehicleHistory(v.getCoreVehicleId(), "TRIP_START", "Vehicle started a new trip."));
        
        int routeId = random.nextInt(6) + 1; // 1 to 6
        VehicleSimulator sim = routeFetchService.fetchAndAssignRouteForVehicle(v.getCoreVehicleId().intValue(), routeId);
        if (sim != null) {
            registry.add(v.getCoreVehicleId(), sim);
            System.out.println("Vehicle " + v.getCoreVehicleId() + " woke up and is now RUNNING on Route " + routeId);
        }
    }

    private void pauseVehicle(VehicleEntity v) {
        v.setCurrentStatus("STOPPED_FOR_BREAK");
        vehicleRepo.save(v);
        historyRepo.save(new VehicleHistory(v.getCoreVehicleId(), "BREAK", "Vehicle stopped for a break."));
        
        if (registry.containsKey(v.getCoreVehicleId())) {
            registry.get(v.getCoreVehicleId()).tick(); // one last tick
        }
        System.out.println("Vehicle " + v.getCoreVehicleId() + " is taking a BREAK.");
    }

    private void resumeVehicle(VehicleEntity v) {
        v.setCurrentStatus("RUNNING");
        v.setLastActiveTimestamp(LocalDateTime.now());
        vehicleRepo.save(v);
        historyRepo.save(new VehicleHistory(v.getCoreVehicleId(), "RESUME", "Vehicle resumed trip after break."));
        System.out.println("Vehicle " + v.getCoreVehicleId() + " RESUMED trip.");
    }
    
    private void stopVehicle(VehicleEntity v) {
        v.setCurrentStatus("PARKED");
        vehicleRepo.save(v);
        historyRepo.save(new VehicleHistory(v.getCoreVehicleId(), "TRIP_END", "Vehicle ended trip and parked."));
        registry.remove(v.getCoreVehicleId());
        System.out.println("Vehicle " + v.getCoreVehicleId() + " completed trip and PARKED.");
    }
}
