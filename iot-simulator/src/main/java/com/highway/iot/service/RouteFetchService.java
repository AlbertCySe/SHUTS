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
    private final SimulatorSettingsService settingsService;

    public RouteFetchService(SimulatorSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    static {
        // 1: Chennai → Trichy (Tamil Nadu, NH38)
        ROUTES.put(1, new RouteDefinition("Chennai → Trichy (TN)", new double[][]{
            {13.0645, 80.2036}, {12.9813, 80.1256}, {12.8279, 80.0384},
            {12.6946, 79.9722}, {12.5512, 79.9238}, {12.3846, 79.7963},
            {11.7513, 79.3149}, {11.4556, 79.0354}, {10.8037, 78.6811}
        }));
        // 2: Trichy → Madurai (Tamil Nadu, NH44)
        ROUTES.put(2, new RouteDefinition("Trichy → Madurai (TN)", new double[][]{
            {10.8037, 78.6811}, {10.5850, 78.5100}, {10.3200, 78.2500},
            {10.0500, 78.0300}, {9.9200, 78.1200}
        }));
        // 3: Coimbatore → Salem (Tamil Nadu, NH44)
        ROUTES.put(3, new RouteDefinition("Coimbatore → Salem (TN)", new double[][]{
            {11.0168, 76.9558}, {11.1500, 77.2000}, {11.3000, 77.5000},
            {11.5800, 77.8500}, {11.6643, 78.1460}
        }));
        // 4: Bangalore → Mysore (Karnataka, NH275)
        ROUTES.put(4, new RouteDefinition("Bangalore → Mysore (KA)", new double[][]{
            {12.9716, 77.5946}, {12.7500, 77.3800}, {12.5500, 77.1000},
            {12.3500, 76.8000}, {12.3100, 76.6600}
        }));
        // 5: Hyderabad → Vijayawada (NH65)
        ROUTES.put(5, new RouteDefinition("Hyderabad → Vijayawada (TS/AP)", new double[][]{
            {17.3850, 78.4867}, {17.1000, 78.8500}, {16.8000, 79.3500},
            {16.5800, 79.7500}, {16.5087, 80.6480}
        }));
        // 6: Mumbai → Pune (Maharashtra Expressway)
        ROUTES.put(6, new RouteDefinition("Mumbai → Pune (MH)", new double[][]{
            {19.0760, 72.8777}, {18.9500, 73.0500}, {18.7500, 73.3000},
            {18.5800, 73.5500}, {18.5204, 73.8567}
        }));
        // 7: Delhi → Agra (NH19)
        ROUTES.put(7, new RouteDefinition("Delhi → Agra (UP)", new double[][]{
            {28.6139, 77.2090}, {28.3000, 77.3500}, {27.9500, 77.5500},
            {27.6500, 77.8000}, {27.1767, 78.0081}
        }));
        // 8: Jaipur → Ajmer (Rajasthan, NH48)
        ROUTES.put(8, new RouteDefinition("Jaipur → Ajmer (RJ)", new double[][]{
            {26.9124, 75.7873}, {26.7000, 75.5000}, {26.5000, 75.2000},
            {26.3200, 74.9500}, {26.4499, 74.6399}
        }));
        // 9: Kolkata → Burdwan (West Bengal, NH19)
        ROUTES.put(9, new RouteDefinition("Kolkata → Burdwan (WB)", new double[][]{
            {22.5726, 88.3639}, {22.7000, 88.2000}, {22.9000, 87.9500},
            {23.1500, 87.7000}, {23.2324, 87.8615}
        }));
        // 10: Ahmedabad → Vadodara (Gujarat, NH48)
        ROUTES.put(10, new RouteDefinition("Ahmedabad → Vadodara (GJ)", new double[][]{
            {23.0225, 72.5714}, {22.7500, 72.7000}, {22.5000, 72.9500},
            {22.2500, 73.0500}, {22.3072, 73.1812}
        }));
        // 11: Bhopal → Indore (Madhya Pradesh, NH46)
        ROUTES.put(11, new RouteDefinition("Bhopal → Indore (MP)", new double[][]{
            {23.2599, 77.4126}, {23.0000, 77.0500}, {22.8000, 76.7500},
            {22.7000, 76.5000}, {22.7196, 75.8577}
        }));
        // 12: Lucknow → Varanasi (Uttar Pradesh, NH19)
        ROUTES.put(12, new RouteDefinition("Lucknow → Varanasi (UP)", new double[][]{
            {26.8467, 80.9462}, {26.5000, 81.5000}, {26.2000, 82.0000},
            {25.8000, 82.5000}, {25.3176, 82.9739}
        }));
        // 13: Pune → Nashik (Maharashtra, NH60)
        ROUTES.put(13, new RouteDefinition("Pune → Nashik (MH)", new double[][]{
            {18.5204, 73.8567}, {19.0000, 73.9000}, {19.3000, 74.0000},
            {19.7000, 73.8000}, {19.9973, 73.7898}
        }));
        // 14: Chandigarh → Shimla (Himachal Pradesh, NH5)
        ROUTES.put(14, new RouteDefinition("Chandigarh → Shimla (HP)", new double[][]{
            {30.7333, 76.7794}, {30.8500, 76.9000}, {31.0000, 77.0500},
            {31.1000, 77.1500}, {31.1048, 77.1734}
        }));
        // 15: Kochi → Thiruvananthapuram (Kerala, NH66)
        ROUTES.put(15, new RouteDefinition("Kochi → Trivandrum (KL)", new double[][]{
            {9.9312, 76.2673}, {9.5000, 76.3500}, {9.0000, 76.6000},
            {8.7000, 76.7000}, {8.5241, 76.9366}
        }));
        // 16: Guwahati → Shillong (Assam/Meghalaya, NH6)
        ROUTES.put(16, new RouteDefinition("Guwahati → Shillong (AS/ML)", new double[][]{
            {26.1445, 91.7362}, {25.9000, 91.8500}, {25.7000, 91.9000},
            {25.5700, 91.8800}
        }));
        // 17: Nagpur → Amravati (Maharashtra, NH53)
        ROUTES.put(17, new RouteDefinition("Nagpur → Amravati (MH)", new double[][]{
            {21.1458, 79.0882}, {21.0000, 78.8000}, {20.9300, 78.5000},
            {20.9320, 77.7523}
        }));
        // 18: Visakhapatnam → Vijayawada (Andhra Pradesh, NH16)
        ROUTES.put(18, new RouteDefinition("Vizag → Vijayawada (AP)", new double[][]{
            {17.6868, 83.2185}, {17.0000, 82.5000}, {16.7000, 81.8000},
            {16.5087, 80.6480}
        }));
        // 19: Surat → Rajkot (Gujarat, NH48)
        ROUTES.put(19, new RouteDefinition("Surat → Rajkot (GJ)", new double[][]{
            {21.1702, 72.8311}, {21.5000, 72.5000}, {22.0000, 72.0000},
            {22.3039, 70.8022}
        }));
        // 20: Patna → Gaya (Bihar, NH83)
        ROUTES.put(20, new RouteDefinition("Patna → Gaya (BR)", new double[][]{
            {25.5941, 85.1376}, {25.3000, 85.0000}, {25.0000, 84.8000},
            {24.7964, 84.9994}
        }));
    }

    public VehicleSimulator fetchAndAssignRouteForVehicle(int id, int routeId) {
        RouteDefinition def = ROUTES.get(routeId);
        if (def == null) {
            def = ROUTES.values().iterator().next();
        }
        VehicleSimulator vehicle = new VehicleSimulator(id, def.name, settingsService);
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        try {
            if (!Boolean.TRUE.equals(settingsService.getSettings().getRoute().getUseOsrmRouteGeometry())) {
                vehicle.setDetailedRoute(createWaypointRoute(def));
                return vehicle;
            }

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
            if (detailedRoute.isEmpty() && Boolean.TRUE.equals(settingsService.getSettings().getRoute().getFallbackToWaypointsOnOsrmFailure())) {
                detailedRoute = createWaypointRoute(def);
            }
            vehicle.setDetailedRoute(detailedRoute);
        } catch (Exception e) {
            if (Boolean.TRUE.equals(settingsService.getSettings().getRoute().getFallbackToWaypointsOnOsrmFailure())) {
                vehicle.setDetailedRoute(createWaypointRoute(def));
            }
        }
        return vehicle;
    }

    public int getSelectableRouteCount() {
        return ROUTES.size(); // always return the actual number of routes defined (20)
    }

    private List<double[]> createWaypointRoute(RouteDefinition def) {
        List<double[]> fallback = new ArrayList<>();
        for (double[] wp : def.waypoints) {
            fallback.add(new double[]{wp[0], wp[1]});
        }
        return fallback;
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
