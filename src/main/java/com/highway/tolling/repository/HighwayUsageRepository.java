package com.highway.tolling.repository;

import com.highway.tolling.model.HighwayUsage;
import com.highway.tolling.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
     * Find all usage records for a vehicle in a date range
     */
    @Query("SELECT hu FROM HighwayUsage hu " +
            "WHERE hu.vehicleId = :vehicleId " +
            "AND hu.entryTimestamp >= :startDate AND hu.entryTimestamp < :endDate " +
            "ORDER BY hu.entryTimestamp DESC")
    List<HighwayUsage> findByVehicleIdAndDateRange(
            @Param("vehicleId") Long vehicleId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get total distance traveled by a vehicle in a date range
     */
    @Query("SELECT COALESCE(SUM(hu.distanceTraveled), 0.0) FROM HighwayUsage hu " +
            "WHERE hu.vehicleId = :vehicleId " +
            "AND hu.entryTimestamp >= :startDate AND hu.entryTimestamp < :endDate")
    Double getTotalDistanceByVehicleIdAndDateRange(
            @Param("vehicleId") Long vehicleId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get all distinct vehicle IDs that have usage in a date range
     */
    @Query("SELECT DISTINCT h.vehicleId FROM HighwayUsage h WHERE h.entryTimestamp >= :startDate AND h.entryTimestamp < :endDate")
    List<Long> findDistinctVehicleIdsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get total distance traveled by all vehicles of a user in a date range
     */
    @Query("SELECT COALESCE(SUM(hu.distanceTraveled), 0.0) FROM HighwayUsage hu, Vehicle v " +
            "WHERE hu.vehicleId = v.vehicleId AND v.user.userId = :userId " +
            "AND hu.entryTimestamp >= :startDate AND hu.entryTimestamp < :endDate")
    Double getTotalDistanceByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get total distance traveled by a vehicle on a specific highway
     */
    @Query("SELECT COALESCE(SUM(hu.distanceTraveled), 0.0) FROM HighwayUsage hu WHERE hu.vehicleId = :vehicleId AND hu.highwayId = :highwayId")
    Double getTotalDistanceByVehicleAndHighway(@Param("vehicleId") Long vehicleId, @Param("highwayId") Long highwayId);
}
