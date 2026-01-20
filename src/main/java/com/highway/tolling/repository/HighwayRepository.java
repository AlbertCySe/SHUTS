package com.highway.tolling.repository;

import com.highway.tolling.model.Highway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Highway Repository Interface
 * Handles database operations for Highway entity
 */
@Repository
public interface HighwayRepository extends JpaRepository<Highway, Long> {

    /**
     * Find a highway by its name
     * 
     * @param highwayName the highway name
     * @return Optional containing the highway if found
     */
    Optional<Highway> findByHighwayName(String highwayName);

    /**
     * Check if a highway exists by name
     * 
     * @param highwayName the highway name
     * @return true if highway exists, false otherwise
     */
    boolean existsByHighwayName(String highwayName);
}
