# 📁 Project Structure — Complete File Reference
### Smart Highway Usage-Based Tolling System

> **Back to:** [README.md](../README.md) | **Related:** [API Reference](API_REFERENCE.md) · [Database Schema](DATABASE_SCHEMA.md) · [Features](FEATURES.md)

---

## 📌 Overview

The project is two fully independent Spring Boot applications that communicate over HTTP, plus one React frontend.

| Application | Root | Port | Database | Purpose |
|-------------|------|------|----------|---------|
| **Core Backend** | `Initial/src/` | 8080 | MySQL | Users, vehicles, GPS processing, tolling, billing |
| **React Frontend** | `Initial/frontend/` | 3000 | — | User and Admin web dashboards |
| **Standalone IoT Simulator** | `Initial/iot-simulator/` | 8082 | H2 (embedded) | Decoupled GPS simulation, mirrors real hardware |

---

## 📊 Project-Wide Statistics

| Category | Count |
|----------|-------|
| JPA Entity Models | 17 |
| Spring Repositories | 12 |
| Service Classes | 18+ |
| Schedulers | 2 |
| REST Controllers | 14 |
| React Pages | 14 |
| React Components | 15+ |
| CSS Style Files | 9 |
| IoT Simulator Services | 8 |
| Database Tables | 12+ |

---

## 📂 Top-Level Directory Map

```
Initial/                              ← PROJECT ROOT
│
├── src/main/java/com/highway/tolling/ ← Core Java Backend
│   ├── config/                        ← CORS configuration
│   ├── dto/                           ← Request/Response data shapes
│   ├── model/                         ← JPA database entities (17 files)
│   ├── repository/                    ← Spring Data JPA interfaces (12 files)
│   ├── service/                       ← Business logic (18+ files)
│   ├── scheduler/                     ← Automated background tasks (2 files)
│   └── controller/                    ← REST API endpoints (14 files)
│
├── src/main/resources/
│   └── application.properties         ← Backend configuration
│
├── frontend/                          ← React Web Application
│   └── src/
│       ├── services/                  ← API client + auth session
│       ├── hooks/                     ← Custom React hooks
│       ├── pages/                     ← 14 full-page views
│       ├── components/                ← 15+ reusable UI pieces
│       └── styles/                    ← 9 global CSS files
│
├── iot-simulator/                     ← Standalone IoT Simulator
│   └── src/main/java/
│       ├── com/highway/iot/           ← Simulator core
│       └── com/highway/simulator/     ← Simulator support services
│
├── .env                               ← Credentials (never committed)
├── .env.example                       ← Credential template
├── start-project.bat                  ← One-click launcher
├── fix-maven.bat                      ← Maven fixer
├── install-maven-offline.bat          ← Offline Maven installer
└── install-nodejs.bat                 ← Node.js installer
```

---

## 🔵 SECTION 1: Core Backend — Entry Point & Configuration

### `TollingSystemApplication.java`
- **Purpose:** The single entry point that starts the entire main backend. Calls `SpringApplication.run()` which bootstraps Spring Boot on port 8080. The `@EnableScheduling` annotation activates the monthly billing and daily deduction schedulers.
- **Key method:** `main(String[] args)`
- **Dependencies:** None — this IS the root.
- **If deleted:** The entire backend stops working.

---

### `config/CorsConfig.java`
- **Purpose:** Allows the React frontend (port 3000) and the IoT Simulator dashboard (port 8082) to call the backend APIs without the browser blocking them with CORS errors. Without this, every API call from the frontend would be blocked by the browser's same-origin policy.
- **Key method:** `corsConfigurer()` — permits all origins, all methods, all headers on the `/api/**` path.
- **If deleted:** Every API call from the frontend returns a CORS error in the browser. The application becomes completely unusable from a browser.

---

## 🔵 SECTION 2: Core Backend — DTOs (Data Transfer Objects)

DTOs define the exact shape of JSON data coming into and going out of the API. They are separate from the database models so that the API contract can evolve independently of the database schema.

### `dto/IoTDataRequest.java`
- **Purpose:** Defines the JSON body that the IoT Simulator must send when posting a GPS ping to `/api/iot/data`. Uses `@Valid` Jakarta annotations so bad or missing data is rejected immediately before reaching service code.
- **Fields:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `vehicleId` | Long | ✅ `@NotNull` | ID of the vehicle sending data |
| `latitude` | Double | ✅ `@DecimalMin(-90)` | GPS latitude |
| `longitude` | Double | ✅ `@DecimalMin(-180)` | GPS longitude |
| `timestamp` | String | ✅ `@NotNull` | ISO-8601 datetime string |
| `speedKmH` | Double | Optional | Current vehicle speed |
| `status` | String | Optional | Vehicle movement status |
| `routeName` | String | Optional | Name of the current route |
| `isHighway` | Boolean | Optional | Whether simulator considers this on a highway |

- **Used by:** `IoTController`, `IoTIdentificationService`
- **If deleted:** The `/api/iot/data` endpoint loses its input model — the controller cannot parse incoming GPS payloads.

---

### `dto/IoTDataResponse.java`
- **Purpose:** The JSON response sent back to the simulator after each GPS ping is processed. Tells the caller whether the ping was accepted successfully, and provides the saved location's ID for traceability.
- **Fields:** `success` (Boolean), `message` (String), `locationId` (Long — present on success only)
- **Constructors:** Two — one for success (includes `locationId`), one for errors (no `locationId`).
- **Used by:** `IoTController`

---

## 🔵 SECTION 3: Core Backend — JPA Entity Models (Database Tables)

Each model class maps to one MySQL table. Spring Data JPA reads these class definitions to create and maintain the database schema automatically.

### `model/User.java` → table: `users`

| Field | Type | Description |
|-------|------|-------------|
| `userId` | Long (PK) | Auto-generated unique ID |
| `name` | String | Full name |
| `email` | String (unique) | Login email |
| `phoneNumber` | String | 10-digit phone (also used for login) |
| `role` | Enum | `USER` or `ADMIN` |
| `createdAt` | LocalDateTime | Account creation timestamp |

- **Used by:** `Wallet`, `Vehicle`, `Bill`, `VehicleRequest`, `UserNotification`, `WalletRechargeRequest`, `UserService`, `AuthController`
- **Design note:** The `role` field enables the distinction between regular users and admins at the data level.

---

### `model/Vehicle.java` → table: `vehicles`

| Field | Type | Description |
|-------|------|-------------|
| `vehicleId` | Long (PK) | Auto-generated unique ID |
| `vehicleNumber` | String (unique) | License plate number |
| `vehicleType` | Enum (VehicleType) | CAR / BIKE / BUS / TRUCK |
| `status` | String | `ACTIVE`, `INACTIVE`, or `SCRAPED` |
| `user` | FK → User | Owner relationship |
| `createdAt` | LocalDateTime | Registration timestamp |

- **Used by:** `HighwayUsage`, `LocationTracking`, `VehicleService`, `AdminService`, `VehicleRequestService`

---

### `model/VehicleType.java` — Enum
- **Values:** `CAR`, `BIKE`, `BUS`, `TRUCK`
- **Purpose:** Used in toll rate lookups — each highway stores a separate rate for each vehicle type.
- **Used by:** `Vehicle`, `Highway`, `HighwayUsageService`

---

### `model/Wallet.java` → table: `wallets`

| Field | Type | Description |
|-------|------|-------------|
| `walletId` | Long (PK) | Auto-generated |
| `user` | FK → User | One wallet per user |
| `balance` | Double | Current balance in ₹ |
| `minimumBalance` | Double | Threshold — wallet enters deficit if below this |

- **Key methods:** `isInDeficit()` — returns true if `balance < minimumBalance`. `getAvailableBalance()` — returns spendable amount.
- **Design note:** The system allows negative balances (deficit mode) — vehicles can continue using highways even if the wallet dips below minimum. The deficit is recovered during monthly billing.

---

### `model/WalletRechargeRequest.java` → table: `wallet_recharge_requests`
- **Purpose:** When a user wants to top up their wallet, they submit a recharge request. It goes to the admin queue for approval (similar to the vehicle lifecycle approval pattern). The admin then approves or declines it, and the balance is updated accordingly.

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long (PK) | Auto-generated |
| `user` | FK → User | The user requesting the top-up |
| `amount` | Double | Requested recharge amount in ₹ |
| `status` | Enum (RechargeStatus) | `PENDING`, `APPROVED`, or `REJECTED` |
| `requestDate` | LocalDateTime | When the request was submitted |
| `processedDate` | LocalDateTime | When admin took action (null if still pending) |

- **Used by:** `AdminController`, `WalletController`

---

### `model/RechargeStatus.java` — Enum
- **Values:** `PENDING`, `APPROVED`, `REJECTED`
- **Used by:** `WalletRechargeRequest`, `AdminController`

---

### `model/Highway.java` → table: `highways`

| Field | Type | Description |
|-------|------|-------------|
| `highwayId` | Long (PK) | Auto-generated |
| `highwayName` | String | Display name (e.g. "NH-44") |
| `startLatitude` | Double | GPS boundary start point |
| `startLongitude` | Double | GPS boundary start point |
| `endLatitude` | Double | GPS boundary end point |
| `endLongitude` | Double | GPS boundary end point |
| `ratePerKmCar` | Double | Toll rate per km for CAR (₹) |
| `ratePerKmBike` | Double | Toll rate per km for BIKE (₹) |
| `ratePerKmBus` | Double | Toll rate per km for BUS (₹) |
| `ratePerKmTruck` | Double | Toll rate per km for TRUCK (₹) |

- **Used by:** `HighwayDetectionService`, `HighwayStateProcessor`, `HighwayUsageService`, `HighwayController`

---

### `model/HighwayUsage.java` → table: `highway_usage`

| Field | Type | Description |
|-------|------|-------------|
| `usageId` | Long (PK) | Auto-generated |
| `vehicleId` | Long (FK) | Which vehicle |
| `highwayId` | Long (FK) | Which highway |
| `entryTime` | LocalDateTime | When the vehicle entered the highway |
| `exitTime` | LocalDateTime | When it exited (null = session still open) |
| `entryLat`, `entryLon` | Double | GPS at entry point |
| `exitLat`, `exitLon` | Double | GPS at exit point (null = session open) |
| `distanceTravelled` | Double | Cumulative km for this session |
| `sessionClosed` | Boolean | False = vehicle still on highway |

- **Used by:** `HighwayUsageService`, `HighwayStateProcessor`, `BillDeductionScheduler`, `MonthlyBillingScheduler`

---

### `model/LocationTracking.java` → table: `location_tracking`

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long (PK) | Auto-generated |
| `vehicleId` | Long (FK) | Which vehicle |
| `latitude`, `longitude` | Double | GPS coordinates (6 decimal places) |
| `timestamp` | LocalDateTime | When the ping was recorded |
| `speedKmH` | Double | Vehicle speed at time of ping |
| `status` | String | Movement status from simulator |
| `routeName` | String | Named route from simulator |
| `isOnHighway` | Boolean | True if inside a highway boundary |
| `highwayId` | Long (nullable) | Which highway, if on one |
| `distanceFromPrevious` | Double | Haversine distance from the last GPS ping |

- **Used by:** `IoTIdentificationService`, `LocationTrackingService`, `AnomalyDetectionService`, `VehicleTrackingModal.jsx`

---

### `model/Bill.java` → table: `bills`

| Field | Type | Description |
|-------|------|-------------|
| `billId` | Long (PK) | Auto-generated |
| `userId` | Long (FK) | Which user |
| `vehicleId` | Long (nullable FK) | Which vehicle (null = consolidated user-level bill) |
| `totalDistance` | Double | Total km driven on highways that month |
| `totalAmount` | Double | Total toll amount for the month |
| `billMonth` | String | Format: `YYYY-MM` (e.g. `2026-04`) |
| `dueDate` | LocalDate | When payment is due |
| `status` | Enum (BillStatus) | `PENDING`, `PAID`, or `OVERDUE` |
| `createdAt` | LocalDateTime | When the bill was generated |

---

### `model/BillStatus.java` — Enum
- **Values:** `PENDING`, `PAID`, `OVERDUE`
- **Used by:** `Bill`, `BillDeductionScheduler`

---

### `model/DataAnomaly.java` → table: `data_anomalies`

| Field | Type | Description |
|-------|------|-------------|
| `anomalyId` | Long (PK) | Auto-generated |
| `vehicleId` | Long (FK) | Which vehicle triggered the anomaly |
| `anomalyType` | Enum (AnomalyType) | Category of the detected problem |
| `description` | String | Human-readable explanation |
| `severity` | Enum (AnomalySeverity) | How serious it is |
| `detectedAt` | LocalDateTime | When the anomaly was detected |
| `relatedLocationId` | Long (nullable) | Links to the GPS ping that triggered it |

---

### `model/AnomalyType.java` — Enum
- **Values:** `MISSING_DATA`, `SUDDEN_DISCONNECTION`, `INACTIVITY_ON_HIGHWAY`, `REPEATED_PATTERN`

---

### `model/AnomalySeverity.java` — Enum
- **Values:** `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`

---

### `model/ProfileUpdateRequest.java` → table: `profile_update_requests`

| Field | Type | Description |
|-------|------|-------------|
| `requestId` | Long (PK) | Auto-generated |
| `userId` | Long (FK) | The user requesting the change |
| `requestedName` | String (nullable) | New name, if changing |
| `requestedPhone` | String (nullable) | New phone, if changing |
| `status` | String | `PENDING`, `APPROVED`, `REJECTED` |
| `adminNotes` | String (nullable) | Admin's notes when resolving |
| `createdAt` | LocalDateTime | When submitted |
| `reviewedAt` | LocalDateTime (nullable) | When admin resolved it |

> **Note:** Profile requests cover name and phone only. Email changes are handled differently.

---

### `model/VehicleRequest.java` → table: `vehicle_requests`

| Field | Type | Description |
|-------|------|-------------|
| `requestId` | Long (PK) | Auto-generated |
| `userId` | Long (FK) | User submitting the request |
| `vehicleId` | Long (nullable FK) | Vehicle being changed (null for ADD) |
| `requestType` | Enum | `ADD`, `SELL`, `SCRAP`, `DEACTIVATE`, `MODIFY` |
| `requestedVehicleNumber` | String (nullable) | For ADD/MODIFY — the plate number |
| `requestedVehicleType` | Enum (nullable) | For ADD/MODIFY — the vehicle type |
| `newOwnerUserId` | Long (nullable FK) | For SELL — the target owner |
| `reason` | String (nullable) | User's explanation |
| `status` | String | `PENDING`, `APPROVED`, `REJECTED` |
| `adminNotes` | String (nullable) | Admin's notes when resolving |
| `createdAt` | LocalDateTime | When submitted |
| `reviewedAt` | LocalDateTime (nullable) | When admin resolved it |

---

### `model/UserNotification.java` → table: `user_notifications`

| Field | Type | Description |
|-------|------|-------------|
| `notificationId` | Long (PK) | Auto-generated |
| `userId` | Long (FK) | Recipient user |
| `title` | String | Short notification title |
| `message` | String | Full alert text (includes admin notes) |
| `isRead` | Boolean | False until user opens the notification bell |
| `createdAt` | LocalDateTime | When the notification was created |

---

## 🔵 SECTION 4: Core Backend — Repositories

Each repository is a Spring Data JPA interface. Spring auto-generates the SQL implementation. Listed below are only the **custom query methods** — standard `findAll()`, `findById()`, `save()`, `delete()` are available on all of them automatically.

| Repository | Custom Queries |
|-----------|---------------|
| `UserRepository` | `findByEmail()`, `findByPhoneNumber()`, `findByEmailAndPhoneNumber()` |
| `VehicleRepository` | `findByUser_UserId()`, `findByVehicleNumber()` |
| `WalletRepository` | `findByUser_UserId()`, `existsByUser_UserId()`, `findWalletsWithNegativeBalance()` |
| `WalletRechargeRequestRepository` | `findByStatusOrderByRequestDateAsc()` |
| `HighwayRepository` | Standard only — `findAll()`, `findById()` |
| `HighwayUsageRepository` | Open session queries, monthly distance sums, distinct vehicle IDs, per-vehicle monthly totals |
| `LocationTrackingRepository` | `findByVehicleIdOrderByTimestampDesc()` — most recent pings first |
| `BillRepository` | Find by user+month, find by vehicle+month, find recent bills |
| `DataAnomalyRepository` | Find by vehicle, count anomalies by type since a given date (for repeat-pattern detection) |
| `ProfileUpdateRequestRepository` | Find pending requests by user |
| `VehicleRequestRepository` | `findAllByOrderByCreatedAtDesc()`, `findByUserIdOrderByCreatedAtDesc()`, `countByStatus()` |
| `UserNotificationRepository` | Find unread notifications for a user, count unread |

---

## 🔵 SECTION 5: Core Backend — Services

Services contain all business logic. Controllers call services; services use repositories. No database access should happen directly in controllers.

### `UserService.java`
- **Purpose:** Standard CRUD operations for user accounts.
- **Methods:** `getAllUsers()`, `getUserById()`, `createUser()`, `updateUser()`
- **Used by:** `AuthController`, `UserController`, `MonthlyBillingScheduler`, `VehicleRequestService`

---

### `VehicleService.java`
- **Purpose:** Standard CRUD for vehicles. Note: lifecycle changes (add/sell/scrap) go through `VehicleRequestService`, not here.
- **Methods:** `getVehiclesByUserId()`, `getVehicleById()`, `getAllVehicles()`, `createVehicle()`, `updateVehicle()`
- **Used by:** `VehicleController`, `AdminController`, `MonthlyBillingScheduler`, `VehicleRequestService`

---

### `HighwayService.java`
- **Purpose:** CRUD for highway toll zone definitions.
- **Methods:** `getAllHighways()`, `getHighwayById()`, `createHighway()`, `updateHighway()`, `deleteHighway()`
- **Used by:** `HighwayController`, `IoTIdentificationService`, `HighwayStateProcessor`

---

### `LocationTrackingService.java`
- **Purpose:** Saves GPS pings to the database and retrieves location history for a vehicle.
- **Methods:** `saveLocation()`, `getLocationsByVehicleId()`, `getLatestLocationForVehicle()`
- **Used by:** `IoTIdentificationService`, `AnomalyDetectionService`, `LocationTrackingController`

---

### `DistanceCalculatorService.java`
- **Purpose:** Pure mathematical service implementing the **Haversine Formula** for geodetic distance calculation. Has no database access — it is purely a math utility.
- **Methods:**
  - `calculateDistance(lat1, lon1, lat2, lon2)` → returns distance in km as a Double
  - `calculateDistanceRounded(lat1, lon1, lat2, lon2, decimalPlaces)` → returns rounded result
- **Used by:** `HighwayStateProcessor`, `AnomalyDetectionService`, `HighwayDetectionService`
- **If deleted:** All distance calculations break. No toll can be computed.

---

### `HighwayDetectionService.java`
- **Purpose:** Given a vehicle's GPS coordinate, checks whether it falls inside a highway's bounding box (with a ±0.05° tolerance buffer). Returns a `HighwayDetectionResult` inner object with the detected highway (if any) and distance to the nearest boundary.
- **Methods:**
  - `isWithinHighwayRange(lat, lon, highway)` — two overloads
  - `getDistanceToNearestHighwayPoint(lat, lon, highway)`
  - `detectHighwayUsage(lat, lon)` → returns `HighwayDetectionResult`
- **Used by:** `IoTIdentificationService`

---

### `HighwayStateProcessor.java`
- **Purpose:** The core **state machine** for highway trip events. Receives the detection result and decides what action to take based on which state the vehicle is transitioning between.
- **The 4 cases it handles:**

| Case | Condition | Action |
|------|-----------|--------|
| Same highway | Vehicle was on highway X, still on highway X | Add distance to existing session |
| Entry | Vehicle was off highway, now on highway X | Create new session (set entry time + coordinates) |
| Exit | Vehicle was on highway X, now off-highway | Close session (set exit time + coordinates) |
| Switch | Vehicle was on highway X, now on highway Y | Close session for X, open new session for Y |

- **Methods:** `processHighwayDetectionAndDistance()`, `handleSameHighway()`, `handleHighwayEntry()`, `handleHighwayExit()`, `handleHighwaySwitch()`
- **Used by:** `IoTIdentificationService`

---

### `IoTIdentificationService.java`
- **Purpose:** The **main GPS pipeline orchestrator**. This is the most important service in the backend. Every GPS ping flows through this service.
- **What it does in order:**
  1. Validates the vehicle exists (via `IoTValidationService`)
  2. Parses the timestamp string into a `LocalDateTime`
  3. Normalizes GPS coordinates to 6 decimal places
  4. Fetches all registered highways
  5. Calls `HighwayDetectionService` to determine if the vehicle is on a highway
  6. Builds a `LocationTracking` object with all the data
  7. Calls `HighwayStateProcessor` to handle the session state change
  8. Saves the location to the database
  9. Triggers `AnomalyDetectionService` checks
  10. Returns an `IoTDataResponse`
- **Methods:** `processIoTData(IoTDataRequest)`, `detectHighway(lat, lon)`
- **Used by:** `IoTController` only

---

### `IoTValidationService.java`
- **Purpose:** Input validation helpers called before any GPS processing begins. Ensures data quality at the entry point.
- **Methods:** `validateVehicleExists(vehicleId)`, `parseAndValidateTimestamp(timestampString)`, `normalizeCoordinate(coordinate)`
- **Used by:** `IoTIdentificationService`

---

### `HighwayUsageService.java`
- **Purpose:** Manages highway trip sessions — creates them on vehicle entry, accumulates distance during the trip, and closes them on exit. Also provides monthly aggregation queries for billing.
- **Methods:** `createHighwaySession()`, `getActiveSession(vehicleId, highwayId)`, `addDistanceToSession()`, `closeSession()`, `getMonthlyDistanceForUser()`, `getMonthlyDistanceForVehicle()`, `getDistinctVehicleIdsWithUsage()`
- **Used by:** `HighwayStateProcessor`, `IoTIdentificationService`, `MonthlyBillingScheduler`, `BillDeductionScheduler`

---

### `HighwayUsageAggregationService.java`
- **Purpose:** Calculates the total monetary toll owed for a specific vehicle or user for a given billing month. Does this by looking up the highway-specific rate for the vehicle's type and multiplying by the distance driven on that highway.
- **Methods:** `calculateTollForMonth(vehicleId, month)`, `calculateTotalUserTollForMonth(userId, month)`
- **Used by:** `MonthlyBillingScheduler`

---

### `AnomalyDetectionService.java`
- **Purpose:** After each GPS ping is saved, runs automated fraud/anomaly checks. Persists findings to `data_anomalies` table.
- **Detection checks:**

| Method | What It Checks | Threshold |
|--------|---------------|-----------|
| `detectMissingData()` | No GPS ping received for a vehicle | 2+ hours |
| `detectInactivity()` | Vehicle on highway but not moving | 30+ minutes |
| `detectDisconnection()` | Abrupt loss of signal mid-session | Session open but no new pings |
| `detectRepeatedPatterns()` | Same anomaly type detected repeatedly | 3+ times for same vehicle |

- **Key methods:** `runAllChecks(vehicleId, locationId)`, `flagAnomaly(vehicleId, type, description, severity)`, `getVehicleAnomalies(vehicleId)`
- **Used by:** `IoTIdentificationService`

---

### `WalletService.java`
- **Purpose:** All wallet money operations. The service allows negative balances (deficit mode) by design — this means vehicles can continue using highways even if the wallet goes below minimum. The inner class `WalletDeductionResult` carries before/after state of each deduction for audit purposes.
- **Methods:** `createWallet()`, `getWalletById()`, `getWalletByUserId()`, `addBalance(userId, amount)`, `deductToll(userId, amount)`, `isWalletInDeficit(userId)`, `getDeficitAmount(userId)`, `seedWallets()` (bulk wallet creation for all users)
- **Used by:** `AdminController`, `WalletController`, `BillDeductionScheduler`, `HighwayUsageService`

---

### `BillService.java`
- **Purpose:** Creates and retrieves bill records. Supports both user-level consolidated bills and per-vehicle bills.
- **Methods:** `createBill(userId, month, distance, amount)` (consolidated), `createBill(vehicleId, month, distance, amount)` (per-vehicle), `getBillByUserAndMonth()`, `getBillByVehicleAndMonth()`, `getBillsByUser(userId)`
- **Used by:** `MonthlyBillingScheduler`, `BillController`

---

### `EmailService.java`
- **Purpose:** Sends bill notification emails to users via Spring Mail (configured via SMTP/Gmail credentials in `.env`). Gracefully does nothing if email is disabled in config — so the app works even without SMTP setup.
- **Methods:** `sendBillEmail(User user, Bill bill)`, `sendEmail(to, subject, body)`
- **Used by:** `MonthlyBillingScheduler`

---

### `AdminService.java`
- **Purpose:** Admin-specific data aggregation. Provides system-wide statistics, joined vehicle+user data, negative wallet detection, and test data seeding.
- **Methods:** `getAllVehicles()`, `getWalletsWithNegativeBalance()`, `getSystemStats()` (returns `AdminStats` inner class), `getRecentBills()`, `populateSampleUsage()`
- **Used by:** `AdminController`

---

### `VehicleRequestService.java`
- **Purpose:** The full lifecycle request approval workflow. Submitting a request stores it as PENDING. Approving executes the actual change. Rejecting notifies the user without changing data.
- **What `approve()` does per type:**

| Request Type | Action on Approval |
|-------------|-------------------|
| `ADD` | Creates a new `Vehicle` record in the database |
| `SELL` | Updates the vehicle's `user` FK to the new owner |
| `SCRAP` | Sets vehicle `status` to `SCRAPED` — GPS processing disabled |
| `DEACTIVATE` | Sets vehicle `status` to `INACTIVE` |
| `MODIFY` | Updates vehicle number/type with requested values |

- **Methods:** `submit()`, `getAll()`, `getByUser(userId)`, `getPendingCount()`, `approve(requestId, adminNotes)`, `reject(requestId, adminNotes)`
- **Used by:** `VehicleRequestController`

---

### `ProfileUpdateRequestService.java`
- **Purpose:** Same approval-queue pattern as `VehicleRequestService`, but for user profile changes (name and phone number). Admin approves → user's `name` and/or `phoneNumber` are updated.
- **Methods:** `submit()`, `getAll()`, `getByUser(userId)`, `approve(requestId, adminNotes)`, `reject(requestId, adminNotes)`
- **Used by:** `ProfileUpdateRequestController`

---

## 🔵 SECTION 6: Core Backend — Schedulers

### `scheduler/MonthlyBillingScheduler.java`
- **Purpose:** Automatically runs on the 1st of every month at midnight. Generates one consolidated bill per user for the previous month based on total distance × highway rates. Sends an email notification. Also exposes manual trigger endpoints for admin use.
- **Schedule:** `@Scheduled(cron = "0 0 0 1 * ?")` — midnight on the 1st of every month
- **Methods:**
  - `generateMonthlyBills()` — the scheduled main method
  - `generateBillForUser(userId, month)` — single user bill
  - `generateBillForVehicle(vehicleId, month)` — single vehicle bill
  - `generateBillsForAllVehicles(month)` — all vehicles
  - `triggerBillGeneration()` — admin manual trigger
- **Used by:** `AdminController` (for manual triggers)
- **If deleted:** Monthly bills are never generated automatically.

---

### `scheduler/BillDeductionScheduler.java`
- **Purpose:** Runs daily. Scans for all `PENDING` bills whose `dueDate` has passed. For each overdue bill, it deducts the amount from the user's wallet via `WalletService` and updates the bill status to `PAID` (if successful) or `OVERDUE` (if wallet insufficient).
- **Schedule:** Daily (exact cron expression to confirm via Antigravity Prompt #2)
- **Methods:** `processDueBills()` — the scheduled main method
- **If deleted:** Overdue bills are never collected — users can accumulate indefinitely unpaid tolls.

---

## 🔵 SECTION 7: Core Backend — Controllers (REST API Endpoints)

### `controller/AuthController.java`
- **Purpose:** Handles user login and logout. Login checks the submitted `email` + `phoneNumber` combination against the `users` table. Admin login uses hardcoded credentials stored in `application.properties` (loaded via `${ADMIN_EMAIL}` and `${ADMIN_PASSWORD}` environment variables from `.env`). Returns user session data (userId, name, role) on success.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/auth/login` | Login with email + phone + role |
| `POST` | `/api/auth/logout` | Clear session |

- **Used by:** `frontend/src/services/auth.js`

---

### `controller/UserController.java`
- **Purpose:** Full CRUD for user records.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/{id}` | Get single user |
| `POST` | `/api/users` | Create new user |
| `PUT` | `/api/users/{id}` | Update user |
| `DELETE` | `/api/users/{id}` | Delete user |
| `GET` | `/api/users/{id}/vehicles` | Get all vehicles for a user |

---

### `controller/VehicleController.java`
- **Purpose:** Vehicle CRUD. Note: destructive lifecycle changes (add/sell/scrap) go through `VehicleRequestController` for admin approval — not here.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/vehicles` | All vehicles (paginated) |
| `GET` | `/api/vehicles/{id}` | Single vehicle |
| `GET` | `/api/vehicles/user/{userId}` | Vehicles by user |
| `POST` | `/api/vehicles` | Create vehicle (admin bypass) |
| `PUT` | `/api/vehicles/{id}` | Update vehicle |
| `PATCH` | `/api/vehicles/{id}/toggle-status` | Toggle ACTIVE/INACTIVE |

---

### `controller/VehicleRequestController.java`
- **Purpose:** The governance approval queue for vehicle lifecycle changes.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/vehicle-requests` | Submit a lifecycle request |
| `GET` | `/api/vehicle-requests` | All requests (admin view) |
| `GET` | `/api/vehicle-requests/user/{userId}` | Requests by user |
| `GET` | `/api/vehicle-requests/pending-count` | Count of pending requests |
| `POST` | `/api/vehicle-requests/{id}/approve` | Admin approves |
| `POST` | `/api/vehicle-requests/{id}/reject` | Admin rejects |

---

### `controller/IoTController.java`
- **Purpose:** The single entry point for all GPS data. Validates the payload with `@Valid`, passes it to `IoTIdentificationService`, returns a structured response. Every GPS ping from the simulator hits this endpoint.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/iot/data` | Receive a GPS ping from a vehicle |

- **Used by:** IoT Simulator's `IoTBroadcasterService` (Port 8082 → Port 8080)

---

### `controller/HighwayController.java`
- **Purpose:** CRUD for highway toll zone definitions.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/highways` | All highways |
| `GET` | `/api/highways/{id}` | Single highway |
| `POST` | `/api/highways` | Create highway |
| `PUT` | `/api/highways/{id}` | Update highway |
| `DELETE` | `/api/highways/{id}` | Delete highway |

---

### `controller/HighwayUsageController.java`
- **Purpose:** Read-only views of highway trip records.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/highway-usage/vehicle/{vehicleId}` | All sessions for a vehicle |
| `GET` | `/api/highway-usage/summary/{vehicleId}` | Summarized usage for a vehicle |
| `GET` | `/api/highway-usage/user/{userId}` | All sessions for a user |

---

### `controller/LocationTrackingController.java`
- **Purpose:** Returns GPS coordinate history for vehicles. Used by the frontend's live tracking map.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/locations/vehicle/{vehicleId}` | All GPS pings for a vehicle |
| `GET` | `/api/locations/vehicle/{vehicleId}/latest` | Most recent GPS ping |
| `GET` | `/api/locations/vehicle/{vehicleId}/history` | Full GPS history |

---

### `controller/WalletController.java`
- **Purpose:** User-facing wallet operations — view balance, request a top-up, view recharge history.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/wallets/user/{userId}` | Get wallet balance |
| `POST` | `/api/wallets/request-recharge` | Submit a top-up request |
| `GET` | `/api/wallets/user/{userId}/recharge-history` | View past recharge requests |

---

### `controller/BillController.java`
- **Purpose:** Returns bills for a user or vehicle.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/bills/user/{userId}` | All bills for a user |
| `GET` | `/api/bills/vehicle/{vehicleId}` | Bills for a vehicle |
| `GET` | `/api/bills/{billId}` | Single bill detail |

---

### `controller/UserNotificationController.java`
- **Purpose:** In-app notification bell management.

> ⚠️ **Correct base path is `/api/notifications` — NOT `/api/user-notifications`.**

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/notifications/user/{userId}` | All notifications for a user |
| `GET` | `/api/notifications/user/{userId}/unread-count` | Count of unread alerts |
| `PATCH` | `/api/notifications/{id}/read` | Mark a single notification as read |
| `DELETE` | `/api/notifications/user/{userId}/clear` | Clear all notifications for a user |

- **Frontend polling:** `Header.jsx` calls the `unread-count` endpoint every 30 seconds.

---

### `controller/ProfileUpdateRequestController.java`
- **Purpose:** Admin-approved profile change requests.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/profile-requests` | Submit a profile change request |
| `GET` | `/api/profile-requests` | All requests (admin view) |
| `GET` | `/api/profile-requests/user/{userId}` | Requests by user |
| `POST` | `/api/profile-requests/{id}/approve` | Admin approves |
| `POST` | `/api/profile-requests/{id}/reject` | Admin rejects |

---

### `controller/AdminController.java`
- **Purpose:** Admin-only power operations — system stats, manual billing triggers, wallet management, test data seeding.
- **Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/admin/vehicles` | All vehicles with user info joined |
| `GET` | `/api/admin/stats` | System-wide statistics |
| `GET` | `/api/admin/wallets/negative` | Wallets in deficit |
| `POST` | `/api/admin/seed-wallets` | Create wallets for all users |
| `POST` | `/api/admin/populate-usage` | Seed test highway usage data |
| `POST` | `/api/admin/generate-bills` | Trigger monthly bill generation for all |
| `POST` | `/api/admin/generate-bill/user/{userId}` | Bill for one user |
| `POST` | `/api/admin/generate-bill/vehicle/{vehicleId}` | Bill for one vehicle |
| `POST` | `/api/admin/generate-all-vehicle-bills` | Bill per vehicle for all |
| `GET` | `/api/admin/bills/recent` | Most recently generated bills |
| `GET` | `/api/admin/wallets/recharge-requests` | All pending recharge requests |
| `POST` | `/api/admin/wallets/recharge-requests/{id}/{action}` | Approve or reject recharge |
| `POST` | `/api/admin/wallets/user/{userId}/topup` | Manual wallet top-up |

---

### `controller/DbExplorerController.java`
- **Purpose:** Development-only tool. Returns raw table contents as JSON for the in-app database viewer page. Should be disabled or removed before any production deployment.
- **Endpoints:** `GET /api/db-explorer/{tableName}`
- **Used by:** `frontend/src/pages/DbViewer.jsx`

---

## 🟠 SECTION 8: IoT Simulator Backend

### Entry Points

**`com/highway/iot/IoTApplication.java`**
- Starts the Standalone Simulator on port 8082 with scheduling enabled.

**`com/highway/iot/BackupH2.java`**
- Prints a reminder to back up the H2 database file when the simulator shuts down (`@PreDestroy`).

---

### H2 Database Entities

**`simulator/entity/VehicleEntity.java`** → H2 table: `vehicles`
- Stores synced vehicle data from the main backend. Updated on every simulator boot.
- **Fields:** `id`, `coreVehicleId`, `vehicleNumber`, `vehicleType`, `ownerName`, `currentStatus`, `lastActiveTimestamp`

**`simulator/entity/VehicleHistory.java`** → H2 table: `vehicle_history`
- Append-only log of trip events per vehicle.
- **Fields:** `id`, `coreVehicleId`, `eventType` (TRIP_START, TRIP_END, STATUS_CHANGE), `eventNote`, `timestamp`

---

### In-Memory Runtime Model

**`iot/model/VehicleSimulator.java`** (not a database entity — lives in RAM)
- Holds the live runtime state of one simulated vehicle: its assigned route waypoints, current position index, speed, and status.
- **Methods:** `advance()` — moves to the next waypoint on the route. `getCurrentLat()`, `getCurrentLon()`
- This object is stored in `ActiveVehicleRegistry` while the simulation runs.

---

### Simulator Services

**`iot/service/ActiveVehicleRegistry.java`**
- Thread-safe `ConcurrentHashMap<Long, VehicleSimulator>`. The single source of truth for which vehicles are currently being simulated. All simulation services read from and write to this registry.
- **Methods:** `add()`, `remove()`, `get(vehicleId)`, `getAll()`, `getAllVehicleIds()`, `size()`

---

**`iot/service/RouteSimulatorService.java`** ⭐ Main Orchestrator
- On startup (`@PostConstruct`): fetches the real vehicle list from `GET http://localhost:8080/api/vehicles`, syncs to local H2, assigns routes, starts simulating every vehicle.
- A `@Scheduled` tick fires every second, advancing every active vehicle one waypoint along its route and broadcasting the new position.
- **Methods:** `initializeAllVehicles()`, `simulationTick()`, `fetchVehiclesFromMainBackend()`, `syncLocalDbWithMainBackend()`, `startSimulationForVehicle(vehicleId)`

---

**`iot/service/IoTBroadcasterService.java`**
- After each simulation tick, POSTs the new GPS coordinates to the main backend (`POST http://localhost:8080/api/iot/data`). Builds the full `IoTDataRequest` payload from the `VehicleSimulator` runtime state.
- **Method:** `broadcastLocation(VehicleSimulator sim)`

---

**`iot/service/RouteFetchService.java`**
- Builds the list of GPS waypoints for a vehicle's route. Attempts to use OSRM (Open Source Routing Machine) to get real road-snapped routes. Falls back to straight-line interpolation between start and end coordinates if OSRM is unreachable.
- **Methods:** `fetchAndAssignRouteForVehicle(vehicleId, routeId)`, `fetchOsrmRoute()`, `interpolateDirectRoute()`, `getSelectableRouteCount()`

---

**`iot/service/NHDetectionService.java`**
- Determines if the vehicle's current coordinate is within a known National Highway bounding box. Sets the `isHighway` flag in the broadcast payload.
- **Methods:** `isOnNationalHighway(lat, lon)`, `getHighwayName(lat, lon)`

---

**`iot/service/VehicleLifecycleManager.java`**
- Starts or stops simulation for a single vehicle. `startVehicle()` fetches a route and registers the vehicle. `stopVehicle()` removes it from the registry and updates H2 status.
- **Methods:** `startVehicle(vehicleId, routeId)`, `stopVehicle(vehicleId)`, `restartVehicle(vehicleId)`

---

**`simulator/service/MovementSimulator.java`**
- Controls vehicle movement physics — acceleration, deceleration, speed limits per vehicle type, whether to pause at waypoints.
- **Methods:** `calculateNextSpeed()`, `shouldPauseAtWaypoint()`, `applySpeedLimit(vehicleType)`

---

**`simulator/service/GPSGenerator.java`**
- Adds realistic random jitter to GPS coordinates to mimic real device noise. A perfectly straight GPS trace would look fake.
- **Method:** `generateNextCoordinate(currentLat, currentLon, targetLat, targetLon, jitterMeters)`

---

**`simulator/service/OfflineStorageService.java`**
- If the main backend is unreachable (e.g. it is restarting), this service buffers unsent GPS payloads locally and retries sending them when the backend comes back online. Prevents data loss during temporary outages.
- **Methods:** `bufferPayload(payload)`, `flushBuffer()`, `isBackendOnline()`

---

**`iot/service/SimulatorPersistenceService.java`**
- Saves telemetry history records to H2 and updates `VehicleEntity.currentStatus` after state changes.

---

**`iot/service/SimulatorSettingsService.java`**
- Manages user-configurable simulation parameters — speed multiplier, tick interval, route assignments. Provides defaults and allows runtime updates from the dashboard.
- **Methods:** `getSettings()`, `updateSettings()`, `getSpeedMultiplier()`, `getTickIntervalMs()`

---

### Simulator Controllers

**`iot/controller/SimulationControlController.java`**
- REST API for the simulator dashboard to control simulations.

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/simulation/start-all` | Start all PARKED vehicles |
| `POST` | `/api/simulation/stop-all` | Stop all RUNNING vehicles |
| `GET` | `/api/simulation/status` | Active vehicle count + IDs |
| `POST` | `/api/simulation/start/{vehicleId}` | Start one vehicle |
| `POST` | `/api/simulation/stop/{vehicleId}` | Stop one vehicle |
| `GET` | `/api/simulation/vehicles` | Full vehicle list with enriched status |
| `GET` | `/api/simulation/history/{vehicleId}` | Trip event log for a vehicle |

---

**`iot/controller/LocationController.java`**
- Returns real-time positions of all active simulated vehicles. The main frontend's `VehicleTrackingModal.jsx` polls this every 3 seconds to overlay live GPS dots on the tracking map.
- **Endpoint:** `GET /api/iot/live-locations`

---

**`iot/controller/SimulatorSettingsController.java`**
- Allows the dashboard to adjust simulation speed and tick rate at runtime.
- **Endpoints:** `GET /api/simulation/settings`, `POST /api/simulation/settings`

---

### Simulator Static Dashboard

`iot-simulator/src/main/resources/static/` — served by the simulator's Spring Boot at `http://localhost:8082`

| File | Purpose |
|------|---------|
| `index.html` | Dashboard shell — loads Leaflet.js map and the vehicle control table |
| `dashboard.js` | All interactive logic — polls `/api/simulation/vehicles` every 2s, renders vehicle rows, manages Leaflet map markers, handles Start/Stop button clicks |
| `styles.css` | Dark-themed CSS for the dashboard — vehicle table, status badges, map container |

---

## 🟢 SECTION 9: React Frontend

### Entry Points

**`main.jsx`** — Mounts `<App />` into `index.html#root`. The Vite build entry point.

**`App.jsx`** — Defines the entire React Router tree. All page routes are declared here. Lazy-loads heavy pages for faster initial load. Protects all routes (redirects unauthenticated users to `/login`).

**All routes:**

| Route | Page Component | Who Can Access |
|-------|---------------|---------------|
| `/login` | `Login.jsx` | Public |
| `/register` | `Register.jsx` | Public |
| `/` | `Home.jsx` | Authenticated |
| `/dashboard` | `UserDashboard.jsx` | Users |
| `/vehicles` | `Vehicles.jsx` | Users |
| `/toll-history` | `TollHistory.jsx` | Users |
| `/wallet-bills` | `WalletBills.jsx` | Users |
| `/admin/dashboard` | `AdminDashboard.jsx` | Admin |
| `/admin/vehicles` | `AdminVehicles.jsx` | Admin |
| `/admin/users` | `Users.jsx` | Admin |
| `/admin/highways` | `AdminHighways.jsx` | Admin |
| `/admin/billing` | `AdminBilling.jsx` | Admin |
| `/admin/reports` | `AdminReports.jsx` | Admin |
| `/admin/wallets` | `AdminWallets.jsx` | Admin |
| `/db-viewer` | `DbViewer.jsx` | Dev only |

---

### `services/api.js`
- Axios instance with `baseURL: http://localhost:8080/api` and 10-second timeout.
- **Exported functions:** `getRequest(endpoint)`, `postRequest(endpoint, data)`, `putRequest(endpoint, data)`, `patchRequest(endpoint)`, `deleteRequest(endpoint)`
- **Used by:** Every page and component that calls the backend.

---

### `services/auth.js`
- Session management using `sessionStorage`. Login stores user data (userId, name, role). Exports guards used by `App.jsx`.
- **Functions:** `login(email, phone, role)`, `getSession()`, `clearSession()`, `isAuthenticated()`

---

### `hooks/usePagination.js`
- Generic pagination hook. Given a full data array and page size, returns `currentPage`, `totalPages`, `paginatedData`, `goToPage()`.
- **Used by:** `AdminUsersTable`, `AdminVehiclesTable`, `TollHistory`, `BillsTable`

---

### Pages

| Page | Route | What the User Sees |
|------|-------|-------------------|
| `Login.jsx` | `/login` | Email + phone + role login form |
| `Register.jsx` | `/register` | New user registration form |
| `Home.jsx` | `/` | Landing/redirect based on role |
| `UserDashboard.jsx` | `/dashboard` | 4 summary stat cards + vehicle table + "Track" button |
| `Vehicles.jsx` | `/vehicles` | User's vehicles + lifecycle request submission |
| `TollHistory.jsx` | `/toll-history` | Paginated table of all highway trip records |
| `WalletBills.jsx` | `/wallet-bills` | Wallet balance + recharge request + monthly bills |
| `AdminDashboard.jsx` | `/admin/dashboard` | System-wide stats + pending request count |
| `AdminVehicles.jsx` | `/admin/vehicles` | All vehicles + lifecycle approval queue |
| `Users.jsx` | `/admin/users` | All users + edit + profile request management |
| `AdminHighways.jsx` | `/admin/highways` | All highways + add/edit forms |
| `AdminBilling.jsx` | `/admin/billing` | Manual billing triggers + recent bills |
| `AdminReports.jsx` | `/admin/reports` | Charts — revenue, anomalies, usage by type |
| `AdminWallets.jsx` | `/admin/wallets` | Recharge request approvals + manual top-up |
| `DbViewer.jsx` | `/db-viewer` | Raw DB table browser (dev tool) |

---

### Components

**`Header.jsx`** — Top nav bar on all pages. Shows app name, current user, notification bell with unread badge count (polls `/api/notifications/user/{id}/unread-count` every 30s), and logout.

**`Paginator.jsx`** — Reusable prev/next/page-number pagination control. Props: `currentPage`, `totalPages`, `onPageChange`.

**`LoadingFallback.jsx`** — Spinner shown while lazy-loaded pages are loading.

**`vehicles/VehicleTrackingModal.jsx`** — Live vehicle tracking modal. Polls main backend for latest GPS location every 3s. Also polls IoT Simulator's `/api/iot/live-locations` for real-time positions. Renders a **Google Maps embed iframe** centered on the vehicle's coordinates. Shows an SVG speed gauge, freshness badge, and trip history.

**`admin/AdminUsersTable.jsx`** — Paginated all-users table with Edit and Profile Requests buttons per row.

**`admin/AdminUserFormModal.jsx`** — Modal to create or edit a user record directly.

**`admin/AdminUserProfileModal.jsx`** — Modal listing profile update requests for a specific user; Admin approves or rejects.

**`admin/AdminVehiclesTable.jsx`** — Paginated all-vehicles table with Edit, Toggle Status, and Track buttons.

**`admin/AdminVehicleModal.jsx`** — Modal for admin to directly create or edit a vehicle (bypasses approval queue).

**`admin/AdminVehicleRequests.jsx`** — Pending lifecycle request queue. Each row has Approve (with notes) and Reject buttons.

**`admin/AdminVehicleTrackingModal.jsx`** — Admin version of the vehicle tracking modal.

**`wallet/WalletCard.jsx`** — Shows current balance, minimum balance, and a recharge request form.

**`wallet/BillsTable.jsx`** — Paginated bills table with month, distance, amount, status, and Pay Now button.

**`wallet/BillGenerator.jsx`** — Admin buttons to manually trigger bill generation for all users / one user / one vehicle.

**`users/ProfileEditModal.jsx`** — Modal for users to submit a profile update request (name/phone change → goes to admin queue).

---

### Styles (`styles/`)

| File | What It Styles |
|------|---------------|
| `global.css` | Body, fonts, CSS root variables |
| `layout.css` | Main layout containers, page structure |
| `buttons.css` | All button variants — primary, danger, ghost |
| `badges.css` | Status pill badges — ACTIVE, INACTIVE, PENDING, etc. |
| `tables.css` | Table wrappers, striped rows, responsive overflow |
| `forms.css` | Input, select, textarea base styles |
| `messages.css` | Error messages, success banners, empty state text |
| `vehicles.css` | Vehicle card grid, type icons |
| `wallet.css` | Wallet card, balance display, recharge form |

---

## ⚙️ SECTION 10: Configuration Files

### `src/main/resources/application.properties` (Main Backend)
- MySQL connection URL, credentials via `${DB_USERNAME}` / `${DB_PASSWORD}`
- SMTP mail config via `${EMAIL_USERNAME}` / `${EMAIL_PASSWORD}`
- Admin credentials via `${ADMIN_EMAIL}` / `${ADMIN_PASSWORD}`
- JPA/Hibernate DDL setting

### `iot-simulator/src/main/resources/application.properties` (Simulator)
- `server.port=8082`
- H2 file database at `./data/iot_simulator_db`
- H2 console enabled at `/h2-console`
- `core.api.url=http://localhost:8080/api/iot/data`
- `core.api.base=http://localhost:8080`

### `.env` (never committed)
All secrets loaded at runtime:
```
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
EMAIL_USERNAME=your_gmail
EMAIL_PASSWORD=your_app_password
EMAIL_FROM=your_gmail
ADMIN_EMAIL=admin@tolling.com
ADMIN_PASSWORD=your_admin_password
```

### `.env.example` (safe to commit)
Template showing which variables are needed — no actual values.

### `frontend/vite.config.js`
- Sets dev server port to 3000.
- Proxies all `/api/*` requests to `http://localhost:8080` — eliminates CORS during development.

### `frontend/vite.config.github.js`
- Separate build config for GitHub Pages deployment.
- Sets `base: './'` for relative asset paths and `outDir: '../docs/main-app'` so the built files land in the repository's `docs/` folder.
- Used with: `npm run build -- --config vite.config.github.js`

### `frontend/package.json`
- **Runtime dependencies:** `react`, `react-dom`, `react-router-dom`, `axios`
- **Dev dependencies:** `vite`, `@vitejs/plugin-react`
- **Scripts:** `dev`, `build`, `preview`

### `pom.xml` (Main Backend)
- **Spring Boot dependencies:** Web, Data JPA, Mail, Validation
- **External:** MySQL Connector/J
- **Packaging:** JAR

### `iot-simulator/pom.xml`
- **Spring Boot dependencies:** Web, Data JPA
- **External:** H2 Database (embedded)
- **Packaging:** JAR

---

## 🔗 SECTION 11: Complete GPS Ping Data Flow

When the IoT Simulator sends one GPS coordinate to the main backend:

```
IoT Simulator (Port 8082)
  IoTBroadcasterService.broadcastLocation()
      ↓ POST /api/iot/data
Main Backend (Port 8080)
  IoTController
      ↓ @Valid validation
  IoTIdentificationService.processIoTData()
      ├── IoTValidationService.validateVehicleExists()
      ├── IoTValidationService.parseAndValidateTimestamp()
      ├── IoTValidationService.normalizeCoordinate()
      ├── HighwayService.getAllHighways()
      ├── HighwayDetectionService.detectHighwayUsage()
      │     └── DistanceCalculatorService.calculateDistance()
      ├── HighwayStateProcessor.processHighwayDetectionAndDistance()
      │     ├── ENTRY  → HighwayUsageService.createHighwaySession()
      │     ├── SAME   → HighwayUsageService.addDistanceToSession()
      │     │             └── WalletService.deductToll()
      │     ├── EXIT   → HighwayUsageService.closeSession()
      │     └── SWITCH → closeSession() + createHighwaySession()
      ├── LocationTrackingService.saveLocation()
      └── AnomalyDetectionService.runAllChecks()
            └── (if anomaly found) → UserNotificationRepository.save()
      ↓
  IoTDataResponse returned to simulator
```

---

*Project Structure — Smart Highway Tolling System*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
