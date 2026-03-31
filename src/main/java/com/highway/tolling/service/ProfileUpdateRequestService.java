package com.highway.tolling.service;

import com.highway.tolling.model.ProfileUpdateRequest;
import com.highway.tolling.model.User;
import com.highway.tolling.model.UserNotification;
import com.highway.tolling.repository.ProfileUpdateRequestRepository;
import com.highway.tolling.repository.UserNotificationRepository;
import com.highway.tolling.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfileUpdateRequestService {

    private final ProfileUpdateRequestRepository requestRepo;
    private final UserRepository userRepo;
    private final UserNotificationRepository notificationRepo;

    @Autowired
    public ProfileUpdateRequestService(ProfileUpdateRequestRepository requestRepo,
                                       UserRepository userRepo,
                                       UserNotificationRepository notificationRepo) {
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
        this.notificationRepo = notificationRepo;
    }

    /** User submits a new profile update request */
    public ProfileUpdateRequest submitRequest(Long userId, String reqName, String reqEmail, String reqPhone) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setUserId(userId);
        req.setCurrentName(user.getName());
        req.setCurrentEmail(user.getEmail());
        req.setCurrentPhone(user.getPhoneNumber());
        req.setRequestedName(reqName != null && !reqName.isBlank() ? reqName : user.getName());
        req.setRequestedEmail(reqEmail != null && !reqEmail.isBlank() ? reqEmail : user.getEmail());
        req.setRequestedPhone(reqPhone != null && !reqPhone.isBlank() ? reqPhone : user.getPhoneNumber());

        return requestRepo.save(req);
    }

    /** Admin: get all requests */
    public List<ProfileUpdateRequest> getAllRequests() {
        return requestRepo.findAllByOrderByCreatedAtDesc();
    }

    /** User: get profile update requests by user ID */
    public List<ProfileUpdateRequest> getRequestsByUser(Long userId) {
        return requestRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /** Admin: get pending count for badge */
    public long getPendingCount() {
        return requestRepo.countByStatus(ProfileUpdateRequest.RequestStatus.PENDING);
    }

    /** Admin: approve request, apply changes to user, and notify user */
    public ProfileUpdateRequest approveRequest(Long requestId, String adminNotes) {
        ProfileUpdateRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + req.getUserId()));

        // Apply changes
        user.setName(req.getRequestedName());
        user.setEmail(req.getRequestedEmail());
        user.setPhoneNumber(req.getRequestedPhone());
        userRepo.save(user);

        req.setStatus(ProfileUpdateRequest.RequestStatus.APPROVED);
        req.setAdminNotes(adminNotes);
        req.setReviewedAt(LocalDateTime.now());
        requestRepo.save(req);

        // Notify the user
        String noteText = (adminNotes != null && !adminNotes.isBlank())
                ? " Admin note: " + adminNotes
                : "";
        notificationRepo.save(new UserNotification(
                req.getUserId(),
                "✅ Profile Update Approved",
                "Your profile update request has been approved. Your name, email, and phone have been updated." + noteText
        ));

        return req;
    }

    /** Admin: reject request and notify user */
    public ProfileUpdateRequest rejectRequest(Long requestId, String adminNotes) {
        ProfileUpdateRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        req.setStatus(ProfileUpdateRequest.RequestStatus.REJECTED);
        req.setAdminNotes(adminNotes);
        req.setReviewedAt(LocalDateTime.now());
        requestRepo.save(req);

        // Notify the user
        String noteText = (adminNotes != null && !adminNotes.isBlank())
                ? " Reason: " + adminNotes
                : " No reason provided.";
        notificationRepo.save(new UserNotification(
                req.getUserId(),
                "❌ Profile Update Rejected",
                "Your profile update request was not approved." + noteText
        ));

        return req;
    }
}
