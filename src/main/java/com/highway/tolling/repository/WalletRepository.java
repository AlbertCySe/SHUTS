package com.highway.tolling.repository;

import com.highway.tolling.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Wallet Repository Interface
 * Handles database operations for Wallet entity
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Find a wallet by user ID
     * 
     * @param userId the user ID
     * @return Optional containing the wallet if found
     */
    Optional<Wallet> findByUser_UserId(Long userId);

    /**
     * Check if a wallet exists for a user
     * 
     * @param userId the user ID
     * @return true if wallet exists, false otherwise
     */
    boolean existsByUser_UserId(Long userId);
}
