package com.highway.iot.service;

import com.highway.iot.model.VehicleSimulator;
import com.highway.simulator.entity.VehicleEntity;
import com.highway.simulator.repository.VehicleEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * IoT Broadcaster Service
 * Serializes and sends live Vehicle GPS data to the Core project backend API every 2 seconds.
 */
@Service
public class IoTBroadcasterService {

    private final ActiveVehicleRegistry registry;
    private final VehicleEntityRepository vehicleRepo;
    private final NHDetectionService nhDetectionService;

    @Autowired
    public IoTBroadcasterService(ActiveVehicleRegistry registry,
                                 VehicleEntityRepository vehicleRepo,
                                 NHDetectionService nhDetectionService) {
        this.registry = registry;
        this.vehicleRepo = vehicleRepo;
        this.nhDetectionService = nhDetectionService;
    }

    @Scheduled(fixedRate = 2000)
    public void sendDataToBackends() {
        if (registry.isEmpty()) return;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        for (Map.Entry<Long, VehicleSimulator> entry : registry.getAll().entrySet()) {
            VehicleSimulator vehicle = entry.getValue();
            Optional<VehicleEntity> dbV = vehicleRepo.findByCoreVehicleId(entry.getKey());
            
            if (!dbV.isPresent() || !"RUNNING".equals(dbV.get().getCurrentStatus())) continue;
            if (!vehicle.isRouteReady()) continue;

            Map<String, Object> payload = new HashMap<>();
            payload.put("vehicleId", vehicle.getVehicleId());
            payload.put("latitude", vehicle.getCurrentLat());
            payload.put("longitude", vehicle.getCurrentLng());
            payload.put("timestamp", LocalDateTime.now().format(formatter));

            String currentNH = nhDetectionService.detectNH(vehicle.getCurrentLat(), vehicle.getCurrentLng());
            payload.put("isHighway", !"LOCAL_ROAD".equals(currentNH));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            try {
                restTemplate.postForEntity("http://localhost:8080/api/iot/data", request, String.class);
            } catch (Exception e) {
                // Silently skip if main backend is offline
            }
        }
    }
}
