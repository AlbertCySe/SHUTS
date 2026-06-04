# 📡 API Reference
### Smart Highway Usage-Based Tolling System

> **Back to:** [README.md](../README.md) | **Related:** [Features](FEATURES.md) · [Database Schema](DATABASE_SCHEMA.md) · [Testing Guide](TESTING_GUIDE.md)

---

## 📌 General Rules

| Rule | Detail |
|------|--------|
| **Content-Type** | `application/json` for all requests and responses |
| **Core Backend URL** | `http://localhost:8080` |
| **Simulator URL** | `http://localhost:8082` |
| **Timestamp Format** | ISO-8601 — e.g. `2026-04-01T10:30:00` |
| **Coordinate Precision** | GPS coordinates normalized to 6 decimal places |
| **Paging** | Supported on vehicle listings via `?page=0&size=20` |
| **Authentication** | Session-based (stored in browser `sessionStorage`) — no JWT yet |

> ⚠️ **Critical path correction:** Notification endpoints use `/api/notifications/` — **NOT** `/api/user-notifications/`. Using the old path returns `404`.

---

## 📋 Complete Endpoint Index

### Main Backend (Port 8080)

| Method | Path | Role | Description |
|--------|------|------|-------------|
| POST | `/api/auth/login` | Public | Login |
| GET | `/api/users` | Admin | All users |
| POST | `/api/users` | Public | Register user |
| GET | `/api/users/{id}` | Both | Get user |
| PUT | `/api/users/{id}` | Admin | Update user |
| DELETE | `/api/users/{id}` | Admin | Delete user |
| GET | `/api/users/{id}/vehicles` | User | User's vehicles |
| POST | `/api/users/{id}/vehicles` | Admin | Add vehicle (admin bypass) |
| GET | `/api/vehicles` | Both | All vehicles (optional paging) |
| GET | `/api/vehicles/{id}` | Both | Get vehicle |
| PUT | `/api/vehicles/{id}` | Admin | Update vehicle |
| PATCH | `/api/vehicles/{id}/toggle-status` | Admin | Toggle ACTIVE/INACTIVE |
| DELETE | `/api/vehicles/{id}` | Admin | Delete vehicle |
| POST | `/api/vehicle-requests` | User | Submit lifecycle request |
| GET | `/api/vehicle-requests` | Admin | All requests |
| GET | `/api/vehicle-requests/pending/count` | Admin | Pending count badge |
| GET | `/api/vehicle-requests/user/{id}` | User | Own requests |
| PUT | `/api/vehicle-requests/{id}/approve` | Admin | Approve request |
| PUT | `/api/vehicle-requests/{id}/reject` | Admin | Reject request |
| POST | `/api/profile-requests` | User | Submit profile change |
| GET | `/api/profile-requests` | Admin | All profile requests |
| GET | `/api/profile-requests/user/{id}` | User | Own profile requests |
| GET | `/api/profile-requests/pending/count` | Admin | Pending count badge |
| PUT | `/api/profile-requests/{id}/approve` | Admin | Approve |
| PUT | `/api/profile-requests/{id}/reject` | Admin | Reject |
| GET | `/api/highways` | Both | All highways |
| GET | `/api/highways/{id}` | Both | Get highway |
| GET | `/api/highways/search?name=` | Both | Search by name |
| POST | `/api/highways` | Admin | Create highway |
| PUT | `/api/highways/{id}` | Admin | Update highway |
| DELETE | `/api/highways/{id}` | Admin | Delete highway |
| GET | `/api/highway-usage` | Admin | All usage records |
| GET | `/api/highway-usage/summary/{vehicleId}` | Both | Usage summary |
| GET | `/api/locations` | Admin | Recent 200 GPS pings |
| GET | `/api/locations/vehicle/{id}/latest` | Both | Latest GPS ping |
| GET | `/api/locations/vehicle/{id}/history` | Both | Last 20 GPS pings |
| POST | `/api/iot/data` | IoT Only | Ingest GPS data |
| GET | `/api/wallets/user/{id}` | User | Get wallet balance |
| POST | `/api/wallets/user/{id}/recharge-request` | User | Request top-up |
| GET | `/api/wallets/user/{id}/recharge-requests` | User | Recharge history |
| GET | `/api/bills` | Admin | All bills |
| GET | `/api/bills/user/{id}` | User | User's bills |
| POST | `/api/bills/{id}/pay` | User | Pay bill from wallet |
| GET | `/api/notifications/user/{id}` | User | All notifications |
| GET | `/api/notifications/user/{id}/unread-count` | User | Unread count |
| PUT | `/api/notifications/{id}/read` | User | Mark one read |
| PUT | `/api/notifications/user/{id}/read-all` | User | Mark all read |
| GET | `/api/admin/stats` | Admin | System statistics |
| GET | `/api/admin/vehicles` | Admin | Enriched vehicle list |
| GET | `/api/admin/wallets/negative` | Admin | Deficit wallets |
| GET | `/api/admin/bills/recent` | Admin | Recent 10 bills |
| POST | `/api/admin/seed-wallets` | Admin | Seed wallets |
| POST | `/api/admin/populate-usage` | Admin | Seed test data |
| POST | `/api/admin/generate-bills` | Admin | Trigger monthly bills |
| POST | `/api/admin/generate-bill/user/{id}` | Admin | Bill for one user |
| POST | `/api/admin/generate-bill/vehicle/{id}` | Admin | Bill for one vehicle |
| POST | `/api/admin/generate-all-vehicle-bills` | Admin | Bulk vehicle bills |
| GET | `/api/admin/wallets/recharge-requests` | Admin | Pending recharges |
| POST | `/api/admin/wallets/recharge-requests/{id}/{action}` | Admin | Approve/decline recharge |
| POST | `/api/admin/wallets/user/{id}/topup` | Admin | Manual wallet top-up |
| GET | `/api/db-explorer/tables` | Dev | List DB tables |
| GET | `/api/db-explorer/table/{name}` | Dev | Raw table data |

### IoT Simulator (Port 8082)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/simulation/start-all` | Start all vehicles |
| POST | `/api/simulation/stop-all` | Stop all vehicles |
| GET | `/api/simulation/status` | Running/parked counts |
| GET | `/api/simulation/vehicles` | Full vehicle + live data list |
| GET | `/api/simulation/settings` | Get simulation settings |
| POST | `/api/simulation/settings` | Update simulation settings |
| GET | `/api/iot/live-locations` | All active vehicle GPS (for main frontend map) |
| GET | `/api/iot/live-location` | Single active vehicle GPS |
| GET | `/h2-console` | H2 database browser (dev only) |

---

## 1. Authentication

### POST `/api/auth/login`

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "phoneNumber": "9876543210",
  "role": "user"
}
```

**For admin login:** Set `"role": "admin"` and use the admin credentials from `application.properties`.

**Success — User (200):**
```json
{
  "role": "user",
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "9876543210"
}
```

**Success — Admin (200):**
```json
{
  "role": "admin",
  "name": "Administrator",
  "email": "admin@highway.com",
  "userId": 0
}
```

**Error Responses:**

| Code | Body |
|------|------|
| 401 | `{ "message": "No account found with this email address." }` |
| 401 | `{ "message": "Phone number does not match our records." }` |
| 401 | `{ "message": "Invalid admin credentials." }` |

> **Default admin credentials:** `admin@highway.com` / `admin123` (set in `application.properties` via `ADMIN_EMAIL` and `ADMIN_PASSWORD` env vars)

---

## 2. Users — `/api/users`

### POST `/api/users` — Create User
```json
Request:  { "name": "John Doe", "email": "john@example.com", "phoneNumber": "9876543210" }
```
**Success (201):**
```json
{
  "userId": 1, "name": "John Doe", "email": "john@example.com",
  "phoneNumber": "9876543210", "createdAt": "2026-04-01T10:00:00", "vehicles": []
}
```
**Errors:** `400` — empty body (missing required fields)

---

### GET `/api/users` — All Users
**Success (200):** Array of User objects (same shape as above).

---

### GET `/api/users/{userId}` — Single User
**Success (200):** Single User object.
**Errors:** `404` — empty body.

---

### PUT `/api/users/{userId}` — Update User
```json
Request:  { "name": "Updated Name", "email": "new@example.com", "phoneNumber": "9999999999" }
```
**Success (200):** Updated User object.
**Errors:** `404` — empty body.

---

### DELETE `/api/users/{userId}` — Delete User
**Success (204):** Empty body.
**Errors:** `404` — empty body.

---

### POST `/api/users/{userId}/vehicles` — Add Vehicle (Admin Bypass)
Adds a vehicle directly without going through the approval queue. Admin use only.
```json
Request:  { "vehicleNumber": "TN01AB1234", "vehicleType": "CAR" }
```
**Success (201):**
```json
{
  "vehicleId": 1, "vehicleNumber": "TN01AB1234", "vehicleType": "CAR",
  "status": "ACTIVE", "registeredAt": "2026-04-01T10:00:00", "ownerId": 1
}
```

---

## 3. Vehicles — `/api/vehicles`

### GET `/api/vehicles` — All Vehicles
Without params → array. With `?page=0&size=20` → Spring Page object:
```json
{
  "content": [ /* Vehicle objects */ ],
  "pageable": { "pageNumber": 0, "pageSize": 20, "sort": { "sorted": false } },
  "totalElements": 100, "totalPages": 5,
  "last": false, "first": true, "size": 20, "number": 0
}
```

---

### PATCH `/api/vehicles/{id}/toggle-status` — Toggle ACTIVE/INACTIVE
**Success (200):** Vehicle object with status toggled (`ACTIVE` ↔ `INACTIVE`).

---

## 4. Vehicle Lifecycle Requests — `/api/vehicle-requests`

### POST `/api/vehicle-requests` — Submit Request

**ADD request:**
```json
{
  "userId": "1", "requestType": "ADD",
  "requestedVehicleNumber": "TN01AB1234",
  "requestedVehicleType": "CAR",
  "reason": "New vehicle purchase"
}
```

**SELL request:**
```json
{
  "userId": "1", "requestType": "SELL",
  "vehicleId": "5", "newOwnerUserId": "3",
  "reason": "Selling to colleague"
}
```

**SCRAP / DEACTIVATE:** Include `vehicleId` and `reason`.
**MODIFY:** Include `vehicleId`, `requestedVehicleNumber`, `requestedVehicleType`.

**Success (200):**
```json
{
  "id": 1, "userId": 1, "vehicleId": null,
  "requestType": "ADD", "status": "PENDING",
  "requestedVehicleNumber": "TN01AB1234",
  "requestedVehicleType": "CAR",
  "newOwnerUserId": null, "reason": "New vehicle purchase",
  "adminNotes": null,
  "createdAt": "2026-04-01T10:00:00", "reviewedAt": null
}
```
**Errors:** `400 "Failed to submit: <error message>"`

---

### GET `/api/vehicle-requests/pending/count`
```json
Success (200): { "count": 3 }
```

---

### PUT `/api/vehicle-requests/{id}/approve`
```json
Request (optional): { "adminNotes": "Documents verified. Approved." }
```
**Success (200):** Updated VehicleRequest with `status: "APPROVED"` and `reviewedAt` set.
**Errors:** `400 "Approval failed: <error message>"`

---

### PUT `/api/vehicle-requests/{id}/reject`
```json
Request (optional): { "adminNotes": "Insufficient documentation." }
```
**Success (200):** Updated VehicleRequest with `status: "REJECTED"`.

---

## 5. Profile Update Requests — `/api/profile-requests`

### POST `/api/profile-requests`
```json
{
  "userId": "1",
  "requestedName": "Albert Joseph",
  "requestedEmail": "albert.joseph@example.com",
  "requestedPhone": "9999999999"
}
```
**Success (200):**
```json
{
  "id": 1, "userId": 1,
  "currentName": "Albert J", "currentEmail": "albert@example.com", "currentPhone": "9876543210",
  "requestedName": "Albert Joseph", "requestedEmail": "albert.joseph@example.com", "requestedPhone": "9999999999",
  "status": "PENDING", "adminNotes": null,
  "createdAt": "2026-04-01T10:00:00", "reviewedAt": null
}
```

---

### GET `/api/profile-requests/pending/count`
```json
Success (200): { "count": 2 }
```

---

### PUT `/api/profile-requests/{id}/approve`
```json
Request (optional): { "adminNotes": "Identity verified." }
```
**Success (200):** Updated ProfileUpdateRequest with `status: "APPROVED"`.

---

## 6. Highways — `/api/highways`

### POST `/api/highways`
```json
{
  "highwayName": "NH-44 Bangalore-Salem",
  "startLatitude": 12.9716, "startLongitude": 77.5946,
  "endLatitude": 11.6643, "endLongitude": 78.1460,
  "ratePerKmForCar": 2.5,
  "ratePerKmForBike": 1.5,
  "ratePerKmForTruck": 4.0
}
```
> **Note:** No `ratePerKmForBus` field — buses use the truck rate.

**Success (201):**
```json
{
  "highwayId": 1, "highwayName": "NH-44 Bangalore-Salem",
  "startLatitude": 12.9716, "startLongitude": 77.5946,
  "endLatitude": 11.6643, "endLongitude": 78.1460,
  "ratePerKmForCar": 2.5, "ratePerKmForBike": 1.5, "ratePerKmForTruck": 4.0
}
```

---

### GET `/api/highways/search?name={name}`
**Success (200):** Single Highway object.
**Errors:** `404` — empty body.

---

## 7. Highway Usage — `/api/highway-usage`

### GET `/api/highway-usage` — All Records (Admin)
**Success (200):**
```json
[{
  "id": 1, "vehicleId": 5, "highwayId": 2,
  "distanceTraveled": 12.34,
  "entryTimestamp": "2026-04-01T08:00:00",
  "exitTimestamp": "2026-04-01T08:45:00",
  "entryLatitude": 12.97, "entryLongitude": 77.59,
  "exitLatitude": 11.66, "exitLongitude": 78.14
}]
```

---

### GET `/api/highway-usage/summary/{vehicleId}`
**Success (200):**
```json
{
  "vehicleId": 1,
  "totalDistance": 45.67,
  "distanceByHighway": {
    "NH-44 Bangalore-Salem": 25.5,
    "ECR Chennai": 20.17
  },
  "totalSessions": 8
}
```
**Errors:** `500 "Error retrieving usage summary: <message>"`

---

## 8. Location Tracking — `/api/locations`

### GET `/api/locations` — Recent 200 GPS Pings (Admin/Diagnostic)
**Success (200):** Array of up to 200 most recent LocationTracking records:
```json
[{
  "id": 1, "vehicleId": 5,
  "latitude": 12.9716, "longitude": 77.5946,
  "timestamp": "2026-04-01T10:30:00",
  "highwayId": 2, "distanceFromPrevious": 0.35,
  "isOnHighway": true, "speedKmH": 85.5,
  "status": "DRIVING", "routeName": "NH-44 Route"
}]
```

---

### GET `/api/locations/vehicle/{vehicleId}/latest`
**Success (200):** Single LocationTracking object (most recent).
**Errors:** `404` — empty body.

---

### GET `/api/locations/vehicle/{vehicleId}/history`
**Success (200):** Array of up to 20 most recent records for that vehicle.

---

## 9. IoT Data Ingestion — `/api/iot`

### POST `/api/iot/data` — Receive GPS Ping
Called exclusively by the IoT Simulator. Triggers the full GPS processing pipeline.

```json
{
  "vehicleId": 5,
  "latitude": 12.9716, "longitude": 77.5946,
  "timestamp": "2026-04-01T10:30:00",
  "speedKmH": 85.5,
  "status": "DRIVING",
  "routeName": "NH-44 Route",
  "isHighway": true
}
```

**What happens internally:** Validate → Detect highway → Haversine distance → Deduct toll → Save GPS ping → Check anomalies → Return response.

**Success (201):**
```json
{ "success": true, "message": "GPS data received and processed successfully", "locationId": 142 }
```

**Error Responses:**

| Code | Body |
|------|------|
| 400 | `{ "success": false, "message": "<runtime error>" }` |
| 400 | `{ "success": false, "message": "Validation failed", "errors": { "vehicleId": "must not be null" } }` |
| 500 | `{ "success": false, "message": "Internal Server Error: <message>" }` |

---

## 10. Wallets — `/api/wallets`

### GET `/api/wallets/user/{userId}`
**Success (200):**
```json
{
  "walletId": 1, "balance": 3450.75, "minimumBalance": 0.0,
  "createdAt": "2026-01-01T00:00:00",
  "lastUpdated": "2026-04-01T10:30:00",
  "inDeficit": false
}
```
**Errors:** `404` — empty body.

---

### POST `/api/wallets/user/{userId}/recharge-request` — Request Top-Up
```json
Request: { "amount": 500.0 }
```
**Success (200):**
```json
{
  "success": true,
  "message": "Recharge request submitted to admin for approval.",
  "upiReference": "UPI1714567890123"
}
```
**Errors:** `404 { "success": false, "message": "User not found" }`

---

### GET `/api/wallets/user/{userId}/recharge-requests` — Recharge History
**Success (200):**
```json
[{
  "requestId": 1,
  "user": { "userId": 1, "name": "John Doe", "email": "john@example.com", "phoneNumber": "9876543210", "createdAt": "..." },
  "amount": 500.0, "status": "PENDING",
  "requestDate": "2026-04-01T10:00:00",
  "processedDate": null,
  "upiReference": "UPI1714567890123"
}]
```

---

## 11. Bills — `/api/bills`

### GET `/api/bills` — All Bills (Admin/Diagnostic)
**Success (200):**
```json
[{
  "billId": 1, "userId": 1, "vehicleId": null,
  "totalDistance": 145.5, "totalAmount": 362.75,
  "billMonth": "2026-03", "dueDate": "2026-04-15",
  "status": "PENDING", "createdAt": "2026-04-01T00:00:00",
  "autoDeductAttempted": false
}]
```

---

### GET `/api/bills/user/{userId}` — User's Bills
**Success (200):** Array of Bill objects for that user across all months.

---

### POST `/api/bills/{billId}/pay` — Pay Bill from Wallet
**Success (200):** `{ "success": true, "message": "Bill paid successfully!" }`

**Error Responses:**

| Code | Body |
|------|------|
| 404 | `{ "success": false, "message": "Bill not found." }` |
| 400 | `{ "success": false, "message": "Bill is already paid." }` |
| 400 | `{ "success": false, "message": "Insufficient wallet balance." }` |

---

## 12. Notifications — `/api/notifications`

> ✅ **Correct base path: `/api/notifications`** — not `/api/user-notifications`

### GET `/api/notifications/user/{userId}` — All Notifications
**Success (200):** Array of UserNotification objects (newest first):
```json
[{
  "id": 1, "userId": 1,
  "title": "✅ Vehicle Add Request Approved",
  "message": "Your vehicle TN01AB1234 has been registered successfully.",
  "read": false,
  "createdAt": "2026-04-01T10:00:00"
}]
```

---

### GET `/api/notifications/user/{userId}/unread-count`
```json
Success (200): { "count": 3 }
```
> This is the endpoint polled every **30 seconds** by `Header.jsx` for the bell badge count.

---

### PUT `/api/notifications/{id}/read` — Mark One Read
**Success (200):** Updated UserNotification with `"read": true`.
**Errors:** `404` — empty body.

---

### PUT `/api/notifications/user/{userId}/read-all` — Mark All Read
```json
Success (200): { "marked": 5 }
```

---

## 13. Admin Operations — `/api/admin`

### GET `/api/admin/stats`
```json
{
  "totalUsers": 42,
  "totalVehicles": 87,
  "totalWallets": 42,
  "totalHighwayUsageSessions": 318,
  "totalBills": 95
}
```

---

### GET `/api/admin/vehicles` — Enriched Vehicle List
```json
[{
  "vehicleId": 1, "vehicleNumber": "TN01AB1234",
  "vehicleType": "CAR", "status": "ACTIVE",
  "registeredAt": "2026-01-01T00:00:00",
  "ownerName": "John Doe", "ownerEmail": "john@example.com"
}]
```

---

### POST `/api/admin/seed-wallets`
```json
Success (200): { "message": "Wallet seeding complete!", "walletsUpdated": 12 }
```

---

### POST `/api/admin/populate-usage` — Seed Test Data
```json
Success (200): { "message": "Usage data population complete!", "recordsCreated": 50 }
```

---

### POST `/api/admin/generate-bills` — Trigger Monthly Bill Generation
```json
Success (200): { "message": "Monthly bill generation triggered successfully!" }
```

---

### POST `/api/admin/generate-bill/user/{userId}`
**Success (200):** `{ "success": true, "message": "Bill generated for User 1", "billId": 42 }`
**Errors:** `204 { "success": false, "message": "No usage found or bill already exists for User 1" }`

---

### POST `/api/admin/generate-bill/vehicle/{vehicleId}`
**Success (200):** `{ "success": true, "message": "Bill generated for Vehicle 5", "billId": 43 }`
**Errors:** `204 { "success": false, "message": "No usage found or bill already exists for Vehicle 5" }`

---

### POST `/api/admin/generate-all-vehicle-bills`
```json
{ "success": true, "message": "Completed bulk vehicle bill generation.", "billsGenerated": 18 }
```

---

### GET `/api/admin/wallets/recharge-requests`
**Success (200):** Array of all PENDING WalletRechargeRequest objects.

---

### POST `/api/admin/wallets/recharge-requests/{id}/{action}`
`action` = `approve` or `decline`

**Success (200):**
```json
{ "success": true, "message": "Request approved and wallet updated." }
// or
{ "success": true, "message": "Request declined." }
```

**Error Responses:**

| Code | Body |
|------|------|
| 404 | `{ "success": false, "message": "Request not found." }` |
| 400 | `{ "success": false, "message": "Request is already processed." }` |
| 400 | `{ "success": false, "message": "Invalid action." }` |

---

### POST `/api/admin/wallets/user/{userId}/topup` — Manual Top-Up
```json
Request:  { "amount": 1000.0 }
Success:  { "success": true, "message": "₹1000.0 added to user wallet successfully." }
Errors:   { "success": false, "message": "Wallet not found for user." }
```

---

## 14. DB Explorer — `/api/db-explorer` (Dev Only)

### GET `/api/db-explorer/tables`
```json
[
  { "key": "users", "label": "📊 Users", "endpoint": "/db-explorer/table/users" },
  { "key": "vehicles", "label": "📊 Vehicles", "endpoint": "/db-explorer/table/vehicles" }
]
```

### GET `/api/db-explorer/table/{tableName}`
**Success (200):** Array of raw row maps (up to 100 rows) — column names as JSON keys.
**Errors:** `500` — IllegalArgumentException if `tableName` contains non-alphanumeric characters (SQL injection protection).

> ⚠️ This endpoint is for development use only. Remove or secure before any production deployment.

---

## 15. Simulator Endpoints (Port 8082)

### POST `/api/simulation/start-all`
```json
{
  "success": true, "started": 28, "alreadyRunning": 4,
  "totalActive": 32, "message": "28 vehicle(s) started, 4 already running."
}
```

---

### POST `/api/simulation/stop-all`
```json
{
  "success": true, "stopped": 30,
  "totalActive": 0, "message": "30 vehicle(s) stopped."
}
```

---

### GET `/api/simulation/status`
```json
{
  "activeCount": 12, "parkedCount": 20,
  "activeVehicleIds": [1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23],
  "totalVehicles": 32
}
```

---

### GET `/api/simulation/vehicles` — Full Vehicle + Live Data List
Polled every 2 seconds by the simulator dashboard. Also used by the main frontend.
```json
[
  {
    "vehicleId": 1, "vehicleNumber": "TN01AB1234",
    "vehicleType": "CAR", "ownerName": "John Doe",
    "iotStatus": "RUNNING",
    "latitude": 12.9716, "longitude": 77.5946,
    "speedKmH": 85.5, "status": "DRIVING",
    "routeName": "NH-44 Bangalore to Salem",
    "isOnHighway": true,
    "timestamp": "2026-04-01T10:30:00"
  },
  {
    "vehicleId": 2, "vehicleNumber": "TN02CD5678",
    "vehicleType": "TRUCK", "ownerName": "Jane Smith",
    "iotStatus": "PARKED",
    "latitude": null, "longitude": null,
    "speedKmH": null, "status": "PARKED",
    "routeName": "—", "isOnHighway": false,
    "timestamp": null
  }
]
```

---

### GET `/api/iot/live-locations` — All Active Vehicles (for Main Frontend Map)
Polled every 3 seconds by `VehicleTrackingModal.jsx`.
```json
[{
  "vehicleId": 1, "vehicleNumber": "TN01AB1234",
  "latitude": 12.9716, "longitude": 77.5946,
  "speedKmH": 85.5, "status": "DRIVING",
  "routeName": "NH-44 Bangalore to Salem",
  "isOnHighway": true,
  "timestamp": "2026-04-01T10:30:15"
}]
```

---

### GET `/api/simulation/settings`
```json
{
  "speedMultiplier": 1.0,
  "tickIntervalMs": 1000,
  "broadcastEnabled": true,
  "routeCount": 5
}
```

### POST `/api/simulation/settings`
```json
Request:  { "speedMultiplier": 2.0, "tickIntervalMs": 500, "broadcastEnabled": true }
Success (200): Updated settings object (same shape as GET)
```

---

### H2 Console (Dev Only)
**URL:** `http://localhost:8082/h2-console`
- **JDBC URL:** `jdbc:h2:file:./data/iot_simulator_db`
- **Username:** `sa`
- **Password:** `password`

---

## 🔢 HTTP Status Code Reference

| Code | When |
|------|------|
| `200 OK` | GET / PUT success |
| `201 Created` | POST creates a new record |
| `204 No Content` | DELETE success or no-op |
| `400 Bad Request` | Validation failure, missing fields, business rule violation |
| `401 Unauthorized` | Login credentials incorrect |
| `404 Not Found` | Entity ID does not exist |
| `500 Internal Server Error` | Unexpected exception |

---

## 🧪 Quick End-to-End Test Sequence

```
1. POST /api/users                              → Create a user
2. POST /api/highways                           → Create a highway
3. POST /api/admin/seed-wallets                 → Initialize wallets
4. POST /api/vehicle-requests                   → Submit ADD request
5. PUT  /api/vehicle-requests/1/approve         → Admin approves
6. POST /api/iot/data                           → First GPS ping
7. POST /api/iot/data (different coordinates)   → Second GPS ping
8. GET  /api/highway-usage/summary/1            → Check distance
9. GET  /api/wallets/user/1                     → Check wallet deduction
10. GET /api/notifications/user/1               → Check notifications
```

---

*API Reference — Smart Highway Tolling System*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
