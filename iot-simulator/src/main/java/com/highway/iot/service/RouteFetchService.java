package com.highway.iot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.highway.iot.model.VehicleSimulator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Route Fetch Service
 * Contacts the OSRM project router to convert waypoints into detailed realistic driving paths.
 */
@Service
public class RouteFetchService {

    private static final Map<Integer, RouteDefinition> ROUTES = new LinkedHashMap<>();

    static {
        // Vehicle 1: Chennai → Trichy (Tamil Nadu, NH38)
        ROUTES.put(1, new RouteDefinition("Chennai → Trichy (TN)", new double[][]{
            {13.0645, 80.2036}, {12.9813, 80.1256}, {12.8279, 80.0384},
            {12.6946, 79.9722}, {12.5512, 79.9238}, {12.3846, 79.7963},
            {12.2224, 79.6631}, {12.0673, 79.5441}, {11.9567, 79.4893},
            {11.7513, 79.3149}, {11.4556, 79.0354}, {11.2335, 78.8598},
            {10.9934, 78.7304}, {10.8402, 78.6941}, {10.8037, 78.6811}
        }));
        // Vehicle 2: Trichy → Madurai (Tamil Nadu, NH44)
        ROUTES.put(2, new RouteDefinition("Trichy → Madurai (TN)", new double[][]{
            {10.8037, 78.6811}, {10.7250, 78.6200}, {10.5850, 78.5100},
            {10.4500, 78.3800}, {10.3200, 78.2500}, {10.1800, 78.1200},
            {10.0500, 78.0300}, {9.9500, 78.0800}, {9.9200, 78.1200}
        }));
        // Vehicle 3: Coimbatore → Salem (Tamil Nadu, NH44)
        ROUTES.put(3, new RouteDefinition("Coimbatore → Salem (TN)", new double[][]{
            {11.0168, 76.9558}, {11.0800, 77.0500}, {11.1500, 77.2000},
            {11.2200, 77.3500}, {11.3000, 77.5000}, {11.3800, 77.6200},
            {11.4500, 77.7200}, {11.5800, 77.8500}, {11.6643, 78.1460}
        }));
        // Vehicle 4: Bangalore → Mysore (Karnataka, NH275)
        ROUTES.put(4, new RouteDefinition("Bangalore → Mysore (KA)", new double[][]{
            {12.9716, 77.5946}, {12.8500, 77.5000}, {12.7500, 77.3800},
            {12.6500, 77.2500}, {12.5500, 77.1000}, {12.4500, 76.9500},
            {12.3500, 76.8000}, {12.3100, 76.6600}
        }));
        // Vehicle 5: Hyderabad → Vijayawada (Telangana/AP, NH65)
        ROUTES.put(5, new RouteDefinition("Hyderabad → Vijayawada (TS/AP)", new double[][]{
            {17.3850, 78.4867}, {17.2500, 78.6500}, {17.1000, 78.8500},
            {16.9500, 79.1000}, {16.8000, 79.3500}, {16.7000, 79.5500},
            {16.5800, 79.7500}, {16.5200, 80.0500}, {16.5087, 80.6480}
        }));
        // Vehicle 6: Mumbai → Pune (Maharashtra, Expressway)
        ROUTES.put(6, new RouteDefinition("Mumbai → Pune (MH)", new double[][]{
            {19.0760, 72.8777}, {19.0200, 72.9500}, {18.9500, 73.0500},
            {18.8500, 73.2000}, {18.7500, 73.3000}, {18.6500, 73.4000},
            {18.5800, 73.5500}, {18.5200, 73.7000}, {18.5204, 73.8567}
        }));
    }

    public VehicleSimulator fetchAndAssignRouteForVehicle(int id, int routeId) {
        RouteDefinition def = ROUTES.get(routeId);
        VehicleSimulator vehicle = new VehicleSimulator(id, def.name);
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        try {
            StringBuilder coords = new StringBuilder();
            for (int i = 0; i < def.waypoints.length; i++) {
                coords.append(def.waypoints[i][1]).append(",").append(def.waypoints[i][0]);
                if (i < def.waypoints.length - 1) coords.append(";");
            }
            String url = "https://router.project-osrm.org/route/v1/driving/" 
                       + coords + "?geometries=geojson&overview=full";

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode coordinatesNode = root.path("routes").get(0).path("geometry").path("coordinates");

            List<double[]> detailedRoute = new ArrayList<>();
            if (coordinatesNode.isArray()) {
                for (JsonNode coord : coordinatesNode) {
                    double lng = coord.get(0).asDouble();
                    double lat = coord.get(1).asDouble();
                    detailedRoute.add(new double[]{lat, lng});
                }
            }
            vehicle.setDetailedRoute(detailedRoute);
        } catch (Exception e) {
            List<double[]> fallback = new ArrayList<>();
            for (double[] wp : def.waypoints) {
                fallback.add(new double[]{wp[0], wp[1]});
            }
            vehicle.setDetailedRoute(fallback);
        }
        return vehicle;
    }

    private static class RouteDefinition {
        final String name;
        final double[][] waypoints;
        RouteDefinition(String name, double[][] waypoints) {
            this.name = name;
            this.waypoints = waypoints;
        }
    }
}
