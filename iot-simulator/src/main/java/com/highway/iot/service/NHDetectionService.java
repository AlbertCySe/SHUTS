package com.highway.iot.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Detects which National Highway a given coordinate is on,
 * by measuring point-to-line distance against known NH corridor segments.
 */
@Service
public class NHDetectionService {

    // Each entry: NH name → array of [lat, lng] waypoints defining that corridor
    private static final Map<String, double[][]> NH_CORRIDORS = new LinkedHashMap<>();

    static {
        // NH38 — Chennai to Trichy
        NH_CORRIDORS.put("NH38", new double[][]{
            {13.0645, 80.2036}, {12.9813, 80.1256}, {12.8279, 80.0384},
            {12.6946, 79.9722}, {12.5512, 79.9238}, {12.3846, 79.7963},
            {12.2224, 79.6631}, {12.0673, 79.5441}, {11.9567, 79.4893},
            {11.7513, 79.3149}, {11.4556, 79.0354}, {11.2335, 78.8598},
            {10.9934, 78.7304}, {10.8402, 78.6941}, {10.8037, 78.6811}
        });

        // NH44 — Trichy to Madurai
        NH_CORRIDORS.put("NH44", new double[][]{
            {10.8037, 78.6811}, {10.7250, 78.6200}, {10.5850, 78.5100},
            {10.4500, 78.3800}, {10.3200, 78.2500}, {10.1800, 78.1200},
            {10.0500, 78.0300}, {9.9500,  78.0800}, {9.9200,  78.1200}
        });

        // NH544 (formerly NH47) — Coimbatore to Salem
        NH_CORRIDORS.put("NH544", new double[][]{
            {11.0168, 76.9558}, {11.0800, 77.0500}, {11.1500, 77.2000},
            {11.2200, 77.3500}, {11.3000, 77.5000}, {11.3800, 77.6200},
            {11.4500, 77.7200}, {11.5800, 77.8500}, {11.6643, 78.1460}
        });

        // NH275 — Bangalore to Mysore
        NH_CORRIDORS.put("NH275", new double[][]{
            {12.9716, 77.5946}, {12.8500, 77.5000}, {12.7500, 77.3800},
            {12.6500, 77.2500}, {12.5500, 77.1000}, {12.4500, 76.9500},
            {12.3500, 76.8000}, {12.3100, 76.6600}
        });

        // NH65 — Hyderabad to Vijayawada
        NH_CORRIDORS.put("NH65", new double[][]{
            {17.3850, 78.4867}, {17.2500, 78.6500}, {17.1000, 78.8500},
            {16.9500, 79.1000}, {16.8000, 79.3500}, {16.7000, 79.5500},
            {16.5800, 79.7500}, {16.5200, 80.0500}, {16.5087, 80.6480}
        });

        // NH48 — Mumbai–Pune Expressway
        NH_CORRIDORS.put("NH48", new double[][]{
            {19.0760, 72.8777}, {19.0200, 72.9500}, {18.9500, 73.0500},
            {18.8500, 73.2000}, {18.7500, 73.3000}, {18.6500, 73.4000},
            {18.5800, 73.5500}, {18.5200, 73.7000}, {18.5204, 73.8567}
        });
    }

    // Tolerance in km — must be within this distance of a corridor to count as "on" that NH
    private static final double HIGHWAY_TOLERANCE_KM = 5.0;

    /**
     * Returns the NH name the vehicle is currently on (e.g. "NH38"),
     * or "LOCAL_ROAD" if not near any recorded corridor.
     */
    public String detectNH(double lat, double lng) {
        for (Map.Entry<String, double[][]> entry : NH_CORRIDORS.entrySet()) {
            double[][] corridor = entry.getValue();
            for (int i = 0; i < corridor.length - 1; i++) {
                double dist = pointToLineDistanceKm(lat, lng,
                        corridor[i][0], corridor[i][1],
                        corridor[i + 1][0], corridor[i + 1][1]);
                if (dist <= HIGHWAY_TOLERANCE_KM) {
                    return entry.getKey(); // matched
                }
            }
        }
        return "LOCAL_ROAD";
    }

    private double pointToLineDistanceKm(double px, double py,
                                          double x1, double y1,
                                          double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return Math.sqrt(Math.pow(px - x1, 2) + Math.pow(py - y1, 2)) * 111.0;
        }
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;
        return Math.sqrt(Math.pow(px - closestX, 2) + Math.pow(py - closestY, 2)) * 111.0;
    }
}
