package com.highway.tolling.repository;

import com.highway.tolling.model.HighwayUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * HighwayUsage Repository
 * Data access layer for highway usage sessions
 */
@Repository
public interface HighwayUsageRepository extends JpaRepository<HighwayUsage, Long> {

    /**
     * Find active highway session for a vehicle (exitTimestamp is null)
     */
    @Query("SELECT hu FROM HighwayUsage hu WHERE hu.vehicleId = :vehicleId AND hu.exitTimestamp IS NULL")
    Optional<HighwayUsage> findActiveSessionByVehicleId(@Param("vehicleId") Long vehicleId);

    /**
     * Find all active sessions for a vehicle
     */
    List<HighwayUsage> findByVehicleIdAndExitTimestampIsNull(Long vehicleId);

    /**
     * Find all highway usage records for a vehicle, ordered by entry time (most
     * recent first)
     */
    List<HighwayUsage> findByVehicleIdOrderByEntryTimestampDesc(Long vehicleId);

    /**
     * Find all completed sessions for a vehicle and highway
     */
    List<HighwayUsage> findByVehicleIdAndHighwayIdAndExitTimestampIsNotNull(Long vehicleId, Long highwayId);

    /**
     * Get total distance traveled by a vehicle across all highways
     */
    @Query("SELECT COALESCE(SUM(hu.distanceTraveled), 0.0) FROM HighwayUsage hu WHERE hu.vehicleId = :vehicleId")
    Double getTotalDistanceByVehicleId(@Param("vehicleId") Long vehicleId);

    /**
     * Get total distance traveled by a vehicle on a specific highway
     */
    @Query("SELECT COALESCE(SUM(hu.distanceTraveled), 0.0) FROM HighwayUsage hu WHERE hu.vehicleId = :vehicleId AND hu.highwayId = :highwayId")
    Double getTotalDistanceByVehicleAndHighway(@Param("vehicleId") Long vehicleId, @Param("highwayId") Long highwayId);
}
