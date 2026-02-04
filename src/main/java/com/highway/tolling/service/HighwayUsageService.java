package com.highway.tolling.service;

import com.highway.tolling.model.HighwayUsage;
import com.highway.tolling.repository.HighwayUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * HighwayUsage Service
 * Manages highway usage sessions and distance accumulation
 */
@Service
public class HighwayUsageService {

    private final HighwayUsageRepository highwayUsageRepository;

    @Autowired
    public HighwayUsageService(HighwayUsageRepository highwayUsageRepository) {
        this.highwayUsageRepository = highwayUsageRepository;
    }

    /**
     * Create a new highway usage session when vehicle enters a highway
     */
    public HighwayUsage createHighwaySession(Long vehicleId, Long highwayId, LocalDateTime entryTimestamp,
            Double entryLatitude, Double entryLongitude) {
        HighwayUsage session = new HighwayUsage(
                vehicleId,
                highwayId,
                entryTimestamp,
                entryLatitude,
                entryLongitude);
        return highwayUsageRepository.save(session);
    }

    /**
     * Get active highway session for a vehicle (if any)
     */
    public Optional<HighwayUsage> getActiveSession(Long vehicleId) {
        return highwayUsageRepository.findActiveSessionByVehicleId(vehicleId);
    }

    /**
     * Add distance to an existing highway session
     */
    public HighwayUsage addDistanceToSession(HighwayUsage session, Double distance) {
        session.addDistance(distance);
        return highwayUsageRepository.save(session);
    }

    /**
     * Close highway session when vehicle exits the highway
     */
    public HighwayUsage closeSession(HighwayUsage session, LocalDateTime exitTimestamp,
            Double exitLatitude, Double exitLongitude) {
        session.setExitTimestamp(exitTimestamp);
        session.setExitLatitude(exitLatitude);
        session.setExitLongitude(exitLongitude);
        return highwayUsageRepository.save(session);
    }

    /**
     * Get all highway usage records for a vehicle
     */
    public List<HighwayUsage> getVehicleHighwayUsage(Long vehicleId) {
        return highwayUsageRepository.findByVehicleIdOrderByEntryTimestampDesc(vehicleId);
    }

    /**
     * Get total distance traveled by a vehicle across all highways
     */
    public Double getTotalDistanceByVehicle(Long vehicleId) {
        return highwayUsageRepository.getTotalDistanceByVehicleId(vehicleId);
    }

    /**
     * Get total distance traveled by a vehicle on a specific highway
     */
    public Double getTotalDistanceByVehicleAndHighway(Long vehicleId, Long highwayId) {
        return highwayUsageRepository.getTotalDistanceByVehicleAndHighway(vehicleId, highwayId);
    }
}
