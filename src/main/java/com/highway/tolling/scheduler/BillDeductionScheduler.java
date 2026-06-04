package com.highway.tolling.scheduler;

import com.highway.tolling.model.Bill;
import com.highway.tolling.model.BillStatus;
import com.highway.tolling.model.UserNotification;
import com.highway.tolling.repository.BillRepository;
import com.highway.tolling.repository.UserNotificationRepository;
import com.highway.tolling.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BillDeductionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BillDeductionScheduler.class);

    private final BillRepository billRepository;
    private final WalletService walletService;
    private final UserNotificationRepository userNotificationRepository;

    @Autowired
    public BillDeductionScheduler(BillRepository billRepository, WalletService walletService, UserNotificationRepository userNotificationRepository) {
        this.billRepository = billRepository;
        this.walletService = walletService;
        this.userNotificationRepository = userNotificationRepository;
    }

    /**
     * Runs every hour to check pending bills that are older than 24 hours
     * and have not been attempted for auto-deduction yet.
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void processDelayedAutoDeductions() {
        logger.info("Starting 24-hr delayed auto-deduction check for PENDING bills...");
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<Bill> pendingBills = billRepository.findByStatusAndAutoDeductAttemptedFalseAndCreatedAtBefore(BillStatus.PENDING, cutoffTime);
        
        if (pendingBills.isEmpty()) {
            logger.info("No bills found for delayed auto-deduction.");
            return;
        }

        logger.info("Found {} bills for auto-deduction processing.", pendingBills.size());

        for (Bill bill : pendingBills) {
            try {
                processSingleBillDeduction(bill);
            } catch (Exception e) {
                logger.error("Failed to process auto-deduction for bill {}: {}", bill.getBillId(), e.getMessage());
            }
        }
        
        logger.info("Finished delayed auto-deduction processing.");
    }

    private void processSingleBillDeduction(Bill bill) {
        Long userId = bill.getUserId();
        Double totalAmount = bill.getTotalAmount();
        
        walletService.getWalletByUserId(userId).ifPresentOrElse(wallet -> {
            if (wallet.getBalance() >= totalAmount) {
                // Sufficient balance: Deduct toll and mark as PAID
                walletService.deductToll(wallet.getWalletId(), totalAmount);
                bill.setStatus(BillStatus.PAID);
                bill.setAutoDeductAttempted(true);
                billRepository.save(bill);
                logger.info("Auto-deducted ₹{} from User {} wallet for Bill {}. Status marked PAID.", totalAmount, userId, bill.getBillId());
            } else {
                // Insufficient balance: Mark attempt as true and notify user
                bill.setAutoDeductAttempted(true);
                billRepository.save(bill);
                
                String title = "Low Wallet Balance - Action Required!";
                String message = String.format("Auto-deduction for your toll bill failed due to insufficient balance. Bill Amount: ₹%.2f. Please recharge your wallet and pay manually.", totalAmount);
                UserNotification notification = new UserNotification(userId, title, message);
                userNotificationRepository.save(notification);
                
                logger.info("Insufficient balance (₹{}) for User {} to cover Bill {}. Notification sent.", wallet.getBalance(), userId, bill.getBillId());
            }
        }, () -> {
            // Wallet not found
            bill.setAutoDeductAttempted(true);
            billRepository.save(bill);
            logger.warn("Wallet not found for User {}. Cannot process auto-deduction for Bill {}.", userId, bill.getBillId());
        });
    }
}
