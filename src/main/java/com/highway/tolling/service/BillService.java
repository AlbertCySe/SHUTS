package com.highway.tolling.service;

import com.highway.tolling.model.Bill;
import com.highway.tolling.model.BillStatus;
import com.highway.tolling.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Bill Service
 * Manages bill operations for monthly toll billing
 */
@Service
public class BillService {

    private final BillRepository billRepository;

    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    /**
     * Create a new bill
     * 
     * @param bill the bill to create
     * @return the created bill
     */
    public Bill createBill(Bill bill) {
        return billRepository.save(bill);
    }

    /**
     * Create a new bill with parameters
     * 
     * @param userId        User ID
     * @param totalDistance Total distance traveled
     * @param totalAmount   Total toll amount
     * @param billMonth     Bill month (format: "2026-01")
     * @param dueDate       Due date for payment
     * @return the created bill
     */
    public Bill createBill(Long userId, Double totalDistance, Double totalAmount,
            String billMonth, LocalDate dueDate) {
        Bill bill = new Bill(userId, totalDistance, totalAmount, billMonth, dueDate);
        return billRepository.save(bill);
    }

    /**
     * Get all bills for a user
     * 
     * @param userId the user ID
     * @return list of bills
     */
    public List<Bill> getUserBills(Long userId) {
        return billRepository.findByUserId(userId);
    }

    /**
     * Get bill by ID
     * 
     * @param billId the bill ID
     * @return Optional containing the bill if found
     */
    public Optional<Bill> getBillById(Long billId) {
        return billRepository.findById(billId);
    }

    /**
     * Get bill for a user for a specific month
     * 
     * @param userId    the user ID
     * @param billMonth the bill month (format: "2026-01")
     * @return Optional containing the bill if found
     */
    public Optional<Bill> getBillByUserAndMonth(Long userId, String billMonth) {
        return billRepository.findByUserIdAndBillMonth(userId, billMonth);
    }

    /**
     * Get all pending bills for a user
     * 
     * @param userId the user ID
     * @return list of pending bills
     */
    public List<Bill> getPendingBills(Long userId) {
        return billRepository.findByUserIdAndStatus(userId, BillStatus.PENDING);
    }

    /**
     * Mark bill as paid
     * 
     * @param billId the bill ID
     * @return updated bill
     */
    public Bill markBillAsPaid(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));

        bill.setStatus(BillStatus.PAID);
        return billRepository.save(bill);
    }

    /**
     * Mark bill as overdue
     * 
     * @param billId the bill ID
     * @return updated bill
     */
    public Bill markBillAsOverdue(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));

        bill.setStatus(BillStatus.OVERDUE);
        return billRepository.save(bill);
    }

    /**
     * Update bill status
     * 
     * @param billId the bill ID
     * @param status the new status
     * @return updated bill
     */
    public Bill updateBillStatus(Long billId, BillStatus status) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));

        bill.setStatus(status);
        return billRepository.save(bill);
    }

    /**
     * Get all bills by status
     * 
     * @param status the bill status
     * @return list of bills
     */
    public List<Bill> getBillsByStatus(BillStatus status) {
        return billRepository.findByStatus(status);
    }

    /**
     * Get all bills
     * 
     * @return list of all bills
     */
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }
}
