# ✨ Features Deep Dive
### Smart Highway Usage-Based Tolling System

> **Back to:** [README.md](../README.md) | **Related:** [API Reference](API_REFERENCE.md) · [Database Schema](DATABASE_SCHEMA.md) · [Project Structure](PROJECT_STRUCTURE.md)

---

## Feature 1: Dual-Mode IoT Simulation Engine

### Mode A — Integrated Engine
Runs inside the main Spring Boot application alongside everything else. Simplest mode — no extra setup.

### Mode B — Standalone Simulator (Primary Mode)
A fully separate Spring Boot application (`iot-simulator/`) on Port 8082 with its own embedded H2 database. This is the production-realistic mode.

**Startup sequence:**
```
1. @PostConstruct: initializeAllVehicles() fires on simulator boot
2. fetchVehiclesFromMainBackend() → GET http://localhost:8080/api/vehicles
3. syncLocalDbWithMainBackend() → reconcile H2 simulated_vehicles with MySQL vehicles
4. Each vehicle gets a route assigned via RouteFetchService
5. @Scheduled tick fires every {movementTickIntervalMs} milliseconds
6. simulateMovement() advances every RUNNING vehicle one waypoint
7. IoTBroadcasterService.broadcastLocation() → POST http://localhost:8080/api/iot/data
```

**Class:** `com.highway.iot.service.RouteSimulatorService`

```java
@PostConstruct
public void initializeAllVehicles();

@Scheduled(fixedDelayString = "#{@simulatorSettingsService.settings.movement.movementTickIntervalMs}")
public void simulateMovement();

private List<Map<String, Object>> fetchVehiclesFromMainBackend();
private void syncLocalDbWithMainBackend(List<Map<String, Object>> mainVehicles);
public List<Map<String, Object>> getAllCurrentLocations();
public Map<String, Object> getCurrentLocation();
```

**Dynamic tick interval:** The `@Scheduled` annotation uses a Spring Expression Language (SpEL) expression `#{@simulatorSettingsService.settings.movement.movementTickIntervalMs}` — this means the tick speed can be changed at runtime via the Settings API without restarting the simulator.

**Offline resilience:** `OfflineStorageService` buffers GPS payloads locally if the main backend is unreachable, then flushes them when connectivity is restored. No GPS data is lost during a temporary backend restart.

**Real-road routes:** `RouteFetchService` attempts to use OSRM (Open Source Routing Machine) for real road-snapped waypoints. If OSRM is unreachable, it falls back to straight-line interpolation between start and end coordinates.

> 💡 **Viva Point:** *"The standalone simulator has offline buffering — if the main backend restarts mid-simulation, no GPS data is lost. This mirrors how real IoT hardware handles intermittent connectivity."*

---

## Feature 2: IoT Grid Highway Detection

**Class:** `com.highway.tolling.service.HighwayDetectionService`

```java
// Default tolerance: ±0.05 degrees (~5.5 km buffer)
public boolean isWithinHighwayRange(double vehicleLat, double vehicleLon, Highway highway);

// Custom tolerance override
public boolean isWithinHighwayRange(double vehicleLat, double vehicleLon, Highway highway, double toleranceDegrees);

// Distance to the nearest highway boundary node
public double getDistanceToNearestHighwayPoint(double vehicleLat, double vehicleLon, Highway highway);

// Full detection result with highway object and distance
public HighwayDetectionResult detectHighwayUsage(double vehicleLat, double vehicleLon, Highway highway);
```

**How the bounding box check works:**
```
Given: vehicle at (lat, lon)
Given: highway with (startLat, startLon) and (endLat, endLon)

minLat = min(startLat, endLat) - 0.05
maxLat = max(startLat, endLat) + 0.05
minLon = min(startLon, endLon) - 0.05
maxLon = max(startLon, endLon) + 0.05

if (lat >= minLat && lat <= maxLat && lon >= minLon && lon <= maxLon):
    vehicle IS on this highway → return HighwayDetectionResult(highway, distance)
else:
    vehicle is NOT on this highway
```

The `±0.05°` tolerance buffer (~5.5 km) accounts for vehicles driving near but not exactly on the highway centerline, and for GPS measurement noise.

> 💡 **Viva Point:** *"The ±0.05 degree tolerance was calibrated to avoid false negatives while preventing false positives. A tighter bound would miss vehicles driving along the highway edge; a looser bound would start charging vehicles on parallel roads."*

---

## Feature 3: Haversine Distance Calculation

**Class:** `com.highway.tolling.service.DistanceCalculatorService`

```java
public double calculateDistance(double lat1, double lon1, double lat2, double lon2);
public double calculateDistanceRounded(double lat1, double lon1, double lat2, double lon2, int decimalPlaces);
```

**The formula:**
```
Δlat = lat2 - lat1 (in radians)
Δlon = lon2 - lon1 (in radians)

a = sin²(Δlat/2) + cos(lat1) × cos(lat2) × sin²(Δlon/2)
c = 2 × atan2(√a, √(1-a))
distance = R × c    where R = 6371 km (Earth's mean radius)
```

**Why Haversine and not Euclidean?**
- Euclidean distance: `√((lat2-lat1)² + (lon2-lon1)²)` — treats the Earth as flat
- Haversine: accounts for Earth's spherical curvature
- For a 100 km highway, Euclidean error can be several km. Haversine keeps it under 0.5%
- GPS coordinates are normalized to 6 decimal places (~11 cm precision) before calculation

**Used by:** `HighwayStateProcessor`, `AnomalyDetectionService`, `HighwayDetectionService`

> 💡 **Viva Point:** *"The Haversine Formula gives us the great-circle distance — the shortest path along the Earth's surface. For highway tolling over tens or hundreds of km, this is significantly more accurate than flat-plane Euclidean distance."*

---

## Feature 4: Highway State Processor — The Session State Machine

**Class:** `com.highway.tolling.service.HighwayStateProcessor`

This is the most architecturally important service. It determines what action to take based on the vehicle's previous state and current detection result.

```java
public void processHighwayDetectionAndDistance(
    Long vehicleId, Double lat, Double lon,
    Highway detectedHighway, Long previousHighwayId
);

private void handleSameHighway(Long vehicleId, Double lat, Double lon, Highway highway);
private void handleHighwayEntry(Long vehicleId, Double lat, Double lon, Highway highway);
private void handleHighwayExit(Long vehicleId, Double lat, Double lon, Long previousHighwayId);
private void handleHighwaySwitch(Long vehicleId, Double lat, Double lon, Highway newHighway, Long oldHighwayId);
```

**The four cases:**

| Previous State | Current Detection | Handler | Action |
|---------------|------------------|---------|--------|
| On Highway X | On Highway X | `handleSameHighway` | Calculate Haversine distance → add to session → deduct toll |
| Off highway | On Highway X | `handleHighwayEntry` | Create new `highway_usage` session — set `entry_timestamp`, `entry_lat/lon` |
| On Highway X | Off highway | `handleHighwayExit` | Close session — set `exit_timestamp`, `exit_lat/lon` |
| On Highway X | On Highway Y | `handleHighwaySwitch` | Close X session + open new Y session |

---

## Feature 5: Automated Toll Pipeline

**Class:** `com.highway.tolling.service.IoTIdentificationService`

```java
public IoTDataResponse processIoTData(IoTDataRequest request);
private Highway detectHighway(double lat, double lon);
```

**Complete pipeline for one GPS ping:**
```
1. IoTValidationService.validateVehicleExists(vehicleId)
2. IoTValidationService.parseAndValidateTimestamp(timestampString)
3. IoTValidationService.normalizeCoordinate(lat)  + normalizeCoordinate(lon)
4. HighwayService.getAllHighways() → loop through all registered highways
5. HighwayDetectionService.detectHighwayUsage(lat, lon, highway) → which highway (if any)
6. Build LocationTracking object with all fields
7. HighwayStateProcessor.processHighwayDetectionAndDistance(...)
   → Haversine distance calculated
   → WalletService.deductToll(userId, tollAmount) called inside processor
   → highway_usage session updated
8. LocationTrackingService.saveLocation(locationTracking)
9. AnomalyDetectionService.runAllChecks(locationTracking)  [async]
10. Return IoTDataResponse { success: true, locationId: savedId }
```

**Toll formula:**
```
distance_km = DistanceCalculatorService.calculateDistance(prevLat, prevLon, currLat, currLon)
rate = highway.getRateForVehicleType(vehicleType)   // CAR/BIKE/TRUCK (BUS uses TRUCK rate)
toll = distance_km × rate
WalletService.deductToll(userId, toll)
```

---

## Feature 6: Anomaly Detection Engine

**Class:** `com.highway.tolling.service.AnomalyDetectionService`

```java
public void runAllChecks(LocationTracking currentLocation);
public void detectMissingData(Long vehicleId);
public void detectInactivity(LocationTracking current, LocationTracking previous);
public void detectDisconnection(Long vehicleId, LocationTracking currentLocation);
public void detectRepeatedPatterns(Long vehicleId, AnomalyType anomalyType);
public DataAnomaly flagAnomaly(Long vehicleId, AnomalyType type, String description, AnomalySeverity severity, Long relatedLocationId);
public List<DataAnomaly> getVehicleAnomalies(Long vehicleId);
```

**Detection rules and thresholds:**

| Method | Threshold | Severity | How Detected |
|--------|-----------|---------|-------------|
| `detectMissingData` | 2 hours without a GPS ping | HIGH | Compares `now()` against last ping timestamp |
| `detectInactivity` | < 50m movement in 30 minutes | MEDIUM | Haversine distance between pings; 50m threshold prevents GPS jitter false positives |
| `detectDisconnection` | Session open + no new pings | HIGH | `exit_timestamp IS NULL` + time elapsed > threshold |
| `detectRepeatedPatterns` | Same type 3+ times | HIGH | Counts anomaly records by type for the vehicle |

**The 50m inactivity threshold fix:** GPS devices produce micro-jitter — tiny coordinate changes of 0.001 km even when stationary. Before the fix, this jitter prevented `detectInactivity` from ever firing because the vehicle always appeared to be "moving." The `INACTIVITY_DISTANCE_THRESHOLD_KM = 0.05` constant (50 meters) filters out this noise.

**Automated notification flow:**
```
flagAnomaly() → INSERT data_anomalies
             → INSERT user_notifications (title + description auto-generated)
```
No admin review step. The user is notified directly and immediately.

---

## Feature 7: Smart Request & Admin Approval Workflow

**Class:** `com.highway.tolling.service.VehicleRequestService`

```java
public VehicleRequest submit(VehicleRequest request);
public List<VehicleRequest> getAll();
public List<VehicleRequest> getByUser(Long userId);
public long getPendingCount();
public VehicleRequest approve(Long requestId, String adminNotes);
public VehicleRequest reject(Long requestId, String adminNotes);
```

**What `approve()` executes per request type:**

| Type | Code Executed |
|------|--------------|
| `ADD` | `vehicleRepository.save(new Vehicle(requestedNumber, requestedType, user))` |
| `SELL` | `vehicle.setUser(newOwner)` → `vehicleRepository.save(vehicle)` |
| `SCRAP` | `vehicle.setStatus("SCRAPED")` → `vehicleRepository.save(vehicle)` |
| `DEACTIVATE` | `vehicle.setStatus("INACTIVE")` → `vehicleRepository.save(vehicle)` |
| `MODIFY` | Update `vehicle_number` and/or `vehicle_type` → `vehicleRepository.save(vehicle)` |

After any approval or rejection: `userNotificationRepository.save(new UserNotification(userId, title, message))` — the message includes the admin notes verbatim.

**Profile update approval class:** `ProfileUpdateRequestService` — same pattern, updates `user.name`, `user.email`, and/or `user.phoneNumber` on approval.

---

## Feature 8: Monthly Billing Scheduler

**Class:** `com.highway.tolling.scheduler.MonthlyBillingScheduler`

```java
@Scheduled(cron = "0 0 0 1 * ?")   // Midnight on the 1st of every month
public void generateMonthlyBills();

public int generateBillsForAllVehicles();
public Bill generateBillForUser(Long userId, YearMonth month);
public Bill generateBillForVehicle(Long vehicleId, YearMonth month);
public void triggerBillGeneration();   // Manual trigger from Admin UI
```

**Cron expression breakdown:**
```
"0 0 0 1 * ?"
 │ │ │ │ │ └── Day of week: any
 │ │ │ │ └──── Month: every month
 │ │ │ └────── Day of month: 1st
 │ │ └──────── Hour: 0 (midnight)
 │ └────────── Minute: 0
 └──────────── Second: 0
```

**Duplicate-bill protection:**
```java
// Before generating, always check:
Bill existing = billService.getBillByVehicleAndMonth(vehicleId, month);
if (existing != null) {
    return; // skip — bill already exists for this vehicle + month
}
```

**Daily deduction companion:** `BillDeductionScheduler` runs daily. It finds all `PENDING` bills whose `due_date` has passed and calls `WalletService.deductToll()` for each, then sets `auto_deduct_attempted = true` and `status = PAID` or `OVERDUE`.

---

## Feature 9: Real-Time Notification Bell

**Frontend component:** `components/Header.jsx`

```javascript
// Polling logic (simplified):
useEffect(() => {
  const interval = setInterval(async () => {
    const res = await getRequest(`/notifications/user/${userId}/unread-count`);
    setUnreadCount(res.data.count);
  }, 30000);   // Every 30 seconds
  return () => clearInterval(interval);
}, [userId]);
```

**Full notification path:**
```
Admin clicks "Approve" or "Reject"
    ↓
VehicleRequestService.approve() / reject()
    ↓
UserNotificationRepository.save(notification)
    ↓  (up to 30 seconds later)
Header.jsx polling: GET /api/notifications/user/{id}/unread-count
    ↓
Badge count updates on bell icon
    ↓
User clicks bell → GET /api/notifications/user/{id}
    ↓
Notification list shows, including admin notes in the message
    ↓
PUT /api/notifications/user/{id}/read-all  (on dropdown open)
```

---

## Feature 10: Wallet Recharge System

Users cannot directly add money to their wallet. They submit a `WalletRechargeRequest` that includes the amount and a simulated UPI reference. The Admin reviews and either approves (balance is credited) or declines (no change).

**This follows the same governance pattern** as vehicle lifecycle requests — no user can unilaterally modify their financial balance.

**Models involved:** `WalletRechargeRequest`, `RechargeStatus` enum
**Controllers:** `WalletController` (user-facing), `AdminController` (admin approval)
**Admin endpoint:** `POST /api/admin/wallets/recharge-requests/{id}/{action}` where `action` = `approve` or `decline`

---

## Feature 11: Data Governance Architecture

The entire system enforces a **read-then-request** pattern for users:

| Sensitive Action | Direct Edit Allowed? | Required Path |
|-----------------|---------------------|---------------|
| Add a vehicle | ❌ | `POST /api/vehicle-requests` with `requestType: ADD` |
| Sell a vehicle | ❌ | `POST /api/vehicle-requests` with `requestType: SELL` |
| Scrap a vehicle | ❌ | `POST /api/vehicle-requests` with `requestType: SCRAP` |
| Deactivate a vehicle | ❌ | `POST /api/vehicle-requests` with `requestType: DEACTIVATE` |
| Modify vehicle details | ❌ | `POST /api/vehicle-requests` with `requestType: MODIFY` |
| Change name/email/phone | ❌ | `POST /api/profile-requests` |
| Add wallet balance | ❌ | `POST /api/wallets/user/{id}/recharge-request` |

Every one of these flows through an admin approval queue before any data is actually changed.

> 💡 **Viva Point:** *"I enforced data governance at the service layer, not just the UI layer. Even if someone bypassed the frontend and sent a direct API request, the backend service still routes the change through the approval workflow."*

---

## Feature 12: Optimized Paging & Filtering

**Backend:** Spring Data JPA `Pageable` interface. Repository methods return `Page<T>` with content, total count, and page metadata.

**Frontend hook:** `hooks/usePagination.js`
```javascript
// Returns: { currentPage, totalPages, paginatedData, goToPage }
// Used by: AdminUsersTable, AdminVehiclesTable, TollHistory, BillsTable
```

**Frontend component:** `components/Paginator.jsx`
```jsx
// Props: currentPage, totalPages, onPageChange
// Renders: prev/next buttons + page number buttons
```

**Why it matters:** Without paging, `GET /api/vehicles` could return thousands of records in one response, causing the browser to hang and the network request to timeout. With `?page=0&size=20`, each request returns exactly 20 records regardless of total count.

---

## Feature 13: GitHub Pages Static Deployment

A separate build configuration exists for showcasing the frontend as a static site.

**File:** `frontend/vite.config.github.js`
```javascript
export default defineConfig({
  base: './',                       // Relative paths — required for GitHub Pages subdirectory
  build: {
    outDir: '../docs/main-app'      // Output to docs/ for GitHub Pages to serve
  }
})
```

**Build command:**
```bash
npm run build -- --config vite.config.github.js
```

**SPA routing fix:** A custom `docs/404.html` redirects all unknown paths back to `index.html` via a query string trick, allowing React Router to handle client-side navigation correctly on GitHub Pages.

> This feature has no impact on the local development workflow — it only affects the static showcase build.

---

*Features Deep Dive — Smart Highway Tolling System*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
