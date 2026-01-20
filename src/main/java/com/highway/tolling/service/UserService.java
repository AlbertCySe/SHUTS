package com.highway.tolling.service;

import com.highway.tolling.model.User;
import com.highway.tolling.repository.UserRepository;
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

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        userRepository.deleteById(userId);
    }
}
