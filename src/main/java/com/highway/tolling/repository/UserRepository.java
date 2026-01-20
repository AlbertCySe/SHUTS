package com.highway.tolling.repository;

import com.highway.tolling.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository Interface
 * Handles database operations for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by email
     * 
     * @param email the user's email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by phone number
     * 
     * @param phoneNumber the user's phone number
     * @return Optional containing the user if found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Check if a user exists by email
     * 
     * @param email the user's email address
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists by phone number
     * 
     * @param phoneNumber the user's phone number
     * @return true if user exists, false otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
