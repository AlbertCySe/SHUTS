# 🛰️ IoT Vehicle Simulator (Standalone Tool)

## 📌 Overview
The **IoT Vehicle Simulator** is a specialized companion tool for the Smart Highway Tolling System. Its primary purpose is to mimic real-world GPS hardware by generating continuous geodetic telemetry for a fleet of 32+ vehicles, simulating their movement across the highways of Tamil Nadu, India.

This tool acts as the "Hardware Layer" for testing, allowing developers and evaluators to see the system in action without needing actual GPS devices on the road.

---

## 🏗️ Architecture & Technology Stack

The simulator is built as a complete mini-system with its own backend and frontend:

### ⚙️ Backend (Spring Boot)
- **Port:** 8082
- **Persistence:** H2 In-Memory Database (Seeded with 32 vehicles)
- **Engine Logic:** 
  - **Route Simulator Service**: Orchestrates movement "ticks" every 500ms.
  - **Active Vehicle Registry**: Manages the current state (latitude, longitude, speed).
  - **IoT Broadcaster**: Sends outbound POST requests with GPS data to the Core Backend (Port 8080).
- **Simulated States**: PARKED, RUNNING, TRAFFIC, STOPPED.

### 🌐 Frontend (React + Leaflet)
- **Port:** 5173
- **Map Engine:** Leaflet (OpenStreetMap Dark-Theme Tiles)
- **Features:** 
  - **Global Cluster View**: Visualize all 32+ vehicles moving in real-time.
  - **Satellite Tracking**: Click any vehicle to "Lock" the camera and track its live telemetry.
  - **Real-Time Polling**: 1.5-second refresh interval for smooth visualization.

---

## ⚡ Key Features

1. **Realistic Movement Simulation**: 
   Vehicles don't just "teleport"; they accelerate, cruise, slow down in traffic, and come to complete stops, recalculating their speed every 500ms based on randomized road conditions.
   
2. **Offline Data Queuing**: 
   If the Core Backend (Port 8080) goes offline, the simulator includes a resilient queuing mechanism that stores data points locally and resyncs them as soon as the connection is restored.

3. **Geodetic Accuracy**: 
   Moves vehicle coordinates using bearing and distance logic, ensuring points follow the actual path of Tamil Nadu's national highways (NH-44).

4. **Multi-Vehicle Fleet**: 
   Simulates a realistic "Fleet Management" scenario where dozens of vehicles are hitting different highway segments at once, testing the Core Backend's concurrency.

---

## 🚀 How to Run (Standalone Mode)

To launch the simulator as a separate process from the main system:

1. **Navigate to this folder:**
   ```bash
   cd Initial/iot-simulator
   ```

2. **Run the One-Click Launcher:**
   ```bash
   run-simulator.bat
   ```
   *Note: On the first run, this will automatically install the necessary `@leaflet` dependencies for the Map Dashboard.*

3. **Access the Interfaces:**
   - **Satellite Map Dashboard:** [http://localhost:5173](http://localhost:5173)
   - **IoT Local API:** [http://localhost:8082/api/iot/live-locations](http://localhost:8082/api/iot/live-locations)

---

## 📡 API Reference (Simulator Context)

| Method | Endpoint | Description |
|--------|----------|-------------|
| **GET** | `/api/iot/live-locations` | Returns real-time coordinates/speed for all 32 vehicles |
| **GET** | `/api/iot/live-location` | Returns telemetry for the primary focused vehicle |
| **POST** | `/api/iot/toggle-status/{id}`| Toggle a vehicle between RUNNING and PARKED |

---

## ℹ️ Developer Notes
- **Local Persistence:** On startup, the simulator seeds its local H2 database. You can view the internal simulator database at `http://localhost:8082/h2-console` (JDBC URL: `jdbc:h2:mem:iotdb`).
- **Target Connection:** By default, the simulator assumes the Core Tolling Backend is running at `http://localhost:8080`. This can be adjusted in `application.properties`.

---
**Maintained by:** Albert J  
**Document Version:** 1.1.0 (Physicalized & Integrated Version)
