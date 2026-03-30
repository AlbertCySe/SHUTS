package com.highway.tolling.controller;

import com.highway.tolling.service.VehicleRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vehicle-requests")
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS})
public class VehicleRequestController {

    private final VehicleRequestService service;

    @Autowired
    public VehicleRequestController(VehicleRequestService service) {
        this.service = service;
    }

    /** POST /api/vehicle-requests — user submits a request */
    @PostMapping
    public ResponseEntity<?> submit(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(service.submit(body));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to submit: " + e.getMessage());
        }
    }

    /** GET /api/vehicle-requests — admin: all requests */
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /** GET /api/vehicle-requests/pending/count — admin badge count */
    @GetMapping("/pending/count")
    public ResponseEntity<?> pendingCount() {
        return ResponseEntity.ok(Map.of("count", service.getPendingCount()));
    }

    /** GET /api/vehicle-requests/user/{userId} — user: own requests */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getByUser(userId));
    }

    /** PUT /api/vehicle-requests/{id}/approve */
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id,
                                     @RequestBody(required = false) Map<String, String> body) {
        try {
            String notes = body != null ? body.getOrDefault("adminNotes", "") : "";
            return ResponseEntity.ok(service.approve(id, notes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Approval failed: " + e.getMessage());
        }
    }

    /** PUT /api/vehicle-requests/{id}/reject */
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id,
                                    @RequestBody(required = false) Map<String, String> body) {
        try {
            String notes = body != null ? body.getOrDefault("adminNotes", "") : "";
            return ResponseEntity.ok(service.reject(id, notes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Rejection failed: " + e.getMessage());
        }
    }
}
