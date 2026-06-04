package com.highway.tolling.service;

import com.highway.tolling.model.HighwayUsage;
import com.highway.tolling.repository.HighwayUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
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

    /**
     * Get total distance traveled by a vehicle in a specific month
     */
    public Double getMonthlyDistanceForVehicle(Long vehicleId, YearMonth month) {
        LocalDateTime startDate = month.atDay(1).atStartOfDay();
        LocalDateTime endDate = month.plusMonths(1).atDay(1).atStartOfDay();
        return highwayUsageRepository.getTotalDistanceByVehicleIdAndDateRange(vehicleId, startDate, endDate);
    }

    /**
     * Get detailed usage records for a vehicle in a specific month
     */
    public List<HighwayUsage> getMonthlyUsageForVehicle(Long vehicleId, YearMonth month) {
        LocalDateTime startDate = month.atDay(1).atStartOfDay();
        LocalDateTime endDate = month.plusMonths(1).atDay(1).atStartOfDay();
        return highwayUsageRepository.findByVehicleIdAndDateRange(vehicleId, startDate, endDate);
    }
    /**
     * Get total distance traveled by all vehicles of a user in a specific month
     */
    public Double getMonthlyDistanceForUser(Long userId, YearMonth month) {
        LocalDateTime startDate = month.atDay(1).atStartOfDay();
        LocalDateTime endDate = month.plusMonths(1).atDay(1).atStartOfDay();
        return highwayUsageRepository.getTotalDistanceByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * Get all distinct vehicle IDs that have usage in a specific month
     */
    public List<Long> getDistinctVehicleIdsWithUsage(YearMonth month) {
        LocalDateTime startDate = month.atDay(1).atStartOfDay();
        LocalDateTime endDate = month.plusMonths(1).atDay(1).atStartOfDay();
        return highwayUsageRepository.findDistinctVehicleIdsByDateRange(startDate, endDate);
    }
}
