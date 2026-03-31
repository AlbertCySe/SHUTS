package com.highway.tolling.controller;

import com.highway.tolling.model.Wallet;
import com.highway.tolling.service.AdminService;
import com.highway.tolling.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final WalletService walletService;

    @Autowired
    public AdminController(AdminService adminService, WalletService walletService) {
        this.adminService = adminService;
        this.walletService = walletService;
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
     * Get system statistics dashboard
     * GET /api/admin/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminService.AdminStats> getSystemStats() {
        AdminService.AdminStats stats = adminService.getSystemStats();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }



    /**
     * Seed random wallet balances for all users with ₹0 or no wallet.
     * POST /api/admin/seed-wallets
     */
    @PostMapping("/seed-wallets")
    public ResponseEntity<Map<String, Object>> seedWallets() {
        int count = walletService.seedWallets();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Wallet seeding complete!");
        response.put("walletsUpdated", count);
        return ResponseEntity.ok(response);
    }
}
