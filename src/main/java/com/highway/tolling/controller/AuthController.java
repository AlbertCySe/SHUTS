package com.highway.tolling.controller;

import com.highway.tolling.model.User;
import com.highway.tolling.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication Controller
 * Handles login for both regular users (email + phone) and admin (email + password)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Value("${admin.email:admin@highway.com}")
    private String adminEmail;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Login endpoint
     * POST /api/auth/login
     *
     * For role="user":  verifies email + phoneNumber against the users table
     * For role="admin": verifies email + password against application.properties credentials
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        // ── Admin Login ──────────────────────────────────────────────
        if ("admin".equalsIgnoreCase(request.getRole())) {
            if (adminEmail.equals(request.getEmail()) &&
                    adminPassword.equals(request.getPhoneNumber())) {
                response.put("role",   "admin");
                response.put("name",   "Administrator");
                response.put("email",  adminEmail);
                response.put("userId", 0);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Invalid admin credentials.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }

        // ── User Login ────────────────────────────────────────────────
        Optional<User> optUser = userService.getUserByEmail(request.getEmail());

        if (optUser.isEmpty()) {
            response.put("message", "No account found with this email address.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = optUser.get();

        if (!user.getPhoneNumber().equals(request.getPhoneNumber())) {
            response.put("message", "Phone number does not match our records.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("role",        "user");
        response.put("userId",      user.getUserId());
        response.put("name",        user.getName());
        response.put("email",       user.getEmail());
        response.put("phoneNumber", user.getPhoneNumber());
        return ResponseEntity.ok(response);
    }

    /**
     * Request body for login
     * - role: "user" or "admin"
     * - email: user email
     * - phoneNumber: user's phone (for user) OR admin password (for admin)
     */
    public static class LoginRequest {
        private String email;
        private String phoneNumber;   // doubles as password for admin
        private String role;

        public String getEmail()       { return email; }
        public void setEmail(String e) { this.email = e; }

        public String getPhoneNumber()       { return phoneNumber; }
        public void setPhoneNumber(String p) { this.phoneNumber = p; }

        public String getRole()       { return role; }
        public void setRole(String r) { this.role = r; }
    }
}
