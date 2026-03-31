package com.highway.simulator.service;

import com.highway.simulator.model.QueuedGPSData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Backend Client Service
 * Sends GPS data to the tolling backend API with offline support
 */
@Service
public class BackendClient {

    private final RestTemplate restTemplate;

    @Autowired
    private OfflineStorageService offlineStorage;

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    @Value("${backend.api.iot-data:/api/iot/data}")
    private String iotDataEndpoint;

    private boolean isOnline = true;

    public BackendClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Send GPS data to backend (with offline fallback)
     */
    public boolean sendGPSData(Long vehicleId, double latitude, double longitude, LocalDateTime timestamp) {
        // First, try to sync any queued data
        syncQueuedData();

        // Then try to send current data
        boolean success = attemptSend(vehicleId, latitude, longitude, timestamp);

        if (!success) {
            // Network unavailable - queue for later
            offlineStorage.queueData(vehicleId, latitude, longitude, timestamp);
            isOnline = false;
        } else {
            if (!isOnline) {
                System.out.println("[NETWORK] Connection restored!");
                isOnline = true;
            }
        }

        return success;
    }

    /**
     * Attempt to send GPS data to backend
     */
    private boolean attemptSend(Long vehicleId, double latitude, double longitude, LocalDateTime timestamp) {
        try {
            String url = backendUrl + iotDataEndpoint;

            // Create request body
            Map<String, Object> request = new HashMap<>();
            request.put("vehicleId", vehicleId);
            request.put("latitude", latitude);
            request.put("longitude", longitude);
            request.put("timestamp", timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            // Send POST request
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            boolean success = response.getStatusCode().is2xxSuccessful();

            if (success) {
                System.out.printf("[%s] Vehicle %d: GPS (%.6f, %.6f) → Backend: SUCCESS%n",
                        timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        vehicleId, latitude, longitude);
            }

            return success;

        } catch (Exception e) {
            // Network error - return false to trigger offline storage
            return false;
        }
    }

    /**
     * Sync queued data when network is available
     */
    public void syncQueuedData() {
        if (offlineStorage.isEmpty()) {
            return;
        }

        System.out.printf("[SYNC] Syncing %d queued items...%n", offlineStorage.getQueueSize());
        int syncedCount = 0;
        int failedCount = 0;

        // Try to send queued items
        while (!offlineStorage.isEmpty()) {
            QueuedGPSData data = offlineStorage.peek();

            if (data.getRetryCount() >= 3) {
                // Too many retries - skip
                System.err.printf("[SYNC] Skipping item after 3 retries: %s%n", data);
                offlineStorage.poll(); // Remove from queue
                failedCount++;
                continue;
            }

            boolean success = attemptSend(
                    data.getVehicleId(),
                    data.getLatitude(),
                    data.getLongitude(),
                    data.getTimestamp());

            if (success) {
                offlineStorage.poll(); // Remove from queue
                syncedCount++;
            } else {
                // Network still unavailable - stop trying
                data.incrementRetryCount();
                break;
            }
        }

        if (syncedCount > 0) {
            System.out.printf("[SYNC] Successfully synced %d items%n", syncedCount);
        }
        if (failedCount > 0) {
            System.err.printf("[SYNC] Failed to sync %d items%n", failedCount);
        }
    }

    /**
     * Check if currently online
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * Get offline queue size
     */
    public int getQueuedCount() {
        return offlineStorage.getQueueSize();
    }
}
