package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.config.SecurityUtils;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all users (for admin purposes)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Create a new user with proper validation and password encoding
     */
    public User createUser(User user) {
        // Validate input
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username, email, and password are required");
        }

        // Validate and sanitize input
        String username = SecurityUtils.validateAndSanitizeUsername(user.getUsername());
        String email = SecurityUtils.validateAndSanitizeEmail(user.getEmail());
        
        if (!SecurityUtils.meetsPasswordRequirements(user.getPassword())) {
            throw new IllegalArgumentException("Password must be at least 6 characters long and contain both letters and numbers");
        }

        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Set sanitized values
        user.setUsername(username);
        user.setEmail(email);
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    /**
     * Update user information
     */
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    // Validate and sanitize username if provided
                    if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(user.getUsername())) {
                        String username = SecurityUtils.validateAndSanitizeUsername(updatedUser.getUsername());
                        
                        // Check if new username already exists
                        if (userRepository.findByUsername(username).isPresent()) {
                            throw new IllegalArgumentException("Username already exists");
                        }
                        user.setUsername(username);
                    }

                    // Validate and sanitize email if provided
                    if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
                        String email = SecurityUtils.validateAndSanitizeEmail(updatedUser.getEmail());
                        
                        // Check if new email already exists
                        if (userRepository.findByEmail(email).isPresent()) {
                            throw new IllegalArgumentException("Email already exists");
                        }
                        user.setEmail(email);
                    }

                    // Update password if provided
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        if (!SecurityUtils.meetsPasswordRequirements(updatedUser.getPassword())) {
                            throw new IllegalArgumentException("Password must be at least 6 characters long and contain both letters and numbers");
                        }
                        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    /**
     * Update user profile (username and email only)
     */
    public User updateUserProfile(Long id, String username, String email) {
        return userRepository.findById(id)
                .map(user -> {
                    if (username != null && !username.equals(user.getUsername())) {
                        String sanitizedUsername = SecurityUtils.validateAndSanitizeUsername(username);
                        
                        if (userRepository.findByUsername(sanitizedUsername).isPresent()) {
                            throw new IllegalArgumentException("Username already exists");
                        }
                        user.setUsername(sanitizedUsername);
                    }

                    if (email != null && !email.equals(user.getEmail())) {
                        String sanitizedEmail = SecurityUtils.validateAndSanitizeEmail(email);
                        
                        if (userRepository.findByEmail(sanitizedEmail).isPresent()) {
                            throw new IllegalArgumentException("Email already exists");
                        }
                        user.setEmail(sanitizedEmail);
                    }

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    /**
     * Change user password
     */
    public void changePassword(Long id, String currentPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password
        if (!SecurityUtils.meetsPasswordRequirements(newPassword)) {
            throw new IllegalArgumentException("New password must be at least 6 characters long and contain both letters and numbers");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Check if username exists
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user without password (for API responses)
     */
    public User getUserWithoutPassword(Long id) {
        return userRepository.findById(id)
                .map(user -> new User(user.getId(), user.getUsername(), user.getEmail()))
                .orElse(null);
    }
}
