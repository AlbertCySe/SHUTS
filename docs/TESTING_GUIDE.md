# 🧪 Testing Guide
### Smart Highway Usage-Based Tolling System

> **Back to:** [README.md](../README.md) | **Related:** [API Reference](API_REFERENCE.md) · [Troubleshooting](TROUBLESHOOTING.md)

---

## 📌 Before You Start Testing

### Prerequisites Checklist

Before running any test, ensure the following:

| Requirement | How to Verify |
|-------------|--------------|
| MySQL is running | Open `services.msc` (Windows) — MySQL80 should show "Running" |
| Backend is started | Terminal shows `Started TollingSystemApplication` |
| Frontend is running | http://localhost:3000 loads the home page |
| Database is seeded | At least one highway exists in the `highways` table |

### Recommended Testing Order

Always follow this order — each scenario builds on the previous one:

```
Scenario 1 → Registration Flow        (creates users and vehicles)
Scenario 2 → IoT Simulation Flow      (requires approved vehicles from Scenario 1)
Scenario 3 → Tolling & Billing Flow   (requires active simulation from Scenario 2)
Scenario 4 → Fraud Detection Flow     (requires running simulation to stop)
Scenario 5 → Smart Request Workflow   (requires existing vehicles from Scenario 1)
Scenario 6 → Profile Update Request   (requires existing users from Scenario 1)
```

---

## 🧪 Scenario 1: The Registration Flow

**What This Tests:** Admin approval queue, wallet auto-seeding, vehicle lifecycle kickoff.

### Pre-Conditions
- Backend and frontend are running
- At least one highway has been created via `POST /api/highways`
- No user account exists yet (fresh test) or you are creating a new one

### Steps

**Step 1.1 — Create a User**
```
1. Open http://localhost:3000
2. Navigate to the "Users" page from the navigation menu
3. Click "Add User" or the equivalent button
4. Fill in the form:
   - Name: Test User
   - Email: test@example.com
   - Phone: 9876543210
5. Submit the form
```
**Expected:** A new user row appears in the users table. A record is created in the `users` MySQL table.

---

**Step 1.2 — Submit a Vehicle ADD Request**
```
1. Navigate to the "Vehicles" page
2. Click "New Request" or "Register Vehicle"
3. Fill in the request form:
   - Request Type: ADD
   - Vehicle Number: TN01TEST01
   - Vehicle Type: CAR
   - User ID: (the ID of the user you just created)
   - Reason: "Initial registration for testing"
4. Submit the request
```
**Expected:** A pending request appears. No vehicle is added to the main vehicle list yet. A record is created in the `vehicle_requests` table with `status = PENDING`.

---

**Step 1.3 — Admin Approves the Request**
```
1. Navigate to the "Admin Dashboard"
2. Find the "Pending Vehicle Requests" section
3. Locate the ADD request for TN01TEST01
4. Click "Approve"
5. In the notes field, type: "Test vehicle approved for demo"
6. Confirm approval
```
**Expected Results:**
- The request status changes to `APPROVED`
- The vehicle TN01TEST01 appears in the main vehicles list
- A new wallet is automatically created for the user
- A notification is created for the user in `user_notifications`
- The user's notification bell shows a new unread badge

---

**Step 1.4 — Verify Wallet Creation**
```
1. Navigate to the User Dashboard for the test user
2. Check the wallet balance section
```
**Expected:** Wallet shows an initial seeded balance (e.g. ₹1000.00 or whatever the seeding amount is set to).

---

**Step 1.5 — Verify Notification Delivery**
```
1. Click the notification bell icon in the top navigation bar
2. Open the notification dropdown
```
**Expected:** A notification appears saying the ADD request was approved, including the admin notes "Test vehicle approved for demo".

### ✅ Pass Criteria

| Check | Expected |
|-------|---------|
| Vehicle appears in list | ✅ After approval only |
| Wallet created automatically | ✅ With initial balance |
| Notification delivered to user | ✅ With admin notes included |
| Request status in DB | ✅ `APPROVED` |

---

## 🧪 Scenario 2: The IoT Simulation Flow

**What This Tests:** Standalone simulator vehicle sync, live GPS telemetry, real-time dashboard updates.

### Pre-Conditions
- Scenario 1 completed — at least one Admin-approved vehicle exists
- Main backend is running on Port 8080

### Steps

**Step 2.1 — Launch the Standalone IoT Simulator**
```
1. Open a new terminal window (keep the main backend running)
2. Navigate to: Initial/iot-simulator/
3. Run: run-simulator.bat
4. Wait for the simulator backend to start (takes ~10 seconds)
5. The GPS Telemetry Dashboard should open automatically in your browser
```
**Expected:** The dashboard loads at http://localhost:8082. A table of vehicles appears, showing TN01TEST01 with its correct type (CAR) and owner name (Test User).

---

**Step 2.2 — Verify Vehicle Sync**
```
1. In the simulator dashboard, check the vehicles table
2. Confirm that the vehicle registered in Scenario 1 appears here
3. Check that the columns show the correct:
   - Vehicle Number: TN01TEST01
   - Vehicle Type: CAR
   - Owner Name: Test User
```
**Expected:** All three fields match exactly what is in the main database. The simulator synced this data on boot.

---

**Step 2.3 — Track a Vehicle**
```
1. In the simulator dashboard, find TN01TEST01
2. Click the "Track" or "Start Simulation" button for that vehicle
3. Watch the coordinates update live in the dashboard table
```
**Expected:** The `current_latitude`, `current_longitude`, and `current_speed_kmh` columns update every few seconds. The `iotStatus` changes from `PARKED` to `RUNNING`.

---

**Step 2.4 — Verify GPS Data in Main Backend**
```
1. Open MySQL Workbench or run a query:
   SELECT * FROM location_tracking ORDER BY id DESC LIMIT 10;
```
**Expected:** New rows are appearing in the `location_tracking` table with the vehicle's coordinates and timestamps. Rows arrive in near real-time.

### ✅ Pass Criteria

| Check | Expected |
|-------|---------|
| Simulator shows approved vehicles | ✅ With correct Number, Type, Owner |
| GPS rows appearing in MySQL | ✅ In `location_tracking` table |
| Simulator status updates live | ✅ PARKED → RUNNING |

---

## 🧪 Scenario 3: The Tolling & Billing Flow

**What This Tests:** Wallet deduction in real-time, highway detection, distance accumulation, monthly billing.

### Pre-Conditions
- Scenario 2 is running (GPS simulation active)
- A highway has been registered with GPS boundaries that overlap the simulated vehicle's route
- The user's wallet has a balance

### Steps

**Step 3.1 — Watch Real-Time Wallet Deduction**
```
1. Open the User Dashboard (http://localhost:3000)
2. Navigate to the wallet/balance section for Test User
3. Note the current balance
4. Wait 30-60 seconds while the simulation is running
5. Refresh the wallet display or wait for auto-refresh
```
**Expected:** The wallet balance decreases as the simulated vehicle travels across the highway. The amount deducted corresponds to: distance × rate per km for CAR type on that highway.

---

**Step 3.2 — Verify Distance Accumulation**
```
1. Call the API in your browser or Postman:
   GET http://localhost:8080/api/highway-usage/total/1
   (replace 1 with the actual vehicle ID)
```
**Expected:** Returns a JSON with `totalDistanceKm` that increases over time while the simulation runs.

---

**Step 3.3 — Verify Highway Usage Sessions**
```
SQL query:
SELECT * FROM highway_usage WHERE vehicle_id = 1 ORDER BY entry_time DESC;
```
**Expected:** One or more session rows. Sessions with `exit_time = NULL` are still open (vehicle still on highway). Closed sessions have both entry and exit times filled.

---

**Step 3.4 — Trigger Monthly Bill Generation**

> The monthly bill is normally generated automatically by the Spring Scheduler at month-end. For testing, you can either wait or trigger it manually.

```
Option A — Wait for the scheduler to run (default: end of month)

Option B — Reduce the scheduler interval temporarily in the service class
and restart the backend to trigger an immediate bill generation.

Option C — Call the billing trigger directly if an admin endpoint exists:
POST http://localhost:8080/api/admin/generate-bills
(confirm this endpoint exists via Antigravity Prompt #3)
```
**Expected:** After bill generation, a record appears in the `bills` table for the test user and vehicle, with the correct month, total distance, and total amount.

### ✅ Pass Criteria

| Check | Expected |
|-------|---------|
| Wallet balance decreasing | ✅ In real time while on highway |
| `highway_usage` session records | ✅ Entry/exit tracked correctly |
| `total_distance_km` accumulating | ✅ Via API and in DB |
| Monthly bill generated | ✅ In `bills` table with correct amounts |

---

## 🧪 Scenario 4: The Fraud Detection Flow

**What This Tests:** Anomaly detection engine, automated alert pipeline, notification bell.

### Pre-Conditions
- Scenario 2 completed (simulation was running)
- Vehicle has sent GPS data recently

### Steps

**Step 4.1 — Simulate a GPS Disconnection**
```
1. Close the IoT Simulator terminal window abruptly (do NOT use a graceful stop)
   OR stop sending GPS pings via the simulator dashboard
2. This simulates a real-world scenario:
   - GPS device battery died
   - GPS signal lost (tunnel, interference)
   - Device removed to avoid tolling (fraud attempt)
```

---

**Step 4.2 — Wait for Detection (or Reduce Threshold)**

The default detection threshold is 2 hours of missing GPS data. For faster testing:
```
1. Open: src/main/java/com/highway/tolling/service/AnomalyDetectionService.java
2. Find the MISSING_DATA threshold constant (currently set to 2 hours)
3. Temporarily change it to 2 minutes for testing
4. Restart the backend
5. Wait 2 minutes after stopping the simulator
```

---

**Step 4.3 — Check the Notification Bell**
```
1. Go to http://localhost:3000
2. Look at the notification bell icon in the top navigation bar
3. Wait for the 30-second poll to complete (or trigger a page refresh)
```
**Expected:** The bell shows a new unread badge. Clicking it reveals a notification such as:
> "ANOMALY DETECTED: Vehicle TN01TEST01 has not sent GPS data for over 2 hours. Possible GPS disconnection or device tampering."

---

**Step 4.4 — Verify Anomaly Record in Database**
```
SQL query:
SELECT * FROM data_anomalies ORDER BY detected_at DESC LIMIT 5;
```
**Expected:** A new row with:
- `type = 'MISSING_DATA'`
- `severity = 'HIGH'`
- `vehicle_id` matching TN01TEST01
- `detected_at` timestamp close to when you stopped the simulation

### ✅ Pass Criteria

| Check | Expected |
|-------|---------|
| Anomaly logged in DB | ✅ In `data_anomalies` table |
| User notification created | ✅ In `user_notifications` table |
| Bell shows unread badge | ✅ Within 30 seconds of notification creation |
| No manual admin step needed | ✅ Fully automated pipeline |

---

## 🧪 Scenario 5: Smart Request Workflow (SELL)

**What This Tests:** Vehicle lifecycle request queue, admin approval with notes, notification delivery with admin message.

### Pre-Conditions
- Two users exist (User A and User B)
- User A owns an approved vehicle

### Steps

**Step 5.1 — Submit a SELL Request**
```
1. Navigate to the Vehicles page
2. Find the vehicle owned by User A
3. Submit a new request:
   - Request Type: SELL
   - Vehicle ID: (ID of User A's vehicle)
   - New Owner User ID: (ID of User B)
   - Reason: "Selling to colleague — both parties agreed"
4. Submit
```
**Expected:** Request created with `status = PENDING`. Vehicle ownership has NOT changed yet.

---

**Step 5.2 — Admin Reviews and Rejects (Test Rejection Path)**
```
1. Go to Admin Dashboard → Pending Vehicle Requests
2. Find the SELL request
3. Click "Reject"
4. Enter admin notes: "Insufficient documentation. Please provide transfer agreement."
5. Confirm rejection
```
**Expected:** Request status → `REJECTED`. Vehicle ownership unchanged.

---

**Step 5.3 — Verify Rejection Notification**
```
1. Log in as User A (or view User A's notifications)
2. Check the notification bell
```
**Expected:** Notification reads: "Your SELL request has been REJECTED. Admin note: Insufficient documentation. Please provide transfer agreement."

---

**Step 5.4 — Resubmit and Approve**
```
1. Resubmit the SELL request
2. In Admin Dashboard, approve it with notes: "Transfer approved after document review."
3. Verify ownership transfer: vehicle now shows User B as owner
4. Verify User A receives approval notification with admin notes
```

### ✅ Pass Criteria

| Check | Expected |
|-------|---------|
| Ownership unchanged before approval | ✅ |
| Rejection notification with exact notes | ✅ |
| Approval changes ownership in DB | ✅ |
| Approval notification delivered | ✅ With admin notes |

---

## 🧪 Scenario 6: Profile Update Request

**What This Tests:** Profile change governance, admin approval, profile data update.

### Steps

**Step 6.1 — Attempt Direct Edit (Should Be Blocked)**
```
1. Navigate to the Users page or User profile
2. Try to directly edit your name or email
```
**Expected:** Direct editing is blocked by the UI. No direct edit form is available for sensitive fields. Only a "Request Profile Update" option exists.

---

**Step 6.2 — Submit a Profile Update Request**
```
1. Click "Request Profile Update"
2. Fill in the new details:
   - New Name: Albert Joseph
   - New Email: albert.joseph@example.com
   - (Leave phone blank to keep it unchanged)
3. Submit the request
```
**Expected:** Request created with `status = PENDING`. User's name and email are still the old values.

---

**Step 6.3 — Admin Approves**
```
1. Admin Dashboard → Profile Requests section
2. Locate the pending request
3. Approve with notes: "Identity verified. Update approved."
```
**Expected:** User's name and email are now updated in the `users` table.

---

**Step 6.4 — Verify Notification**
```
Check the user's notification bell.
```
**Expected:** Notification: "Your profile update request has been approved. Admin note: Identity verified. Update approved."

### ✅ Pass Criteria

| Check | Expected |
|-------|---------|
| Direct edit blocked | ✅ No direct edit option |
| Profile unchanged before approval | ✅ |
| Profile updated after approval | ✅ Name and email changed in DB |
| Notification includes admin notes | ✅ |

---

## 🔬 Database Verification Queries

Use these SQL queries in MySQL Workbench or the MySQL CLI to verify data at any point during testing:

```sql
-- Check all users:
SELECT user_id, name, email FROM users;

-- Check all vehicles and their owners:
SELECT v.vehicle_id, v.vehicle_number, v.vehicle_type, u.name AS owner
FROM vehicles v JOIN users u ON v.user_id = u.user_id;

-- Check wallet balances:
SELECT u.name, w.balance FROM wallets w JOIN users u ON w.user_id = u.user_id;

-- Check last 10 GPS pings:
SELECT * FROM location_tracking ORDER BY id DESC LIMIT 10;

-- Check open highway sessions (vehicle still on highway):
SELECT * FROM highway_usage WHERE exit_time IS NULL;

-- Check all pending requests:
SELECT * FROM vehicle_requests WHERE status = 'PENDING';

-- Check unread notifications for user ID 1:
SELECT * FROM user_notifications WHERE user_id = 1 AND is_read = false;

-- Check recent anomalies:
SELECT * FROM data_anomalies ORDER BY detected_at DESC LIMIT 5;
```

---

## ⚡ Quick API Test (Without Frontend)

If you want to test purely via API calls (Postman, curl, or browser):

```bash
# 1. Create a user:
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","phoneNumber":"9876543210"}'

# 2. Create a highway:
curl -X POST http://localhost:8080/api/highways \
  -H "Content-Type: application/json" \
  -d '{"highwayName":"NH-44","startLatitude":12.97,"startLongitude":77.59,"endLatitude":13.07,"endLongitude":77.69,"ratePerKmForCar":2.5,"ratePerKmForBike":1.5,"ratePerKmForBus":5.0,"ratePerKmForTruck":5.0}'

# 3. Seed wallets:
curl -X POST http://localhost:8080/api/admin/seed-wallets

# 4. Send GPS ping:
curl -X POST http://localhost:8080/api/iot/data \
  -H "Content-Type: application/json" \
  -d '{"vehicleId":1,"latitude":12.980,"longitude":77.600,"timestamp":"2026-05-01T14:30:00"}'
```

---

*Testing Guide — Smart Highway Tolling System*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
