package com.highway.tolling.scheduler;

import com.highway.tolling.model.Bill;
import com.highway.tolling.model.User;
import com.highway.tolling.service.BillService;
import com.highway.tolling.service.UserService;
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
 * 
 * This scheduled job runs once a month to:
 * 1. Get all users
 * 2. Calculate total toll for each user
 * 3. Generate a monthly bill
 */
@Component
public class MonthlyBillingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyBillingScheduler.class);

    private final UserService userService;
    private final BillService billService;

    @Autowired
    public MonthlyBillingScheduler(UserService userService, BillService billService) {
        this.userService = userService;
        this.billService = billService;
    }

    /**
     * Scheduled job that runs on the 1st day of every month at 00:00 (midnight)
     * 
     * Cron expression breakdown: "0 0 0 1 * ?"
     * - 0: seconds (at 0 seconds)
     * - 0: minutes (at 0 minutes)
     * - 0: hours (at midnight)
     * - 1: day of month (1st day)
     * - *: month (every month)
     * - ?: day of week (don't care)
     * 
     * Note: For testing, you can use: @Scheduled(fixedRate = 60000) // Runs every
     * 60 seconds
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void generateMonthlyBills() {
        logger.info("Starting monthly bill generation job...");

        try {
            // Get previous month details
            YearMonth previousMonth = YearMonth.now().minusMonths(1);
            String billMonth = previousMonth.toString(); // Format: "2026-01"
            LocalDate dueDate = LocalDate.now().plusDays(15); // Due date: 15 days from now

            // Get all users
            List<User> allUsers = userService.getAllUsers();
            logger.info("Found {} users to process", allUsers.size());

            int billsGenerated = 0;

            // Generate bill for each user
            for (User user : allUsers) {
                try {
                    // Check if bill already exists for this month
                    if (billService.getBillByUserAndMonth(user.getUserId(), billMonth).isPresent()) {
                        logger.info("Bill already exists for user {} for month {}",
                                user.getUserId(), billMonth);
                        continue;
                    }

                    // For simplicity, using mock data
                    // In a real system, you would:
                    // 1. Query location tracking data for the month
                    // 2. Calculate total distance traveled on highways
                    // 3. Calculate toll based on vehicle type and highway rates

                    double totalDistance = calculateUserDistance(user.getUserId(), billMonth);
                    double totalAmount = calculateUserToll(user.getUserId(), billMonth);

                    // Create bill
                    Bill bill = billService.createBill(
                            user.getUserId(),
                            totalDistance,
                            totalAmount,
                            billMonth,
                            dueDate);

                    logger.info("Generated bill {} for user {} - Amount: ₹{}",
                            bill.getBillId(), user.getUserId(), totalAmount);
                    billsGenerated++;

                } catch (Exception e) {
                    logger.error("Error generating bill for user {}: {}",
                            user.getUserId(), e.getMessage());
                }
            }

            logger.info("Monthly bill generation completed. Generated {} bills", billsGenerated);

        } catch (Exception e) {
            logger.error("Error in monthly bill generation job: {}", e.getMessage(), e);
        }
    }

    /**
     * Calculate total distance traveled by user in a month
     * This is a placeholder method for demonstration
     * 
     * In a real implementation:
     * - Query LocationTracking table for the given month
     * - Calculate distance between consecutive GPS points
     * - Sum up all distances
     * 
     * @param userId    User ID
     * @param billMonth Bill month
     * @return Total distance in kilometers
     */
    private double calculateUserDistance(Long userId, String billMonth) {
        // Placeholder logic - returns a random value for demonstration
        // In real implementation, query location_tracking table
        return Math.random() * 500; // Random distance between 0-500 km
    }

    /**
     * Calculate total toll amount for user in a month
     * This is a placeholder method for demonstration
     * 
     * In a real implementation:
     * - Get user's vehicles and their types
     * - Calculate toll based on distance and vehicle type rates
     * - Sum up all toll amounts
     * 
     * @param userId    User ID
     * @param billMonth Bill month
     * @return Total toll amount
     */
    private double calculateUserToll(Long userId, String billMonth) {
        // Placeholder logic - returns a random value for demonstration
        // In real implementation, use TollCalculationService
        double distance = calculateUserDistance(userId, billMonth);
        double avgRatePerKm = 2.5; // Average rate
        return distance * avgRatePerKm;
    }

    /**
     * Manual trigger for testing
     * Can be called from a controller for testing purposes
     */
    public void triggerBillGeneration() {
        logger.info("Manually triggered bill generation");
        generateMonthlyBills();
    }
}
