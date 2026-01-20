package com.highway.tolling.service;

import com.highway.tolling.model.Vehicle;
import com.highway.tolling.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Vehicle Service Class
 * Contains business logic for vehicle operations
 */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Register a new vehicle in the system
     * 
     * @param vehicle the vehicle to register
     * @return the registered vehicle
     */
    public Vehicle registerVehicle(Vehicle vehicle) {
        // Check if vehicle already exists
        if (vehicleRepository.existsByVehicleNumber(vehicle.getVehicleNumber())) {
            throw new RuntimeException("Vehicle with number " + vehicle.getVehicleNumber() + " already exists");
        }
        return vehicleRepository.save(vehicle);
    }

    /**
     * Get all registered vehicles
     * 
     * @return list of all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Get a vehicle by its ID
     * 
     * @param id the vehicle ID
     * @return Optional containing the vehicle if found
     */
    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    /**
     * Get a vehicle by its vehicle number
     * 
     * @param vehicleNumber the vehicle registration number
     * @return Optional containing the vehicle if found
     */
    public Optional<Vehicle> getVehicleByNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber);
    }

    /**
     * Update vehicle information
     * 
     * @param id             the vehicle ID
     * @param updatedVehicle the updated vehicle data
     * @return the updated vehicle
     */
    public Vehicle updateVehicle(Long id, Vehicle updatedVehicle) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));

        vehicle.setVehicleType(updatedVehicle.getVehicleType());
        vehicle.setUser(updatedVehicle.getUser());

        return vehicleRepository.save(vehicle);
    }

    /**
     * Delete a vehicle from the system
     * 
     * @param id the vehicle ID to delete
     */
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new RuntimeException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}
