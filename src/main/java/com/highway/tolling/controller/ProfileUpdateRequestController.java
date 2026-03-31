package com.highway.tolling.controller;

import com.highway.tolling.model.ProfileUpdateRequest;
import com.highway.tolling.service.ProfileUpdateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile-requests")
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS})
public class ProfileUpdateRequestController {

    private final ProfileUpdateRequestService service;

    @Autowired
    public ProfileUpdateRequestController(ProfileUpdateRequestService service) {
        this.service = service;
    }

    /** POST /api/profile-requests — User submits a change request */
    @PostMapping
    public ResponseEntity<?> submitRequest(@RequestBody Map<String, String> body) {
        try {
            Long userId = Long.parseLong(body.get("userId"));
            ProfileUpdateRequest req = service.submitRequest(
                    userId,
                    body.get("requestedName"),
                    body.get("requestedEmail"),
                    body.get("requestedPhone")
            );
            return ResponseEntity.ok(req);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to submit request: " + e.getMessage());
        }
    }

    /** GET /api/profile-requests — Admin: get all requests */
    @GetMapping
    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    /** GET /api/profile-requests/user/{userId} — Get requests for a specific user */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRequestsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getRequestsByUser(userId));
    }

    /** GET /api/profile-requests/pending/count — Admin: badge count */
    @GetMapping("/pending/count")
    public ResponseEntity<?> getPendingCount() {
        return ResponseEntity.ok(Map.of("count", service.getPendingCount()));
    }

    /** PUT /api/profile-requests/{id}/approve — Admin approves */
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        try {
            String notes = body != null ? body.getOrDefault("adminNotes", "") : "";
            return ResponseEntity.ok(service.approveRequest(id, notes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Approval failed: " + e.getMessage());
        }
    }

    /** PUT /api/profile-requests/{id}/reject — Admin rejects */
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        try {
            String notes = body != null ? body.getOrDefault("adminNotes", "") : "";
            return ResponseEntity.ok(service.rejectRequest(id, notes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Rejection failed: " + e.getMessage());
        }
    }
}
