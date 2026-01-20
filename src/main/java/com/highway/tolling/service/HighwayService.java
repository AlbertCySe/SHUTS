package com.highway.tolling.service;

import com.highway.tolling.model.Highway;
import com.highway.tolling.repository.HighwayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Highway Service Class
 * Contains business logic for highway operations
 */
@Service
public class HighwayService {

    private final HighwayRepository highwayRepository;

    @Autowired
    public HighwayService(HighwayRepository highwayRepository) {
        this.highwayRepository = highwayRepository;
    }

    /**
     * Create a new highway
     * 
     * @param highway the highway to create
     * @return the created highway
     */
    public Highway createHighway(Highway highway) {
        // Check if highway already exists
        if (highwayRepository.existsByHighwayName(highway.getHighwayName())) {
            throw new RuntimeException("Highway with name " + highway.getHighwayName() + " already exists");
        }
        return highwayRepository.save(highway);
    }

    /**
     * Get all highways
     * 
     * @return list of all highways
     */
    public List<Highway> getAllHighways() {
        return highwayRepository.findAll();
    }

    /**
     * Get a highway by ID
     * 
     * @param highwayId the highway ID
     * @return Optional containing the highway if found
     */
    public Optional<Highway> getHighwayById(Long highwayId) {
        return highwayRepository.findById(highwayId);
    }

    /**
     * Get a highway by name
     * 
     * @param highwayName the highway name
     * @return Optional containing the highway if found
     */
    public Optional<Highway> getHighwayByName(String highwayName) {
        return highwayRepository.findByHighwayName(highwayName);
    }

    /**
     * Update highway information
     * 
     * @param highwayId      the highway ID
     * @param updatedHighway the updated highway data
     * @return the updated highway
     */
    public Highway updateHighway(Long highwayId, Highway updatedHighway) {
        Highway highway = highwayRepository.findById(highwayId)
                .orElseThrow(() -> new RuntimeException("Highway not found with id: " + highwayId));

        highway.setHighwayName(updatedHighway.getHighwayName());
        highway.setStartLatitude(updatedHighway.getStartLatitude());
        highway.setStartLongitude(updatedHighway.getStartLongitude());
        highway.setEndLatitude(updatedHighway.getEndLatitude());
        highway.setEndLongitude(updatedHighway.getEndLongitude());
        highway.setRatePerKmForCar(updatedHighway.getRatePerKmForCar());
        highway.setRatePerKmForBike(updatedHighway.getRatePerKmForBike());
        highway.setRatePerKmForTruck(updatedHighway.getRatePerKmForTruck());

        return highwayRepository.save(highway);
    }

    /**
     * Delete a highway
     * 
     * @param highwayId the highway ID to delete
     */
    public void deleteHighway(Long highwayId) {
        if (!highwayRepository.existsById(highwayId)) {
            throw new RuntimeException("Highway not found with id: " + highwayId);
        }
        highwayRepository.deleteById(highwayId);
    }
}
