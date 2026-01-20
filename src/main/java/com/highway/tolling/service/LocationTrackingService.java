package com.highway.tolling.service;

import com.highway.tolling.model.LocationTracking;
import com.highway.tolling.repository.LocationTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * LocationTracking Service Class
 * Contains business logic for location tracking operations
 */
@Service
public class LocationTrackingService {

    private final LocationTrackingRepository locationTrackingRepository;

    @Autowired
    public LocationTrackingService(LocationTrackingRepository locationTrackingRepository) {
        this.locationTrackingRepository = locationTrackingRepository;
    }

    /**
     * Save location data from vehicle IoT device
     * 
     * @param locationTracking the location data to save
     * @return the saved location tracking record
     */
    public LocationTracking saveLocation(LocationTracking locationTracking) {
        return locationTrackingRepository.save(locationTracking);
    }

    /**
     * Get all location records for a specific vehicle
     * 
     * @param vehicleId the vehicle ID
     * @return list of location tracking records
     */
    public List<LocationTracking> getLocationsByVehicleId(Long vehicleId) {
        return locationTrackingRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
    }

    /**
     * Get a single location record by ID
     * 
     * @param id the location tracking ID
     * @return Optional containing the location record if found
     */
    public Optional<LocationTracking> getLocationById(Long id) {
        return locationTrackingRepository.findById(id);
    }

    /**
     * Get all location records
     * 
     * @return list of all location tracking records
     */
    public List<LocationTracking> getAllLocations() {
        return locationTrackingRepository.findAll();
    }
}
