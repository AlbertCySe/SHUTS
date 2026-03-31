package com.highway.iot.controller;

import com.highway.iot.service.RouteSimulatorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/iot")
@CrossOrigin(origins = "*")
public class LocationController {

    @Autowired
    private RouteSimulatorService simulatorService;

    @GetMapping("/live-location")
    public Map<String, Object> getLiveLocation() {
        return simulatorService.getCurrentLocation();
    }

    @GetMapping("/live-locations")
    public List<Map<String, Object>> getAllLiveLocations() {
        return simulatorService.getAllCurrentLocations();
    }
}
