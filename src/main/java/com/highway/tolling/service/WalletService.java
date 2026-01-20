package com.highway.tolling.service;

import com.highway.tolling.model.User;
import com.highway.tolling.model.Wallet;
import com.highway.tolling.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Wallet Service
 * Manages wallet operations including toll deductions and balance management
 */
@Service
public class WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /**
     * Create a new wallet for a user
     * 
     * @param user           The user for whom to create wallet
     * @param initialBalance Initial balance amount
     * @param minimumBalance Minimum balance threshold
     * @return Created wallet
     */
    public Wallet createWallet(User user, Double initialBalance, Double minimumBalance) {
        // Check if wallet already exists for this user
        if (walletRepository.existsByUser_UserId(user.getUserId())) {
            throw new RuntimeException("Wallet already exists for user: " + user.getUserId());
        }

        Wallet wallet = new Wallet(user, initialBalance, minimumBalance);
        return walletRepository.save(wallet);
    }

    /**
     * Get wallet by wallet ID
     * 
     * @param walletId The wallet ID
     * @return Optional containing wallet if found
     */
    public Optional<Wallet> getWalletById(Long walletId) {
        return walletRepository.findById(walletId);
    }

    /**
     * Get wallet by user ID
     * 
     * @param userId The user ID
     * @return Optional containing wallet if found
     */
    public Optional<Wallet> getWalletByUserId(Long userId) {
        return walletRepository.findByUser_UserId(userId);
    }

    /**
     * Add money to wallet (recharge)
     * 
     * @param walletId The wallet ID
     * @param amount   Amount to add
     * @return Updated wallet
     */
    public Wallet addBalance(Long walletId, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        wallet.setBalance(wallet.getBalance() + amount);
        return walletRepository.save(wallet);
    }

    /**
     * Deduct toll amount from wallet
     * Allows negative balance (user can go into deficit)
     * 
     * @param walletId   The wallet ID
     * @param tollAmount Toll amount to deduct
     * @return Updated wallet with deduction result
     */
    public WalletDeductionResult deductToll(Long walletId, Double tollAmount) {
        if (tollAmount <= 0) {
            throw new IllegalArgumentException("Toll amount must be positive");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        // Store previous balance
        double previousBalance = wallet.getBalance();

        // Deduct toll amount (allows negative balance)
        double newBalance = previousBalance - tollAmount;
        wallet.setBalance(newBalance);

        // Save updated wallet
        Wallet updatedWallet = walletRepository.save(wallet);

        // Create result with deficit status
        boolean isDeficit = updatedWallet.isInDeficit();

        return new WalletDeductionResult(
                updatedWallet,
                previousBalance,
                newBalance,
                tollAmount,
                isDeficit);
    }

    /**
     * Check if wallet is in deficit
     * 
     * @param walletId The wallet ID
     * @return true if balance is below minimum balance
     */
    public boolean isWalletInDeficit(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));
        return wallet.isInDeficit();
    }

    /**
     * Get deficit amount (how much below minimum balance)
     * 
     * @param walletId The wallet ID
     * @return Deficit amount (0 if not in deficit)
     */
    public double getDeficitAmount(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        if (wallet.isInDeficit()) {
            return wallet.getMinimumBalance() - wallet.getBalance();
        }
        return 0.0;
    }

    /**
     * Inner class to hold wallet deduction results
     */
    public static class WalletDeductionResult {
        private Wallet wallet;
        private double previousBalance;
        private double newBalance;
        private double amountDeducted;
        private boolean isDeficit;

        public WalletDeductionResult(Wallet wallet, double previousBalance,
                double newBalance, double amountDeducted,
                boolean isDeficit) {
            this.wallet = wallet;
            this.previousBalance = previousBalance;
            this.newBalance = newBalance;
            this.amountDeducted = amountDeducted;
            this.isDeficit = isDeficit;
        }

        // Getters
        public Wallet getWallet() {
            return wallet;
        }

        public double getPreviousBalance() {
            return previousBalance;
        }

        public double getNewBalance() {
            return newBalance;
        }

        public double getAmountDeducted() {
            return amountDeducted;
        }

        public boolean isDeficit() {
            return isDeficit;
        }

        @Override
        public String toString() {
            return "WalletDeductionResult{" +
                    "walletId=" + wallet.getWalletId() +
                    ", previousBalance=₹" + previousBalance +
                    ", newBalance=₹" + newBalance +
                    ", amountDeducted=₹" + amountDeducted +
                    ", isDeficit=" + isDeficit +
                    '}';
        }
    }
}
