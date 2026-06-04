package com.highway.tolling.controller;

import com.highway.tolling.model.Wallet;
import com.highway.tolling.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Wallet Controller
 * REST API endpoints for wallet operations
 */
@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Get wallet by user ID
     * GET /api/wallets/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Wallet> getWalletByUserId(@PathVariable Long userId) {
        return walletService.getWalletByUserId(userId)
                .map(wallet -> new ResponseEntity<>(wallet, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Submit a simulated UPI recharge request
     * POST /api/wallets/user/{userId}/recharge-request
     */
    @PostMapping("/user/{userId}/recharge-request")
    public ResponseEntity<java.util.Map<String, Object>> createRechargeRequest(
            @PathVariable Long userId,
            @RequestBody java.util.Map<String, Object> payload,
            @Autowired com.highway.tolling.repository.WalletRechargeRequestRepository rechargeRepo,
            @Autowired com.highway.tolling.service.UserService userService) {
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        com.highway.tolling.model.User user = userService.getUserById(userId).orElse(null);
        if (user == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Double amount = Double.valueOf(payload.get("amount").toString());
        String upiRef = "UPI" + System.currentTimeMillis();

        com.highway.tolling.model.WalletRechargeRequest request = 
                new com.highway.tolling.model.WalletRechargeRequest(user, amount, upiRef);
        rechargeRepo.save(request);

        response.put("success", true);
        response.put("message", "Recharge request submitted to admin for approval.");
        response.put("upiReference", upiRef);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all recharge requests for a user
     * GET /api/wallets/user/{userId}/recharge-requests
     */
    @GetMapping("/user/{userId}/recharge-requests")
    public ResponseEntity<java.util.List<com.highway.tolling.model.WalletRechargeRequest>> getUserRequests(
            @PathVariable Long userId,
            @Autowired com.highway.tolling.repository.WalletRechargeRequestRepository rechargeRepo) {
        return ResponseEntity.ok(rechargeRepo.findByUser_UserIdOrderByRequestDateDesc(userId));
    }
}
