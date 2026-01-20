package com.highway.tolling.controller;

import com.highway.tolling.model.User;
import com.highway.tolling.model.Vehicle;
import com.highway.tolling.model.VehicleType;
import com.highway.tolling.service.UserService;
import com.highway.tolling.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller
 * REST API endpoints for user operations
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final VehicleService vehicleService;

    @Autowired
    public UserController(UserService userService, VehicleService vehicleService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    /**
     * Create a new user
     * POST /api/users
     * Request body: {"name": "John Doe", "email": "john@example.com",
     * "phoneNumber": "9876543210"}
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Get user by ID with their vehicles
     * GET /api/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Add a vehicle to a user
     * POST /api/users/{userId}/vehicles
     * Request body: {"vehicleNumber": "MH01AB1234", "vehicleType": "CAR"}
     */
    @PostMapping("/{userId}/vehicles")
    public ResponseEntity<Vehicle> addVehicleToUser(
            @PathVariable Long userId,
            @RequestBody VehicleRequest vehicleRequest) {
        try {
            // Get the user
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            // Create vehicle with user reference
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleNumber(vehicleRequest.getVehicleNumber());
            vehicle.setVehicleType(vehicleRequest.getVehicleType());
            vehicle.setUser(user);

            // Save vehicle
            Vehicle savedVehicle = vehicleService.registerVehicle(vehicle);
            return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all vehicles of a user
     * GET /api/users/{userId}/vehicles
     */
    @GetMapping("/{userId}/vehicles")
    public ResponseEntity<List<Vehicle>> getUserVehicles(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(user -> new ResponseEntity<>(user.getVehicles(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Update user information
     * PUT /api/users/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(userId, user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete a user
     * DELETE /api/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Helper class for vehicle request
     */
    public static class VehicleRequest {
        private String vehicleNumber;
        private VehicleType vehicleType;

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }

        public VehicleType getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(VehicleType vehicleType) {
            this.vehicleType = vehicleType;
        }
    }
}
