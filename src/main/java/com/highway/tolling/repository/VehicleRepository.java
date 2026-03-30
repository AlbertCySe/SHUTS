package com.highway.tolling.repository;

import com.highway.tolling.dto.VehicleAdminDTO;
import com.highway.tolling.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * Fetch vehicles with user data as DTO using JPQL projection
     * Executes single query with JOIN to avoid N+1 problem
     * Optimized for admin dashboard
     * 
     * @param pageable pagination information
     * @return Page of VehicleAdminDTO
     */
    @Query("""
            SELECT new com.highway.tolling.dto.VehicleAdminDTO(
                v.vehicleId,
                v.vehicleNumber,
                v.vehicleType,
                u.userId,
                u.name,
                u.email
            )
            FROM Vehicle v
            INNER JOIN v.user u
            """)
    Page<VehicleAdminDTO> findAllVehiclesWithUserData(Pageable pageable);
}
