package com.highway.tolling.controller;

import com.highway.tolling.model.Bill;
import com.highway.tolling.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Bill Controller
 * Provides endpoints for bill retrieval
 */
@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillRepository billRepository;
    private final com.highway.tolling.service.BillService billService;
    private final com.highway.tolling.service.WalletService walletService;

    @Autowired
    public BillController(BillRepository billRepository, com.highway.tolling.service.BillService billService, com.highway.tolling.service.WalletService walletService) {
        this.billRepository = billRepository;
        this.billService = billService;
        this.walletService = walletService;
    }

    /**
     * Get all bills (Diagnostic View)
     * GET /api/bills
     */
    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        return ResponseEntity.ok(billRepository.findAll());
    }

    /**
     * Get all bills for a specific user
     * GET /api/bills/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bill>> getBillsByUserId(@org.springframework.web.bind.annotation.PathVariable("userId") Long userId) {
        return ResponseEntity.ok(billRepository.findByUserId(userId));
    }

    /**
     * Pay a pending bill manually using wallet balance
     * POST /api/bills/{billId}/pay
     */
    @org.springframework.web.bind.annotation.PostMapping("/{billId}/pay")
    public ResponseEntity<java.util.Map<String, Object>> payBill(@org.springframework.web.bind.annotation.PathVariable("billId") Long billId) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        Bill bill = billService.getBillById(billId).orElse(null);
        if (bill == null) {
            response.put("success", false);
            response.put("message", "Bill not found.");
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(response);
        }

        if (bill.getStatus() == com.highway.tolling.model.BillStatus.PAID) {
            response.put("success", false);
            response.put("message", "Bill is already paid.");
            return ResponseEntity.badRequest().body(response);
        }

        com.highway.tolling.model.Wallet wallet = walletService.getWalletByUserId(bill.getUserId()).orElse(null);
        if (wallet == null || wallet.getBalance() < bill.getTotalAmount()) {
            response.put("success", false);
            response.put("message", "Insufficient wallet balance.");
            return ResponseEntity.badRequest().body(response);
        }

        walletService.deductToll(wallet.getWalletId(), bill.getTotalAmount());
        billService.updateBillStatus(billId, com.highway.tolling.model.BillStatus.PAID);

        response.put("success", true);
        response.put("message", "Bill paid successfully!");
        return ResponseEntity.ok(response);
    }
}
