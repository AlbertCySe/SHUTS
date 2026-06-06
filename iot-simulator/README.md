# 🛰️ IoT Simulator
### Smart Highway Usage-Based Tolling System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![H2](https://img.shields.io/badge/H2-2.2.224-darkblue.svg)](https://www.h2database.com/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Version](https://img.shields.io/badge/Version-1.1.0-blue.svg)]()
[![Port](https://img.shields.io/badge/Port-8082-purple.svg)]()

> A fully standalone Spring Boot application that simulates an array of real physical GPS tracking devices.
> Runs on **Port 8082** with its own embedded H2 database and a built-in HTML/JS telemetry dashboard.

---

## 📌 What This Is

The IoT Simulator is a **separate, self-contained Spring Boot application** that lives inside the `iot-simulator/` folder. It is not part of the main backend — it is an independent service that mimics what real GPS hardware devices would do in a physical deployment.

On every simulation tick, it:
1. Advances each active vehicle along its assigned GPS route
2. Broadcasts the new coordinates to the main backend via `POST /api/iot/data` on port 8080
3. Saves vehicle state locally so positions are preserved across restarts

> **Think of it as a network of 30 GPS trackers mounted on vehicles, all broadcasting live telemetry to the main server.**

---

## 🔗 How It Fits Into the Main Project

```
IoT Simulator (Port 8082)               Main Backend (Port 8080)
┌─────────────────────────┐             ┌─────────────────────────┐
│  RouteSimulatorService  │────POST ────▶│  /api/iot/data          │
│  (ticks every N ms)     │  GPS ping    │  IoTIdentificationService│
│                         │             │  → Highway detection     │
│  30 vehicles simulated  │             │  → Haversine distance    │
│  OSRM road-snapped routes│            │  → Wallet deduction      │
│  Offline queue on fail  │◀── GET ─────│  → Anomaly detection     │
│                         │  /api/vehicles (on boot)               │
└─────────────────────────┘             └─────────────────────────┘
         │
         ▼ also serves
┌─────────────────────────┐
│  GPS Telemetry Dashboard│
│  http://localhost:8082  │
│  (Vanilla HTML/JS/CSS)  │
└─────────────────────────┘
```

> ⚠️ **Important:** The old README mentioned a React + Leaflet frontend on port 5173. **That has been removed.** The dashboard is now a vanilla HTML/JS/CSS application served directly by Spring Boot from `src/main/resources/static/` on **port 8082**.

---

## 🚀 Quick Start

### One-Click Launch (Windows)
```bash
# From the iot-simulator/ folder:
run-simulator.bat
```

**What happens:**
1. Runs `mvn spring-boot:run` to compile and start the simulator
2. Waits **12 seconds** for Spring Boot to fully initialize
3. Automatically opens `http://localhost:8082` in your default browser

> ℹ️ **The main backend does not need to be running first.** The simulator starts regardless and queues GPS data locally until the main backend comes online. See [Standalone Mode](#-standalone-mode).

### Manual Start
```bash
cd iot-simulator
mvn spring-boot:run
# Dashboard available at: http://localhost:8082
```

---

## 🛠️ Technology Stack

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Language** | Java | 17 LTS | Core simulation engine |
| **Framework** | Spring Boot | 3.2.1 | REST API + scheduling + static file serving |
| **Database** | H2 (embedded file) | 2.2.224 | Local vehicle state persistence — no external DB needed |
| **Routing API** | OSRM | Public API | Real road-snapped GPS route generation |
| **Dashboard** | Vanilla HTML/JS/CSS | — | Telemetry UI served directly by Spring Boot |
| **Build Tool** | Maven | 3.6+ | Dependency management and packaging |

---

## 📂 Complete Project Structure

```
iot-simulator/
│
├── src/main/java/
│   │
│   ├── com/highway/iot/                         ← Core IoT package
│   │   ├── IoTApplication.java                  ← Spring Boot entry point (Port 8082)
│   │   ├── BackupH2.java                        ← Prints H2 backup reminder on shutdown
│   │   │
│   │   ├── controller/
│   │   │   ├── SimulationControlController.java ← Start/Stop all, vehicle list, status
│   │   │   ├── LocationController.java          ← Live GPS positions (used by main frontend)
│   │   │   └── SimulatorSettingsController.java ← Runtime settings adjustment
│   │   │
│   │   ├── model/
│   │   │   ├── VehicleSimulator.java            ← In-memory runtime state for one vehicle
│   │   │   └── SimulatorSettings.java           ← Settings bean (speeds, intervals, probabilities)
│   │   │
│   │   └── service/
│   │       ├── RouteSimulatorService.java        ← ⭐ Main orchestrator: boot sync + tick loop
│   │       ├── IoTBroadcasterService.java        ← POSTs GPS pings to main backend
│   │       ├── ActiveVehicleRegistry.java        ← Thread-safe in-memory vehicle map
│   │       ├── RouteFetchService.java            ← OSRM route fetching + fallback
│   │       ├── NHDetectionService.java           ← Highway boundary detection
│   │       ├── VehicleLifecycleManager.java      ← Start/stop/break logic per vehicle
│   │       ├── SimulatorPersistenceService.java  ← Saves state to H2 + vehicle-state.json
│   │       └── SimulatorSettingsService.java     ← Manages runtime settings
│   │
│   └── com/highway/simulator/                   ← Core mechanics package
│       ├── config/
│       │   └── HighwayConfig.java               ← Highway route definitions (config properties)
│       │
│       ├── model/
│       │   ├── GPSPoint.java                    ← Single latitude/longitude coordinate
│       │   ├── QueuedGPSData.java               ← Payload shape for offline queuing
│       │   ├── Route.java                       ← A named route with list of GPSPoints
│       │   ├── SimulatedVehicle.java            ← Rich vehicle model for simulation logic
│       │   └── VehicleState.java                ← Enum: RUNNING, PARKED, ON_BREAK, TRAFFIC
│       │
│       ├── entity/
│       │   ├── VehicleEntity.java               ← H2 table: simulated_vehicles
│       │   └── VehicleHistory.java              ← H2 table: vehicle_history (trip events)
│       │
│       ├── repository/
│       │   ├── VehicleEntityRepository.java     ← JPA interface for simulated_vehicles
│       │   └── VehicleHistoryRepository.java    ← JPA interface for vehicle_history
│       │
│       └── service/
│           ├── BackendClient.java               ← HTTP client for main backend communication
│           ├── GPSGenerator.java                ← Adds realistic GPS jitter to coordinates
│           ├── MovementSimulator.java           ← Speed physics: acceleration, braking, traffic
│           └── OfflineStorageService.java       ← Queues GPS pings when backend unreachable
│
├── src/main/resources/
│   ├── application.properties                   ← Port, H2 config, backend URL
│   └── static/
│       ├── index.html                           ← Dashboard shell (Leaflet map + vehicle table)
│       ├── dashboard.js                         ← All dashboard logic (polling, map, controls)
│       └── styles.css                           ← Dark-themed dashboard styles
│
├── data/                                        ← Auto-created on first run
│   ├── iot_simulator_db.mv.db                   ← H2 binary database file (~280 KB)
│   ├── iot_simulator_db.trace.db                ← H2 transaction log for crash recovery (~418 KB)
│   └── vehicle-state.json                       ← Vehicle position cache (~2.5 KB)
│
├── pom.xml                                      ← Maven build + dependencies
├── run-simulator.bat                            ← One-click Windows launcher
├── package-lock.json                            ← npm lock (legacy — not actively used)
└── README.md                                    ← This file
```

---

## ⚙️ Configuration

### `src/main/resources/application.properties`

```properties
server.port=8082

# H2 Embedded Database (file-based — persists between restarts)
spring.datasource.url=jdbc:h2:file:./data/iot_simulator_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update

# Main Backend Connection
core.api.url=http://localhost:8080/api/iot/data
core.api.base=http://localhost:8080
```

### H2 Console (Dev Only)
Access the raw H2 database at: `http://localhost:8082/h2-console`
- **JDBC URL:** `jdbc:h2:file:./data/iot_simulator_db`
- **Username:** `sa`
- **Password:** `password`

---

## 🗂️ The `data/` Folder

Three files are automatically created in `iot-simulator/data/` on first run:

| File | Size | Purpose |
|------|------|---------|
| `iot_simulator_db.mv.db` | ~280 KB | Binary H2 database — stores `simulated_vehicles` and `vehicle_history` tables |
| `iot_simulator_db.trace.db` | ~418 KB | H2 transaction log — used for crash recovery, auto-managed |
| `vehicle-state.json` | ~2.5 KB | JSON cache of each vehicle's current route progress |

### What `vehicle-state.json` Does
This file is the key to position persistence. It stores the exact **waypoint index** and **progress percentage** of every actively simulating vehicle. If the simulator crashes or is restarted, vehicles resume from their last known position instead of teleporting back to the route start point.

**Current state:** 30 vehicles cached with `vehicleId`s **3 through 32**.

> **Why does it start at 3?** Vehicle IDs 1 and 2 were deleted during early testing and database seeding. The simulator simply works with whatever IDs exist in the H2 database — there is no hardcoded assumption about starting at 1.

---

## 🚗 How the Simulation Works

### Startup Sequence

```
run-simulator.bat
      ↓
Spring Boot starts on Port 8082
      ↓
RouteSimulatorService @PostConstruct fires:
      ↓
  1. BackendClient.fetchVehicles()
     → GET http://localhost:8080/api/vehicles
     → Syncs H2 simulated_vehicles table with main MySQL DB
      ↓
  2. RouteFetchService assigns routes to each vehicle
     → Tries OSRM API (router.project-osrm.org) for road-snapped routes
     → Falls back to straight-line interpolation if OSRM is unavailable
      ↓
  3. Simulation begins
     → @Scheduled tick fires every N milliseconds (configurable)
     → VehicleLifecycleManager manages RUNNING/PARKED/ON_BREAK transitions
      ↓
Dashboard opens at http://localhost:8082 (after 12s delay in .bat)
```

### Vehicle States (VehicleState enum)

| State | Description | Probability Change |
|-------|-------------|-------------------|
| `RUNNING` | Actively moving along route, broadcasting GPS | — |
| `PARKED` | Stationary — not broadcasting | 10% chance to wake up each tick |
| `ON_BREAK` | Temporarily stopped mid-route | 5% chance to enter; 20% chance to resume |
| `TRAFFIC` | Moving slowly (< 10 km/h) | Configurable probability |

> Vehicles **do not all start RUNNING** — they begin in a staggered mix of states. This is intentional: it makes the simulation realistic rather than having 30 vehicles all move simultaneously from a dead stop.

### Route Fetching (OSRM)

`RouteFetchService` queries the public OSRM API (`router.project-osrm.org`) to get real road-following coordinates for **20 predefined Indian highway routes** (e.g., Chennai → Trichy, Bangalore → Salem). This gives vehicles authentic curved road paths instead of straight diagonal lines.

**Fallback:** If OSRM is rate-limiting your IP or is unreachable, `createWaypointRoute()` generates straight-line interpolated paths between start and end coordinates.

### GPS Broadcasting

`IoTBroadcasterService` sends one POST request per vehicle per tick to:
```
POST http://localhost:8080/api/iot/data
{
  "vehicleId": 5,
  "latitude": 12.9716,
  "longitude": 77.5946,
  "timestamp": "2026-05-01T10:30:00",
  "speedKmH": 85.5,
  "status": "DRIVING",
  "routeName": "NH-44 Bangalore to Salem",
  "isHighway": true
}
```

`GPSGenerator` adds realistic jitter to every coordinate to mimic real GPS device noise.

### Offline Queue (No Main Backend)

If the main backend is unreachable, `OfflineStorageService` intercepts the failed POST and queues the payload locally in the H2 database. When the main backend comes back online, the queue is flushed automatically. **No GPS data is lost.**

---

## 🖥️ The Dashboard (`http://localhost:8082`)

The dashboard is a vanilla HTML/JS/CSS application served by Spring Boot from `src/main/resources/static/`. It has no dependencies on React, Leaflet, or any npm packages.

### What It Shows

| Section | Description |
|---------|-------------|
| **Vehicle Table** | Live list of all 30 vehicles with status badges, current speed, coordinates, and highway detection |
| **Map** | Leaflet.js map (loaded via CDN) with real-time vehicle markers |
| **Start All / Stop All** | Buttons to begin or halt the entire simulation |
| **Settings Drawer** | Live control panel for simulation parameters |
| **Status Bar** | Active vehicle count, backend connection status |

### How `dashboard.js` Works

The dashboard polls `GET /api/simulation/vehicles` every **2 seconds** to refresh the vehicle table and map markers. It does not require a page reload — updates are injected directly into the DOM.

```
dashboard.js (every 2 seconds):
  → GET /api/simulation/vehicles
  → Update vehicle table rows
  → Move Leaflet markers to new coordinates
  → Update status bar counts
```

---

## ⚡ Settings Drawer — Runtime Controls

The Settings drawer lets you tune the simulation **without restarting the Java server**. Changes take effect immediately.

| Setting Category | What You Can Change |
|-----------------|---------------------|
| **Lifecycle** | Probability (%) of a PARKED vehicle waking up each tick |
| **Lifecycle** | Probability of entering a BREAK; probability of resuming |
| **Movement** | Tick interval (ms) — how fast the position math runs |
| **Movement** | Broadcast interval (ms) — how often pings are sent to the backend |
| **Routes** | Toggle OSRM geometry on/off (switch between road-snapped and straight-line) |
| **Routes** | Force all vehicles to a specific named route instead of random |
| **Speed** | Min/max speed per vehicle state (RUNNING, TRAFFIC, ON_BREAK) |
| **Highway Detection** | GPS tolerance radius (km) for "on-highway" detection |

---

## 📡 API Endpoints (Port 8082)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/simulation/start-all` | Start all PARKED vehicles with random routes |
| `POST` | `/api/simulation/stop-all` | Stop all RUNNING vehicles |
| `GET` | `/api/simulation/status` | Active/parked counts and active vehicle ID list |
| `GET` | `/api/simulation/vehicles` | Full vehicle list with live coordinates and status |
| `POST` | `/api/simulation/start/{vehicleId}` | Start one specific vehicle |
| `POST` | `/api/simulation/stop/{vehicleId}` | Stop one specific vehicle |
| `GET` | `/api/simulation/history/{vehicleId}` | Trip event log for a vehicle |
| `GET` | `/api/simulation/settings` | Get current simulation settings |
| `POST` | `/api/simulation/settings` | Update simulation settings at runtime |
| `GET` | `/api/iot/live-locations` | All active vehicle GPS positions (used by main frontend map) |
| `GET` | `/api/iot/live-location` | Single active vehicle GPS position |
| `GET` | `/h2-console` | H2 database browser (dev only) |

> **Note:** Individual start/stop (`/start/{vehicleId}` and `/stop/{vehicleId}`) are available via API but **not exposed in the dashboard UI**. The dashboard only has "Start All" and "Stop All" buttons.

---

## 🔌 Standalone Mode (No Main Backend)

The simulator is designed to run **completely independently** of the main backend.

| Feature | Standalone (Port 8080 offline) |
|---------|-------------------------------|
| Simulation logic | ✅ Fully working |
| Vehicle movement and state machine | ✅ Fully working |
| OSRM route fetching | ✅ Fully working |
| Dashboard map and table | ✅ Fully working |
| GPS broadcasting to main backend | ❌ Fails — but `OfflineStorageService` queues the data |
| Toll deductions | ❌ Not processed until main backend is online |
| Anomaly detection | ❌ Not processed until main backend is online |

**When the main backend comes back online:** The `OfflineStorageService` automatically flushes its queue and delivers all buffered GPS pings. This ensures no telemetry data is permanently lost.

> ⚠️ **Long offline periods:** If the main backend is offline for a very long time, the offline queue in H2 can grow large. This is a known limitation — see [Known Issues](#-known-issues--limitations).

---

## 🐛 Known Issues & Limitations

### 1. H2 "Object Already Closed" Errors on Shutdown
**Symptom:** You see `"The object is already closed [90007-224]"` in the terminal when stopping the simulator.

**Cause:** A harmless race condition between Spring Boot closing the H2 connection pool and the `@Scheduled` background threads (`VehicleLifecycleManager`, `SimulatorPersistenceService`) attempting one final data flush a few milliseconds after shutdown begins.

**Impact:** None. Runtime functionality is completely unaffected. Data is not corrupted.

---

### 2. No Individual Vehicle Control in Dashboard UI
The dashboard only exposes **Start All** and **Stop All** buttons. You cannot manually start or stop a specific vehicle from the browser UI.

**Workaround:** Use the API directly:
```bash
# Start vehicle ID 5:
curl -X POST http://localhost:8082/api/simulation/start/5

# Stop vehicle ID 5:
curl -X POST http://localhost:8082/api/simulation/stop/5
```

---

### 3. OSRM Public API Dependency
The simulator uses the public OSRM demo server (`router.project-osrm.org`). If this server rate-limits your IP or goes down, vehicles fall back to straight-line routes (point-to-point instead of road-following).

**Symptom:** Vehicles appear to drive across fields and through buildings on the map instead of following roads.

**Fix:** Toggle "Use OSRM geometry" off in the Settings drawer — this immediately switches all vehicles to waypoint (straight-line) mode.

---

### 4. Offline Queue Memory Growth
If the main backend is offline for an extended period, `OfflineStorageService` continuously queues GPS payloads in the H2 database. This can cause the `iot_simulator_db.mv.db` file to grow significantly.

**Fix:** Restart the simulator after the main backend comes back online to flush the queue and reset.

---

### 5. Vehicle IDs Start at 3 (IDs 1 and 2 Missing)
During early development, vehicles 1 and 2 were deleted from the H2 database during testing. The simulator works with whatever IDs exist and does not require a contiguous sequence starting at 1. This is cosmetic only.

---

## 🔄 Connection to the Main Project

| Interaction | Direction | Details |
|------------|-----------|---------|
| Vehicle sync on boot | Simulator → Main | `GET /api/vehicles` — populates H2 with current vehicle roster |
| GPS telemetry | Simulator → Main | `POST /api/iot/data` — one call per vehicle per tick |
| Live location data | Main Frontend → Simulator | `GET /api/iot/live-locations` — main React frontend polls this every 3 seconds for `VehicleTrackingModal` |
| Simulation status | Main Frontend → Simulator | `GET /api/simulation/status` — `UserDashboard.jsx` and `Vehicles.jsx` poll every 5 seconds for the IoT connection indicator |

---

## 📊 Current Simulator State

| Metric | Value |
|--------|-------|
| **Version** | 1.1.0 |
| **Active Vehicles** | 30 (IDs 3–32) |
| **Port** | 8082 |
| **Dashboard** | Vanilla HTML/JS/CSS (no React, no Leaflet npm package) |
| **Database** | H2 file-based (persists between restarts) |
| **Routing** | OSRM (road-snapped) with straight-line fallback |
| **Startup Time** | ~12 seconds via `run-simulator.bat` |

---

## 🙏 Part of the Main Project

This simulator is a sub-component of the **Smart Highway Usage-Based Tolling System** — MCA Final Year Project at SRM Institute of Science and Technology, Trichy.

For the full project documentation, see the [main README](../README.md) or the [docs/ folder](../docs/).

---

*IoT Simulator v1.1.0 — Smart Highway Tolling System*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
