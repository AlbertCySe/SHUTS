package com.highway.tolling.service;

import com.highway.tolling.model.*;
import com.highway.tolling.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class VehicleRequestService {

    private final VehicleRequestRepository requestRepo;
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final UserNotificationRepository notificationRepo;

    @Autowired
    public VehicleRequestService(VehicleRequestRepository requestRepo,
                                 VehicleRepository vehicleRepo,
                                 UserRepository userRepo,
                                 UserNotificationRepository notificationRepo) {
        this.requestRepo = requestRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.notificationRepo = notificationRepo;
    }

    /** User submits a vehicle action request */
    public VehicleRequest submit(Map<String, String> body) {
        VehicleRequest req = new VehicleRequest();
        req.setUserId(Long.parseLong(body.get("userId")));
        req.setRequestType(VehicleRequest.RequestType.valueOf(body.get("requestType")));
        req.setReason(body.getOrDefault("reason", ""));

        if (body.containsKey("vehicleId") && body.get("vehicleId") != null && !body.get("vehicleId").isBlank()) {
            req.setVehicleId(Long.parseLong(body.get("vehicleId")));
        }
        if (body.containsKey("requestedVehicleNumber")) req.setRequestedVehicleNumber(body.get("requestedVehicleNumber"));
        if (body.containsKey("requestedVehicleType"))   req.setRequestedVehicleType(body.get("requestedVehicleType"));
        if (body.containsKey("newOwnerUserId") && body.get("newOwnerUserId") != null && !body.get("newOwnerUserId").isBlank()) {
            req.setNewOwnerUserId(Long.parseLong(body.get("newOwnerUserId")));
        }
        return requestRepo.save(req);
    }

    public List<VehicleRequest> getAll()               { return requestRepo.findAllByOrderByCreatedAtDesc(); }
    public List<VehicleRequest> getByUser(Long userId) { return requestRepo.findByUserIdOrderByCreatedAtDesc(userId); }
    public long getPendingCount()                      { return requestRepo.countByStatus(VehicleRequest.RequestStatus.PENDING); }

    /** Admin approves — executes the action */
    public VehicleRequest approve(Long requestId, String adminNotes) {
        VehicleRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        String notifTitle, notifMsg;

        switch (req.getRequestType()) {
            case ADD -> {
                User owner = userRepo.findById(req.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                Vehicle v = new Vehicle();
                v.setVehicleNumber(req.getRequestedVehicleNumber().toUpperCase());
                v.setVehicleType(VehicleType.valueOf(req.getRequestedVehicleType().toUpperCase()));
                v.setUser(owner);
                vehicleRepo.save(v);
                notifTitle = "✅ Vehicle Add Request Approved";
                notifMsg = "Your vehicle " + req.getRequestedVehicleNumber() + " has been registered.";
            }
            case DEACTIVATE -> {
                Vehicle v = vehicleRepo.findById(req.getVehicleId())
                        .orElseThrow(() -> new RuntimeException("Vehicle not found"));
                v.setStatus("INACTIVE");
                vehicleRepo.save(v);
                notifTitle = "✅ Vehicle Deactivation Approved";
                notifMsg = "Vehicle " + v.getVehicleNumber() + " has been deactivated.";
            }
            case SCRAP -> {
                Vehicle v = vehicleRepo.findById(req.getVehicleId())
                        .orElseThrow(() -> new RuntimeException("Vehicle not found"));
                v.setStatus("SCRAPED");
                vehicleRepo.save(v);
                notifTitle = "✅ Vehicle Scrap Request Approved";
                notifMsg = "Vehicle " + v.getVehicleNumber() + " has been marked as scraped.";
            }
            case SELL -> {
                Vehicle v = vehicleRepo.findById(req.getVehicleId())
                        .orElseThrow(() -> new RuntimeException("Vehicle not found"));
                User newOwner = userRepo.findById(req.getNewOwnerUserId())
                        .orElseThrow(() -> new RuntimeException("New owner not found"));
                v.setUser(newOwner);
                vehicleRepo.save(v);
                notifTitle = "✅ Vehicle Sale/Transfer Approved";
                notifMsg = "Vehicle " + v.getVehicleNumber() + " has been transferred to User #" + newOwner.getUserId() + ".";
            }
            case MODIFY -> {
                Vehicle v = vehicleRepo.findById(req.getVehicleId())
                        .orElseThrow(() -> new RuntimeException("Vehicle not found"));
                if (req.getRequestedVehicleNumber() != null && !req.getRequestedVehicleNumber().isBlank())
                    v.setVehicleNumber(req.getRequestedVehicleNumber().toUpperCase());
                if (req.getRequestedVehicleType() != null && !req.getRequestedVehicleType().isBlank())
                    v.setVehicleType(VehicleType.valueOf(req.getRequestedVehicleType().toUpperCase()));
                vehicleRepo.save(v);
                notifTitle = "✅ Vehicle Modification Approved";
                notifMsg = "Your vehicle details have been updated to: " + v.getVehicleNumber() + " (" + v.getVehicleType() + ").";
            }
            default -> throw new RuntimeException("Unknown request type");
        }

        String note = (adminNotes != null && !adminNotes.isBlank()) ? " Admin note: " + adminNotes : "";
        notificationRepo.save(new UserNotification(req.getUserId(), notifTitle, notifMsg + note));

        req.setStatus(VehicleRequest.RequestStatus.APPROVED);
        req.setAdminNotes(adminNotes);
        req.setReviewedAt(LocalDateTime.now());
        return requestRepo.save(req);
    }

    /** Admin rejects — notifies user */
    public VehicleRequest reject(Long requestId, String adminNotes) {
        VehicleRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        String reason = (adminNotes != null && !adminNotes.isBlank()) ? " Reason: " + adminNotes : "";
        notificationRepo.save(new UserNotification(req.getUserId(),
                "❌ Vehicle Request Rejected",
                "Your " + req.getRequestType() + " request was rejected." + reason));

        req.setStatus(VehicleRequest.RequestStatus.REJECTED);
        req.setAdminNotes(adminNotes);
        req.setReviewedAt(LocalDateTime.now());
        return requestRepo.save(req);
    }
}
