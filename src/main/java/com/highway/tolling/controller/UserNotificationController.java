package com.highway.tolling.controller;

import com.highway.tolling.model.UserNotification;
import com.highway.tolling.repository.UserNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.OPTIONS})
public class UserNotificationController {

    private final UserNotificationRepository repo;

    @Autowired
    public UserNotificationController(UserNotificationRepository repo) {
        this.repo = repo;
    }

    /** GET /api/notifications/user/{userId} – get all notifications for a user */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserNotification>> getForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(repo.findByUserIdOrderByCreatedAtDesc(userId));
    }

    /** GET /api/notifications/user/{userId}/unread-count – badge count */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long count = repo.countByUserIdAndIsRead(userId, false);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /** PUT /api/notifications/{id}/read – mark single notification as read */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id) {
        return repo.findById(id).map(n -> {
            n.setRead(true);
            return ResponseEntity.ok(repo.save(n));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** PUT /api/notifications/user/{userId}/read-all – mark all as read */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllRead(@PathVariable Long userId) {
        List<UserNotification> notifications = repo.findByUserIdOrderByCreatedAtDesc(userId);
        notifications.forEach(n -> n.setRead(true));
        repo.saveAll(notifications);
        return ResponseEntity.ok(Map.of("marked", notifications.size()));
    }
}
