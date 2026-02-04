package com.highway.tolling.repository;

import com.highway.tolling.model.AnomalyType;
import com.highway.tolling.model.DataAnomaly;
import com.highway.tolling.model.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DataAnomaly Repository
 * Data access layer for anomaly records
 */
@Repository
public interface DataAnomalyRepository extends JpaRepository<DataAnomaly, Long> {

    /**
     * Find all pending anomalies for review
     */
    List<DataAnomaly> findByReviewStatusOrderByDetectedAtDesc(ReviewStatus reviewStatus);

    /**
     * Find all anomalies for a specific vehicle
     */
    List<DataAnomaly> findByVehicleIdOrderByDetectedAtDesc(Long vehicleId);

    /**
     * Find pending anomalies for a specific vehicle
     */
    List<DataAnomaly> findByVehicleIdAndReviewStatus(Long vehicleId, ReviewStatus reviewStatus);

    /**
     * Find anomalies by type
     */
    List<DataAnomaly> findByAnomalyTypeOrderByDetectedAtDesc(AnomalyType anomalyType);

    /**
     * Count anomalies for a vehicle within a time period
     */
    @Query("SELECT COUNT(a) FROM DataAnomaly a WHERE a.vehicleId = :vehicleId AND a.detectedAt >= :since")
    Long countAnomaliesByVehicleSince(@Param("vehicleId") Long vehicleId, @Param("since") LocalDateTime since);

    /**
     * Count anomalies of a specific type for a vehicle within a time period
     */
    @Query("SELECT COUNT(a) FROM DataAnomaly a WHERE a.vehicleId = :vehicleId " +
            "AND a.anomalyType = :type AND a.detectedAt >= :since")
    Long countAnomaliesByVehicleAndTypeSince(@Param("vehicleId") Long vehicleId,
            @Param("type") AnomalyType type,
            @Param("since") LocalDateTime since);

    /**
     * Find recent anomalies of a specific type for a vehicle
     */
    List<DataAnomaly> findByVehicleIdAndAnomalyTypeAndDetectedAtAfterOrderByDetectedAtDesc(
            Long vehicleId, AnomalyType anomalyType, LocalDateTime after);
}
