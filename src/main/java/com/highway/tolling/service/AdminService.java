package com.highway.tolling.service;

import com.highway.tolling.model.Bill;
import com.highway.tolling.model.Vehicle;
import com.highway.tolling.model.Wallet;
import com.highway.tolling.repository.BillRepository;
import com.highway.tolling.repository.VehicleRepository;
import com.highway.tolling.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Service
 * Provides administrative functions for monitoring and management
 */
@Service
public class AdminService {

    private final VehicleRepository vehicleRepository;
    private final WalletRepository walletRepository;
    private final BillRepository billRepository;

    @Autowired
    public AdminService(VehicleRepository vehicleRepository,
            WalletRepository walletRepository,
            BillRepository billRepository) {
        this.vehicleRepository = vehicleRepository;
        this.walletRepository = walletRepository;
        this.billRepository = billRepository;
    }

    /**
     * Get all vehicles in the system
     * 
     * @return List of all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Get all wallets with negative balance
     * 
     * @return List of wallets in deficit
     */
    public List<Wallet> getWalletsWithNegativeBalance() {
        List<Wallet> allWallets = walletRepository.findAll();
        return allWallets.stream()
                .filter(wallet -> wallet.getBalance() < 0)
                .collect(Collectors.toList());
    }

    /**
     * Get all wallets in deficit (below minimum balance)
     * 
     * @return List of wallets in deficit
     */
    public List<Wallet> getWalletsInDeficit() {
        List<Wallet> allWallets = walletRepository.findAll();
        return allWallets.stream()
                .filter(Wallet::isInDeficit)
                .collect(Collectors.toList());
    }

    /**
     * Calculate total toll collected from all paid bills
     * 
     * @return Total amount collected
     */
    public double getTotalTollCollected() {
        List<Bill> allBills = billRepository.findAll();
        return allBills.stream()
                .mapToDouble(Bill::getTotalAmount)
                .sum();
    }

    /**
     * Get system statistics
     * 
     * @return AdminStats object with system statistics
     */
    public AdminStats getSystemStats() {
        long totalVehicles = vehicleRepository.count();
        long totalWallets = walletRepository.count();
        long totalBills = billRepository.count();
        double totalTollCollected = getTotalTollCollected();
        long walletsInDeficit = getWalletsInDeficit().size();

        return new AdminStats(
                totalVehicles,
                totalWallets,
                totalBills,
                totalTollCollected,
                walletsInDeficit);
    }

    /**
     * Inner class to hold admin statistics
     */
    public static class AdminStats {
        private long totalVehicles;
        private long totalWallets;
        private long totalBills;
        private double totalTollCollected;
        private long walletsInDeficit;

        public AdminStats(long totalVehicles, long totalWallets, long totalBills,
                double totalTollCollected, long walletsInDeficit) {
            this.totalVehicles = totalVehicles;
            this.totalWallets = totalWallets;
            this.totalBills = totalBills;
            this.totalTollCollected = totalTollCollected;
            this.walletsInDeficit = walletsInDeficit;
        }

        // Getters
        public long getTotalVehicles() {
            return totalVehicles;
        }

        public long getTotalWallets() {
            return totalWallets;
        }

        public long getTotalBills() {
            return totalBills;
        }

        public double getTotalTollCollected() {
            return totalTollCollected;
        }

        public long getWalletsInDeficit() {
            return walletsInDeficit;
        }

        @Override
        public String toString() {
            return "AdminStats{" +
                    "totalVehicles=" + totalVehicles +
                    ", totalWallets=" + totalWallets +
                    ", totalBills=" + totalBills +
                    ", totalTollCollected=₹" + totalTollCollected +
                    ", walletsInDeficit=" + walletsInDeficit +
                    '}';
        }
    }
}
