package com.highway.tolling.controller;

import com.highway.tolling.model.Bill;
import com.highway.tolling.model.Wallet;
import com.highway.tolling.scheduler.MonthlyBillingScheduler;
import com.highway.tolling.service.AdminService;
import com.highway.tolling.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
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
    private final MonthlyBillingScheduler monthlyBillingScheduler;

    @Autowired
    public AdminController(AdminService adminService, WalletService walletService,
            MonthlyBillingScheduler monthlyBillingScheduler) {
        this.adminService = adminService;
        this.walletService = walletService;
        this.monthlyBillingScheduler = monthlyBillingScheduler;
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

    /**
     * Populate sample highway usage records for testing
     * POST /api/admin/populate-usage
     */
    @PostMapping("/populate-usage")
    public ResponseEntity<Map<String, Object>> populateUsage() {
        int count = adminService.populateSampleUsage();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Usage data population complete!");
        response.put("recordsCreated", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Manually trigger monthly bill generation (Consolidated)
     * POST /api/admin/generate-bills
     */
    @PostMapping("/generate-bills")
    public ResponseEntity<Map<String, Object>> generateBills() {
        monthlyBillingScheduler.triggerBillGeneration();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Monthly bill generation triggered successfully!");
        return ResponseEntity.ok(response);
    }

    /**
     * Generate bill for a specific user
     * POST /api/admin/generate-bill/user/{userId}
     */
    @PostMapping("/generate-bill/user/{userId}")
    public ResponseEntity<Map<String, Object>> generateUserBill(@PathVariable Long userId) {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        Bill bill = monthlyBillingScheduler.generateBillForUser(userId, previousMonth);
        
        Map<String, Object> response = new HashMap<>();
        if (bill != null) {
            response.put("success", true);
            response.put("message", "Bill generated successfully for User " + userId);
            response.put("billId", bill.getBillId());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "No usage found or bill already exists for User " + userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
    }

    /**
     * Generate bill for a specific vehicle
     * POST /api/admin/generate-bill/vehicle/{vehicleId}
     */
    @PostMapping("/generate-bill/vehicle/{vehicleId}")
    public ResponseEntity<Map<String, Object>> generateVehicleBill(@PathVariable Long vehicleId) {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        Bill bill = monthlyBillingScheduler.generateBillForVehicle(vehicleId, previousMonth);
        
        Map<String, Object> response = new HashMap<>();
        if (bill != null) {
            response.put("success", true);
            response.put("message", "Bill generated successfully for Vehicle " + vehicleId);
            response.put("billId", bill.getBillId());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "No usage found or bill already exists for Vehicle " + vehicleId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
    }

    /**
     * Generate individual bills for all vehicles with usage
     * POST /api/admin/generate-all-vehicle-bills
     */
    @PostMapping("/generate-all-vehicle-bills")
    public ResponseEntity<Map<String, Object>> generateAllVehicleBills() {
        int count = monthlyBillingScheduler.generateBillsForAllVehicles();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Completed bulk vehicle bill generation.");
        response.put("billsGenerated", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get recent bills generated in the system
     * GET /api/admin/bills/recent
     */
    @GetMapping("/bills/recent")
    public ResponseEntity<List<Bill>> getRecentBills() {
        List<Bill> recentBills = adminService.getRecentBills();
        return ResponseEntity.ok(recentBills);
    }

    /**
     * Get all pending recharge requests
     * GET /api/admin/wallets/recharge-requests
     */
    @GetMapping("/wallets/recharge-requests")
    public ResponseEntity<List<com.highway.tolling.model.WalletRechargeRequest>> getPendingRechargeRequests(
            @Autowired com.highway.tolling.repository.WalletRechargeRequestRepository rechargeRepo) {
        return ResponseEntity.ok(rechargeRepo.findByStatusOrderByRequestDateAsc(com.highway.tolling.model.RechargeStatus.PENDING));
    }

    /**
     * Approve or decline a recharge request
     * POST /api/admin/wallets/recharge-requests/{id}/{action}
     * action: approve or decline
     */
    @PostMapping("/wallets/recharge-requests/{id}/{action}")
    public ResponseEntity<Map<String, Object>> handleRechargeRequest(
            @PathVariable Long id, @PathVariable String action,
            @Autowired com.highway.tolling.repository.WalletRechargeRequestRepository rechargeRepo,
            @Autowired com.highway.tolling.service.WalletService walletService) {
        
        Map<String, Object> response = new HashMap<>();
        com.highway.tolling.model.WalletRechargeRequest request = rechargeRepo.findById(id).orElse(null);
        if (request == null) {
            response.put("success", false);
            response.put("message", "Request not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (request.getStatus() != com.highway.tolling.model.RechargeStatus.PENDING) {
            response.put("success", false);
            response.put("message", "Request is already processed.");
            return ResponseEntity.badRequest().body(response);
        }

        if ("approve".equalsIgnoreCase(action)) {
            // Find wallet by user ID
            com.highway.tolling.model.Wallet wallet = walletService.getWalletByUserId(request.getUser().getUserId()).orElse(null);
            if (wallet != null) {
                walletService.addBalance(wallet.getWalletId(), request.getAmount());
            } else {
                // Should not happen, but fallback creating wallet
                walletService.createWallet(request.getUser(), request.getAmount(), 0.0);
            }

            request.setStatus(com.highway.tolling.model.RechargeStatus.APPROVED);
            request.setProcessedDate(java.time.LocalDateTime.now());
            rechargeRepo.save(request);

            response.put("success", true);
            response.put("message", "Request approved and wallet updated.");
            return ResponseEntity.ok(response);
        } else if ("decline".equalsIgnoreCase(action)) {
            request.setStatus(com.highway.tolling.model.RechargeStatus.REJECTED);
            request.setProcessedDate(java.time.LocalDateTime.now());
            rechargeRepo.save(request);

            response.put("success", true);
            response.put("message", "Request declined.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid action.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Manually Top-Up User Wallet (Admin Override)
     * POST /api/admin/wallets/user/{userId}/topup
     */
    @PostMapping("/wallets/user/{userId}/topup")
    public ResponseEntity<Map<String, Object>> manualTopUp(
            @PathVariable Long userId, @RequestBody Map<String, Object> payload,
            @Autowired com.highway.tolling.service.WalletService walletService) {
        
        Map<String, Object> response = new HashMap<>();
        Double amount = Double.valueOf(payload.get("amount").toString());

        com.highway.tolling.model.Wallet wallet = walletService.getWalletByUserId(userId).orElse(null);
        if (wallet == null) {
            response.put("success", false);
            response.put("message", "Wallet not found for user.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        walletService.addBalance(wallet.getWalletId(), amount);
        response.put("success", true);
        response.put("message", "₹" + amount + " added to user wallet successfully.");
        return ResponseEntity.ok(response);
    }
}
