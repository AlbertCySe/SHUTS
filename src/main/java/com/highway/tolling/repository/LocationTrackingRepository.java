package com.highway.tolling.repository;

import com.highway.tolling.model.LocationTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LocationTracking Repository Interface
 * Handles database operations for LocationTracking entity
 */
@Repository
public interface LocationTrackingRepository extends JpaRepository<LocationTracking, Long> {

    /**
     * Find all location records for a specific vehicle
     * 
     * @param vehicleId the vehicle ID
     * @return list of location tracking records
     */
    List<LocationTracking> findByVehicleId(Long vehicleId);

    /**
     * Find all location records for a vehicle, ordered by timestamp descending
     * 
     * @param vehicleId the vehicle ID
     * @return list of location tracking records in reverse chronological order
     */
    List<LocationTracking> findByVehicleIdOrderByTimestampDesc(Long vehicleId);
}
