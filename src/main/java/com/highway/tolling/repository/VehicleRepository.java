package com.highway.tolling.repository;

import com.highway.tolling.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Vehicle Repository Interface
 * Handles database operations for Vehicle entity
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Find a vehicle by its vehicle number
     * 
     * @param vehicleNumber the vehicle registration number
     * @return Optional containing the vehicle if found
     */
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    /**
     * Check if a vehicle exists by vehicle number
     * 
     * @param vehicleNumber the vehicle registration number
     * @return true if vehicle exists, false otherwise
     */
    boolean existsByVehicleNumber(String vehicleNumber);
}
