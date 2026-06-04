package com.highway.tolling.scheduler;

import com.highway.tolling.model.Bill;
import com.highway.tolling.model.User;
import com.highway.tolling.model.Vehicle;
import com.highway.tolling.service.BillService;
import com.highway.tolling.service.EmailService;
import com.highway.tolling.service.HighwayUsageAggregationService;
import com.highway.tolling.service.HighwayUsageService;
import com.highway.tolling.service.UserService;
import com.highway.tolling.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Monthly Billing Scheduler
 * Automatically generates monthly bills for all users
 */
@Component
public class MonthlyBillingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyBillingScheduler.class);

    private final UserService userService;
    private final BillService billService;
    private final HighwayUsageService highwayUsageService;
    private final HighwayUsageAggregationService aggregationService;
    private final EmailService emailService;
    private final VehicleService vehicleService;
    private final com.highway.tolling.service.WalletService walletService;

    @Autowired
    public MonthlyBillingScheduler(BillService billService,
            VehicleService vehicleService,
            UserService userService,
            HighwayUsageService highwayUsageService,
            EmailService emailService,
            HighwayUsageAggregationService aggregationService,
            com.highway.tolling.service.WalletService walletService) {
        this.billService = billService;
        this.vehicleService = vehicleService;
        this.userService = userService;
        this.highwayUsageService = highwayUsageService;
        this.emailService = emailService;
        this.aggregationService = aggregationService;
        this.walletService = walletService;
    }

    /**
     * Scheduled job that runs on the 1st day of every month at 00:00 (midnight)
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void generateMonthlyBills() {
        logger.info("Starting automated monthly bill generation job...");
        try {
            YearMonth previousMonth = YearMonth.now().minusMonths(1);
            List<User> allUsers = userService.getAllUsers();
            logger.info("Found {} users to process", allUsers.size());

            int billsGenerated = 0;
            for (User user : allUsers) {
                if (generateBillForUser(user.getUserId(), previousMonth) != null) {
                    billsGenerated++;
                }
            }
            logger.info("Monthly bill generation completed. Generated {} bills", billsGenerated);
        } catch (Exception e) {
            logger.error("Error in monthly bill generation job: {}", e.getMessage(), e);
        }
    }

    /**
     * Generate bills for all vehicles individually
     */
    public int generateBillsForAllVehicles() {
        logger.info("Starting manual bill generation for all vehicles...");
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        List<Long> vehicleIds = highwayUsageService.getDistinctVehicleIdsWithUsage(previousMonth);
        
        int billsGenerated = 0;
        for (Long vehicleId : vehicleIds) {
            if (generateBillForVehicle(vehicleId, previousMonth) != null) {
                billsGenerated++;
            }
        }
        return billsGenerated;
    }

    /**
     * Generate bill for a specific user
     */
    public Bill generateBillForUser(Long userId, YearMonth month) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        String billMonth = month.toString();
        LocalDate dueDate = LocalDate.now().plusDays(15);

        try {
            // Check if consolidated bill already exists
            if (billService.getBillByUserAndMonth(userId, billMonth)
                    .filter(b -> b.getVehicleId() == null).isPresent()) {
                logger.info("Consolidated bill already exists for user {} for month {}", userId, billMonth);
                return null;
            }

            double totalDistance = highwayUsageService.getMonthlyDistanceForUser(userId, month);
            double totalAmount = aggregationService.calculateTotalUserTollForMonth(userId, month);

            if (totalDistance <= 0) return null;

            Bill bill = billService.createBill(userId, totalDistance, totalAmount, billMonth, dueDate);
            
            emailService.sendBillEmail(user, bill);
            return bill;
        } catch (Exception e) {
            logger.error("Failed to generate bill for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * Generate bill for a specific vehicle
     */
    public Bill generateBillForVehicle(Long vehicleId, YearMonth month) {
        String billMonth = month.toString();
        LocalDate dueDate = LocalDate.now().plusDays(15);

        try {
            // Check if vehicle-specific bill already exists
            if (billService.getBillByVehicleAndMonth(vehicleId, billMonth).isPresent()) {
                logger.info("Bill already exists for vehicle {} for month {}", vehicleId, billMonth);
                return null;
            }

            double distance = highwayUsageService.getMonthlyDistanceForVehicle(vehicleId, month);
            double totalAmount = aggregationService.calculateTollForMonth(vehicleId, month);
            
            if (distance <= 0) {
                logger.info("No usage found for vehicle {} for month {}. Skipping bill.", vehicleId, billMonth);
                return null;
            }

            Vehicle vehicle = vehicleService.getVehicleById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Vehicle not found: " + vehicleId));
            User user = vehicle.getUser();

            Bill bill = billService.createBill(user.getUserId(), vehicleId, distance, 
                    totalAmount, billMonth, dueDate);

            logger.info("Generated vehicle-specific bill {} for vehicle {} (User {})", 
                    bill.getBillId(), vehicleId, user.getUserId());
            
            emailService.sendBillEmail(user, bill);
            return bill;
        } catch (Exception e) {
            logger.error("Failed to generate bill for vehicle {}: {}", vehicleId, e.getMessage());
            return null;
        }
    }


    /**
     * Manual trigger for testing
     */
    public void triggerBillGeneration() {
        logger.info("Manually triggered consolidated bill generation");
        generateMonthlyBills();
    }
}
