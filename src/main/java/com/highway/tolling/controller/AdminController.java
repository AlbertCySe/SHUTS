package com.highway.tolling.controller;

import com.highway.tolling.dto.VehicleAdminDTO;
import com.highway.tolling.model.Vehicle;
import com.highway.tolling.model.Wallet;
import com.highway.tolling.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin Controller
 * REST API endpoints for administrative operations
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Get all vehicles in the system
     * GET /api/admin/vehicles
     */
    @GetMapping("/vehicles")
    public ResponseEntity<List<java.util.Map<String, Object>>> getAllVehicles() {
        List<java.util.Map<String, Object>> vehicles = adminService.getAllVehicles();
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    /**
     * Get all users with negative wallet balance
     * GET /api/admin/wallets/negative
     */
    @GetMapping("/wallets/negative")
    public ResponseEntity<List<Wallet>> getWalletsWithNegativeBalance() {
        List<Wallet> wallets = adminService.getWalletsWithNegativeBalance();
        return new ResponseEntity<>(wallets, HttpStatus.OK);
    }

    /**
     * Get all wallets in deficit (below minimum balance)
     * GET /api/admin/wallets/deficit
     */
    @GetMapping("/wallets/deficit")
    public ResponseEntity<List<Wallet>> getWalletsInDeficit() {
        List<Wallet> wallets = adminService.getWalletsInDeficit();
        return new ResponseEntity<>(wallets, HttpStatus.OK);
    }

    /**
     * Get total toll collected
     * GET /api/admin/toll/total
     */
    @GetMapping("/toll/total")
    public ResponseEntity<Map<String, Object>> getTotalTollCollected() {
        double totalToll = adminService.getTotalTollCollected();

        Map<String, Object> response = new HashMap<>();
        response.put("totalTollCollected", totalToll);
        response.put("currency", "INR");
        response.put("formattedAmount", "₹" + String.format("%.2f", totalToll));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get system statistics dashboard
     * GET /api/admin/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminService.AdminStats> getSystemStats() {
        AdminService.AdminStats stats = adminService.getSystemStats();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    /**
     * Health check for admin endpoints
     * GET /api/admin/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("Admin API is running!", HttpStatus.OK);
    }

    /**
     * Get paginated vehicles with user details for admin dashboard
     * GET /api/admin/vehicles/paginated
     * 
     * Uses JPQL projection to fetch vehicle and user data in single query
     * Optimized to avoid N+1 query problem
     * 
     * Example: GET /api/admin/vehicles/paginated?page=0&size=20
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ResponseEntity containing Page of VehicleAdminDTO
     */
    @GetMapping("/vehicles/paginated")
    public ResponseEntity<Page<VehicleAdminDTO>> getVehiclesPaginated(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleAdminDTO> vehiclesPage = adminService.getVehiclesWithUserData(pageable);
        return new ResponseEntity<>(vehiclesPage, HttpStatus.OK);
    }
}
