package com.highway.tolling.service;

import com.highway.tolling.model.User;
import com.highway.tolling.repository.UserRepository;
import com.highway.tolling.repository.WalletRepository;
import com.highway.tolling.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * User Service Class
 * Contains business logic for user operations
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public UserService(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    /**
     * Create a new user in the system
     * 
     * @param user the user to create
     * @return the created user
     */
    public User createUser(User user) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }

        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("User with phone number " + user.getPhoneNumber() + " already exists");
        }

        return userRepository.save(user);
    }

    /**
     * Get all users
     * 
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get a user by ID (includes vehicles)
     * 
     * @param userId the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Get a user by email
     * 
     * @param email the user's email
     * @return Optional containing the user if found
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Update user information
     * 
     * @param userId      the user ID
     * @param updatedUser the updated user data
     * @return the updated user
     */
    public User updateUser(Long userId, User updatedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setName(updatedUser.getName());
        user.setPhoneNumber(updatedUser.getPhoneNumber());

        return userRepository.save(user);
    }

    /**
     * Delete a user from the system
     * 
     * @param userId the user ID to delete
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        // Delete associated wallet first to prevent foreign key constraint violations
        Optional<Wallet> wallet = walletRepository.findByUser_UserId(userId);
        wallet.ifPresent(walletRepository::delete);

        userRepository.deleteById(userId);
    }
}
