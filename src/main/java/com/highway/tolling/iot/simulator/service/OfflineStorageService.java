package com.highway.tolling.iot.simulator.service;

import com.highway.tolling.iot.simulator.model.QueuedGPSData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Offline Storage Service
 * Stores GPS data locally when network is unavailable
 */
@Service
public class OfflineStorageService {

    private final Queue<QueuedGPSData> offlineQueue = new ConcurrentLinkedQueue<>();
    private final String storageFilePath = "offline-gps-data.json";
    private final ObjectMapper objectMapper;

    public OfflineStorageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        loadFromDisk();
    }

    /**
     * Add GPS data to offline queue
     */
    public void queueData(Long vehicleId, double latitude, double longitude, LocalDateTime timestamp) {
        QueuedGPSData data = new QueuedGPSData(vehicleId, latitude, longitude, timestamp);
        offlineQueue.add(data);

        System.out.printf("[OFFLINE] Queued data for Vehicle %d (Queue size: %d)%n",
                vehicleId, offlineQueue.size());

        // Persist to disk
        saveToDisk();
    }

    /**
     * Get next item from queue without removing
     */
    public QueuedGPSData peek() {
        return offlineQueue.peek();
    }

    /**
     * Remove successfully sent item from queue
     */
    public QueuedGPSData poll() {
        QueuedGPSData data = offlineQueue.poll();
        saveToDisk(); // Update disk after removal
        return data;
    }

    /**
     * Get queue size
     */
    public int getQueueSize() {
        return offlineQueue.size();
    }

    /**
     * Check if queue is empty
     */
    public boolean isEmpty() {
        return offlineQueue.isEmpty();
    }

    /**
     * Save queue to disk for persistence
     */
    private void saveToDisk() {
        try {
            List<QueuedGPSData> dataList = new ArrayList<>(offlineQueue);
            objectMapper.writeValue(new File(storageFilePath), dataList);
        } catch (IOException e) {
            System.err.println("Failed to save offline data to disk: " + e.getMessage());
        }
    }

    /**
     * Load queue from disk on startup
     */
    private void loadFromDisk() {
        try {
            File file = new File(storageFilePath);
            if (file.exists()) {
                QueuedGPSData[] dataArray = objectMapper.readValue(file, QueuedGPSData[].class);
                offlineQueue.addAll(Arrays.asList(dataArray));

                if (!offlineQueue.isEmpty()) {
                    System.out.printf("[OFFLINE] Loaded %d items from disk%n", offlineQueue.size());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load offline data from disk: " + e.getMessage());
        }
    }

    /**
     * Clear all offline data (for testing)
     */
    public void clearQueue() {
        offlineQueue.clear();
        saveToDisk();
        System.out.println("[OFFLINE] Queue cleared");
    }
}
