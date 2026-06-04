# 🗄️ Database Schema
### Smart Highway Usage-Based Tolling System

> **Back to:** [README.md](../README.md) | **Related:** [API Reference](API_REFERENCE.md) · [Features](FEATURES.md) · [Project Structure](PROJECT_STRUCTURE.md)

---

## 📌 Overview

| Database | Engine | Used By | Tables |
|----------|--------|---------|--------|
| **`tolling_system`** | MySQL 8.0+ | Core Backend (Port 8080) | 11 tables |
| **`iot_simulator_db`** | H2 2.2.224 (embedded file) | Standalone Simulator (Port 8082) | 2 tables |
| **Total** | — | — | **13 tables** |

**Schema Management:** Spring Boot's `spring.jpa.hibernate.ddl-auto=update` auto-creates and updates all tables on startup from the Java entity class definitions. No manual SQL is needed.

### Legend
| Symbol | Meaning |
|--------|---------|
| PK | Primary Key |
| FK | Foreign Key |
| UQ | Unique constraint |
| NN | Not Null |
| AUTO | Auto-increment |

---

## 🗺️ Entity Relationship Map

```
users ──1:1──► wallets
users ──1:many──► vehicles
users ──1:many──► bills
users ──1:many──► user_notifications
users ──1:many──► profile_update_requests
users ──1:many──► wallet_recharge_requests
users ──1:many──► vehicle_requests

vehicles ──1:many──► location_tracking
vehicles ──1:many──► highway_usage
vehicles ──1:many──► data_anomalies
vehicles ──1:many──► vehicle_requests

highways ──1:many──► location_tracking
highways ──1:many──► highway_usage
```

---

## 📋 PART A — Main Platform Database (MySQL `tolling_system`)

---

### Table 1: `users`
**Entity:** `User.java` | `@Table(name = "users")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `user_id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `name` | `String` | `VARCHAR(100)` | ✅ | ❌ | `@Column(nullable=false, length=100)` |
| `email` | `String` | `VARCHAR(100)` | ✅ | ✅ | `@Column(nullable=false, unique=true, length=100)` |
| `phone_number` | `String` | `VARCHAR(15)` | ✅ | ❌ | `@Column(nullable=false, length=15)` |
| `created_at` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | Set to `LocalDateTime.now()` in constructor |

> ⚠️ **Important correction:** There is **no `role` column** in this table. The admin/user role distinction is handled entirely at login — `AuthController` compares submitted credentials against hardcoded admin values from `application.properties`. Regular users are looked up in this table by email + phone.

**Relationships:** Parent of `vehicles`, `wallets`, `bills`, `user_notifications`, `profile_update_requests`, `wallet_recharge_requests`

---

### Table 2: `vehicles`
**Entity:** `Vehicle.java` | `@Table(name = "vehicles")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `vehicle_id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `vehicle_number` | `String` | `VARCHAR(20)` | ✅ | ✅ | `@Column(nullable=false, unique=true, length=20)` |
| `vehicle_type` | `VehicleType` enum | `VARCHAR(20)` | ✅ | ❌ | `@Enumerated(STRING)` — values: `CAR`, `BIKE`, `BUS`, `TRUCK` |
| `user_id` | `Long` (FK → users) | `BIGINT` | ✅ | ❌ | `@JoinColumn(name="user_id", nullable=false)` |
| `status` | `String` | `VARCHAR(20)` | ✅ | ❌ | Default: `"ACTIVE"` — values: `ACTIVE`, `INACTIVE`, `SCRAPED` |
| `registered_at` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | Set to `LocalDateTime.now()` in constructor |

---

### Table 3: `wallets`
**Entity:** `Wallet.java` | `@Table(name = "wallets")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `wallet_id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `user_id` | `Long` (FK → users) | `BIGINT` | ✅ | ✅ | `@JoinColumn(unique=true, nullable=false)` — one wallet per user enforced |
| `balance` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Default `0.0`; **can go negative** (deficit mode by design) |
| `minimum_balance` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Default `0.0`; deficit triggered when `balance < minimumBalance` |
| `created_at` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | Set in constructor |
| `last_updated` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | Updated automatically by `setBalance()` on every deduction |

**Design note:** The system deliberately allows negative balances. Vehicles continue using highways even if the wallet goes into deficit. The outstanding amount is collected via `BillDeductionScheduler` when the monthly bill is processed.

---

### Table 4: `highways`
**Entity:** `Highway.java` | `@Table(name = "highways")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `highway_id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `highway_name` | `String` | `VARCHAR(100)` | ✅ | ❌ | `@Column(nullable=false, length=100)` |
| `start_latitude` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | GPS bounding box start point |
| `start_longitude` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | GPS bounding box start point |
| `end_latitude` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | GPS bounding box end point |
| `end_longitude` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | GPS bounding box end point |
| `rate_per_km_for_car` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Toll rate ₹/km for CAR type |
| `rate_per_km_for_bike` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Toll rate ₹/km for BIKE type |
| `rate_per_km_for_truck` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Toll rate ₹/km for TRUCK **and BUS** types |

> ⚠️ **Important:** There is **no separate `rate_per_km_for_bus` column**. The `HighwayUsageAggregationService` uses the `rate_per_km_for_truck` value when calculating tolls for BUS type vehicles. This is a deliberate design decision — buses and trucks are treated at the same commercial rate tier.

---

### Table 5: `highway_usage`
**Entity:** `HighwayUsage.java` | `@Table(name = "highway_usage")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `vehicle_id` | `Long` | `BIGINT` | ✅ | ❌ | Raw ID stored (no `@JoinColumn` FK annotation) |
| `highway_id` | `Long` | `BIGINT` | ✅ | ❌ | Raw ID stored (no `@JoinColumn` FK annotation) |
| `distance_traveled` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Default `0.0`; accumulated via `addDistance()` method |
| `entry_timestamp` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | Set when vehicle enters the highway boundary |
| `exit_timestamp` | `LocalDateTime` | `DATETIME(6)` | ❌ | ❌ | `NULL` while session is active (vehicle still on highway) |
| `entry_latitude` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | GPS coordinates at entry point |
| `entry_longitude` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | GPS coordinates at entry point |
| `exit_latitude` | `Double` | `DOUBLE PRECISION` | ❌ | ❌ | `NULL` while session is open |
| `exit_longitude` | `Double` | `DOUBLE PRECISION` | ❌ | ❌ | `NULL` while session is open |

**How a session works:**
- A new row is INSERTed when the first GPS ping inside a highway boundary arrives
- Each subsequent on-highway ping UPDATEs `distance_traveled`
- When the vehicle leaves the highway, `exit_timestamp`, `exit_latitude`, `exit_longitude` are set

---

### Table 6: `location_tracking`
**Entity:** `LocationTracking.java` | `@Table(name = "location_tracking")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `vehicle_id` | `Long` | `BIGINT` | ✅ | ❌ | Raw ID (no FK annotation) |
| `latitude` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Normalized to 6 decimal places before save |
| `longitude` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Normalized to 6 decimal places |
| `timestamp` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | From IoT request; defaults to `now()` if missing |
| `highway_id` | `Long` | `BIGINT` | ❌ | ❌ | `NULL` if vehicle not on any highway at this ping |
| `distance_from_previous` | `Double` | `DOUBLE PRECISION` | ❌ | ❌ | Haversine km from previous ping; `NULL` for first ping |
| `is_on_highway` | `Boolean` | `BIT(1)` | ✅ | ❌ | Default `false` in constructor |
| `speed_km_h` | `Double` | `DOUBLE PRECISION` | ❌ | ❌ | From simulator payload; `NULL` if not provided |
| `status` | `String` | `VARCHAR(50)` | ❌ | ❌ | Simulator movement state (e.g. `"DRIVING"`) |
| `route_name` | `String` | `VARCHAR(150)` | ❌ | ❌ | Simulator route label |

**Volume note:** This table grows very rapidly during simulation — one row per vehicle per second. Consider archival strategies for long-running production deployments.

---

### Table 7: `bills`
**Entity:** `Bill.java` | `@Table(name = "bills")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `bill_id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `user_id` | `Long` | `BIGINT` | ✅ | ❌ | Owner of the bill |
| `vehicle_id` | `Long` | `BIGINT` | ❌ | ❌ | `NULL` for consolidated user-level bills; set for per-vehicle bills |
| `total_distance` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Total km driven on highways in the billing month |
| `total_amount` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Total toll in ₹ |
| `bill_month` | `String` | `VARCHAR(7)` | ✅ | ❌ | Format: `"YYYY-MM"` e.g. `"2026-04"` |
| `due_date` | `LocalDate` | `DATE` | ✅ | ❌ | Set to `now + 15 days` at generation time |
| `status` | `BillStatus` enum | `VARCHAR(20)` | ✅ | ❌ | `@Enumerated(STRING)`; default `PENDING`; values: `PENDING`, `PAID`, `OVERDUE` |
| `created_at` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | Set in constructor |
| `auto_deduct_attempted` | `boolean` | `BIT(1)` | ✅ | ❌ | Default `false`; set to `true` after `BillDeductionScheduler` runs — prevents double-deduction |

**Duplicate-bill protection:** The scheduler checks `getBillByVehicleAndMonth()` before generating. If a bill for the same vehicle + same month already exists, generation is skipped. This prevents double-billing if the backend restarts on the 1st of the month.

---

### Table 8: `data_anomalies`
**Entity:** `DataAnomaly.java` | `@Table(name = "data_anomalies")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `vehicle_id` | `Long` | `BIGINT` | ✅ | ❌ | Which vehicle triggered the anomaly |
| `anomaly_type` | `AnomalyType` enum | `VARCHAR(30)` | ✅ | ❌ | Values: `MISSING_DATA`, `SUDDEN_DISCONNECTION`, `INACTIVITY_ON_HIGHWAY`, `REPEATED_PATTERN` |
| `description` | `String` | `VARCHAR(500)` | ✅ | ❌ | Human-readable anomaly explanation |
| `severity` | `AnomalySeverity` enum | `VARCHAR(10)` | ✅ | ❌ | Values: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| `detected_at` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | Set to `now()` in constructor |
| `related_location_id` | `Long` | `BIGINT` | ❌ | ❌ | FK to `location_tracking.id`; `NULL` if the anomaly is vehicle-level (e.g. missing data) |

**Anomaly thresholds (implemented):**

| Type | Trigger | Severity | Threshold Detail |
|------|---------|---------|-----------------|
| `MISSING_DATA` | No GPS ping for extended period | HIGH | 2+ hours |
| `INACTIVITY_ON_HIGHWAY` | On highway but not moving | MEDIUM | < 50m movement in 30 minutes |
| `SUDDEN_DISCONNECTION` | Abrupt signal loss mid-session | HIGH | Session open + no new pings |
| `REPEATED_PATTERN` | Same anomaly type multiple times | HIGH | 3+ occurrences for same vehicle |

> **Note on inactivity threshold:** GPS jitter (micro-movements) was causing false positives. A `INACTIVITY_DISTANCE_THRESHOLD_KM = 0.05` (50 meters) was added — the vehicle must move more than 50m within 30 minutes or it's flagged as stationary.

---

### Table 9: `vehicle_requests`
**Entity:** `VehicleRequest.java` | `@Table(name = "vehicle_requests")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `user_id` | `Long` | `BIGINT` | ✅ | ❌ | Requesting user |
| `vehicle_id` | `Long` | `BIGINT` | ❌ | ❌ | `NULL` for ADD requests (vehicle doesn't exist yet) |
| `request_type` | `RequestType` enum | `VARCHAR(255)` | ✅ | ❌ | Values: `ADD`, `DEACTIVATE`, `SELL`, `SCRAP`, `MODIFY` |
| `status` | `RequestStatus` enum | `VARCHAR(255)` | ✅ | ❌ | Default: `PENDING`; values: `PENDING`, `APPROVED`, `REJECTED` |
| `requested_vehicle_number` | `String` | `VARCHAR(255)` | ❌ | ❌ | Used for ADD and MODIFY operations |
| `requested_vehicle_type` | `String` | `VARCHAR(255)` | ❌ | ❌ | Used for ADD and MODIFY (`"CAR"`, `"BIKE"`, `"TRUCK"`) |
| `new_owner_user_id` | `Long` | `BIGINT` | ❌ | ❌ | Used for SELL requests only |
| `reason` | `String` | `VARCHAR(500)` | ❌ | ❌ | User's stated justification |
| `admin_notes` | `String` | `VARCHAR(500)` | ❌ | ❌ | Admin's decision notes (visible to user via notification) |
| `created_at` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | Set at field declaration via `LocalDateTime.now()` |
| `reviewed_at` | `LocalDateTime` | `DATETIME(6)` | ❌ | ❌ | `NULL` until admin acts |

---

### Table 10: `profile_update_requests`
**Entity:** `ProfileUpdateRequest.java` | `@Table(name = "profile_update_requests")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `user_id` | `Long` | `BIGINT` | ✅ | ❌ | Which user is requesting the change |
| `current_name` | `String` | `VARCHAR(255)` | ❌ | ❌ | **Snapshot** of the user's name at time of request |
| `current_email` | `String` | `VARCHAR(255)` | ❌ | ❌ | Snapshot of current email |
| `current_phone` | `String` | `VARCHAR(255)` | ❌ | ❌ | Snapshot of current phone |
| `requested_name` | `String` | `VARCHAR(255)` | ❌ | ❌ | Desired new name (`NULL` if not changing) |
| `requested_email` | `String` | `VARCHAR(255)` | ❌ | ❌ | Desired new email (`NULL` if not changing) |
| `requested_phone` | `String` | `VARCHAR(255)` | ❌ | ❌ | Desired new phone (`NULL` if not changing) |
| `status` | `RequestStatus` enum | `VARCHAR(255)` | ✅ | ❌ | Default: `PENDING`; values: `PENDING`, `APPROVED`, `REJECTED` |
| `admin_notes` | `String` | `VARCHAR(255)` | ❌ | ❌ | Admin's decision notes |
| `created_at` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | Set at field declaration |
| `reviewed_at` | `LocalDateTime` | `DATETIME(6)` | ❌ | ❌ | `NULL` until admin acts |

> **Design insight:** The table stores both the **current value** (snapshot at request time) and the **requested value**. This gives the admin a clear side-by-side comparison when reviewing requests — they can see exactly what is changing without querying the users table separately.

---

### Table 11: `wallet_recharge_requests`
**Entity:** `WalletRechargeRequest.java` | `@Table(name = "wallet_recharge_requests")`

| Column | Java Type | SQL Type | NN | UQ | Notes |
|--------|-----------|----------|----|----|-------|
| `request_id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | `@Id @GeneratedValue(IDENTITY)` |
| `user_id` | `Long` (FK → users) | `BIGINT` | ✅ | ❌ | `@JoinColumn(name="user_id", nullable=false)`, EAGER fetch |
| `amount` | `Double` | `DOUBLE PRECISION` | ✅ | ❌ | Requested top-up amount in ₹ |
| `status` | `RechargeStatus` enum | `VARCHAR(255)` | ✅ | ❌ | Values: `PENDING`, `APPROVED`, `REJECTED` |
| `request_date` | `LocalDateTime` | `DATETIME(6)` | ✅ | ❌ | Set in constructor |
| `processed_date` | `LocalDateTime` | `DATETIME(6)` | ❌ | ❌ | `NULL` until admin approves or rejects |
| `upi_reference` | `String` | `VARCHAR(255)` | ❌ | ❌ | Simulated UPI payment reference ID — generated on request |

---

## 📋 PART B — IoT Simulator Database (H2 file: `./data/iot_simulator_db`)

The H2 database is created automatically when the simulator first starts. It is stored as a file at `iot-simulator/data/iot_simulator_db.mv.db`.

Access the H2 web console at: `http://localhost:8082/h2-console`
- **JDBC URL:** `jdbc:h2:file:./data/iot_simulator_db`
- **Username:** `sa`
- **Password:** `password`

---

### Table 12 (H2): `simulated_vehicles`
**Entity:** `VehicleEntity.java` | `@Table(name = "simulated_vehicles")`

| Column | Java Type | H2 SQL Type | NN | UQ | Notes |
|--------|-----------|-------------|----|----|-------|
| `id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | Simulator-internal ID |
| `core_vehicle_id` | `Long` | `BIGINT` | ✅ | ✅ | `@Column(unique=true, nullable=false)` — mirrors `vehicles.vehicle_id` from main MySQL DB |
| `vehicle_number` | `String` | `VARCHAR(255)` | ❌ | ❌ | License plate — synced from main backend on boot |
| `vehicle_type` | `String` | `VARCHAR(255)` | ❌ | ❌ | `"CAR"`, `"BIKE"`, `"TRUCK"`, `"BUS"` — stored as plain string |
| `owner_name` | `String` | `VARCHAR(255)` | ❌ | ❌ | Owner's full name — synced from main backend on boot |
| `current_status` | `String` | `VARCHAR(255)` | ❌ | ❌ | Values: `RUNNING`, `STOPPED_FOR_BREAK`, `PARKED` |
| `last_active_timestamp` | `LocalDateTime` | `TIMESTAMP` | ❌ | ❌ | Updated each time the status changes |

**Sync process:** On every simulator boot, `RouteSimulatorService.syncLocalDbWithMainBackend()` calls `GET http://localhost:8080/api/vehicles`, compares the result against existing H2 rows, and inserts/updates to keep them in sync. Vehicles present in MySQL but not H2 are added. Vehicles deleted from MySQL are removed from H2.

---

### Table 13 (H2): `vehicle_history`
**Entity:** `VehicleHistory.java` | `@Table(name = "vehicle_history")`

| Column | Java Type | H2 SQL Type | NN | UQ | Notes |
|--------|-----------|-------------|----|----|-------|
| `id` | `Long` | `BIGINT` AUTO | ✅ | ✅ PK | Auto-generated |
| `core_vehicle_id` | `Long` | `BIGINT` | ✅ | ❌ | References `simulated_vehicles.core_vehicle_id` (no FK annotation) |
| `event_type` | `String` | `VARCHAR(255)` | ✅ | ❌ | Values: `TRIP_START`, `TRIP_END`, `STATUS_CHANGE` |
| `description` | `String` | `VARCHAR(500)` | ❌ | ❌ | `@Column(length=500)` — human-readable event detail |
| `timestamp` | `LocalDateTime` | `TIMESTAMP` | ✅ | ❌ | Set to `LocalDateTime.now()` in constructor |

---

## 🔑 Key Index Summary

| Table | Indexed Columns | Reason |
|-------|----------------|--------|
| `users` | `email` (UNIQUE) | Fast login lookup + prevents duplicate accounts |
| `vehicles` | `vehicle_number` (UNIQUE) | Prevents duplicate plates |
| `wallets` | `user_id` (UNIQUE) | Enforces one-wallet-per-user |
| `simulated_vehicles` | `core_vehicle_id` (UNIQUE) | Prevents duplicate simulator entries per main vehicle |

---

## 🔄 Data Flow Through the Schema (One GPS Ping)

```
POST /api/iot/data arrives
    ↓
INSERT → location_tracking (new GPS ping row)
    ↓ (if on highway)
UPDATE → highway_usage (distance_traveled += haversine_distance)
    OR
INSERT → highway_usage (new session: entry_timestamp, entry_lat, entry_lon)
    ↓
UPDATE → wallets (balance -= toll_amount, last_updated = now())
    ↓ (if anomaly detected)
INSERT → data_anomalies (anomaly record)
INSERT → user_notifications (alert to user)
```

---

## ⚠️ Schema Correction Log

| What Was Wrong | Correction |
|---------------|-----------|
| `users` had a `role` column | No role column exists — admin is hardcoded in `AuthController` |
| `highways` had `rate_per_km_for_bus` | No bus column — buses use `rate_per_km_for_truck` |
| `highway_usage` used `entry_time`/`exit_time` | Actual columns: `entry_timestamp`/`exit_timestamp` |
| `highway_usage` used `distance_travelled` | Actual column: `distance_traveled` |
| `bills` missing `auto_deduct_attempted` | Column exists — prevents double-deduction by scheduler |
| `profile_update_requests` only covered name/phone | Also covers email; stores current AND requested values |
| Notification endpoint was `/api/user-notifications` | Correct path: `/api/notifications` |
| Total 11 tables | Correct total: **13 tables** (11 MySQL + 2 H2) |

---

*Database Schema — Smart Highway Tolling System*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
