package com.highway.tolling.repository;

import com.highway.tolling.model.Bill;
import com.highway.tolling.model.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Bill Repository Interface
 * Handles database operations for Bill entity
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    /**
     * Find all bills for a specific user
     * 
     * @param userId the user ID
     * @return list of bills
     */
    List<Bill> findByUserId(Long userId);

    /**
     * Find all bills for a user with specific status
     * 
     * @param userId the user ID
     * @param status the bill status
     * @return list of bills
     */
    List<Bill> findByUserIdAndStatus(Long userId, BillStatus status);

    /**
     * Find bill by user ID and bill month
     * 
     * @param userId    the user ID
     * @param billMonth the bill month (format: "2026-01")
     * @return Optional containing the bill if found
     */
    Optional<Bill> findByUserIdAndBillMonth(Long userId, String billMonth);

    /**
     * Find all bills by status
     * 
     * @param status the bill status
     * @return list of bills
     */
    List<Bill> findByStatus(BillStatus status);
}
