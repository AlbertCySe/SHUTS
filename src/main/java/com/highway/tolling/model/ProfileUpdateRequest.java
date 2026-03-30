package com.highway.tolling.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ProfileUpdateRequest Entity
 * Stores user-submitted profile change requests for admin review.
 */
@Entity
@Table(name = "profile_update_requests")
public class ProfileUpdateRequest {

    public enum RequestStatus { PENDING, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    // Current values (snapshot at time of request)
    private String currentName;
    private String currentEmail;
    private String currentPhone;

    // Requested new values
    private String requestedName;
    private String requestedEmail;
    private String requestedPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    private String adminNotes;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    // Constructors
    public ProfileUpdateRequest() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCurrentName() { return currentName; }
    public void setCurrentName(String currentName) { this.currentName = currentName; }

    public String getCurrentEmail() { return currentEmail; }
    public void setCurrentEmail(String currentEmail) { this.currentEmail = currentEmail; }

    public String getCurrentPhone() { return currentPhone; }
    public void setCurrentPhone(String currentPhone) { this.currentPhone = currentPhone; }

    public String getRequestedName() { return requestedName; }
    public void setRequestedName(String requestedName) { this.requestedName = requestedName; }

    public String getRequestedEmail() { return requestedEmail; }
    public void setRequestedEmail(String requestedEmail) { this.requestedEmail = requestedEmail; }

    public String getRequestedPhone() { return requestedPhone; }
    public void setRequestedPhone(String requestedPhone) { this.requestedPhone = requestedPhone; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
