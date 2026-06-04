# 🐛 Troubleshooting Guide
### Smart Highway Usage-Based Tolling System

> **Back to:** [README.md](../README.md) | **Related:** [Setup Guide](SETUP_GUIDE.md) · [Manual Setup](MANUAL_SETUP.md)

---

## 📌 How to Use This Guide

Find your symptom in the Quick Reference table, then jump to the detailed section.

### Quick Reference

| Symptom / Error | Section |
|----------------|---------|
| `mvn: command not found` | [#1](#issue-1-maven-not-found) |
| `npm` / `node` not found | [#2](#issue-2-nodejs-not-found) |
| MySQL connection refused / Access denied | [#3](#issue-3-mysql-connection-failed) |
| Port 8080 already in use | [#4](#issue-4-port-8080-already-in-use) |
| Port 3000 already in use | [#5](#issue-5-port-3000-already-in-use) |
| `npm install` fails | [#6](#issue-6-npm-install-fails) |
| CORS error in browser console | [#7](#issue-7-cors-errors) |
| Map / route visualization blank | [#8](#issue-8-map-visualization-not-rendering) |
| Notification bell not updating | [#9](#issue-9-notification-bell-not-working) |
| Vehicle request stuck in PENDING | [#10](#issue-10-request-stuck-in-pending) |
| Simulator shows no vehicles | [#11](#issue-11-simulator-shows-no-vehicles) |
| No GPS data in `location_tracking` | [#12](#issue-12-no-gps-data-being-stored) |
| Wallet balance not changing | [#13](#issue-13-wallet-not-deducting) |
| Backend crashes on startup | [#14](#issue-14-backend-crashes-on-startup) |
| `tolling_system` database not created | [#15](#issue-15-database-not-created) |
| Frontend shows blank white page | [#16](#issue-16-frontend-blank-page) |
| Direct page links return 404 on deployment | [#17](#issue-17-spa-routing-404s-on-deployment) |
| Anomaly detection never fires | [#18](#issue-18-anomaly-detection-not-firing) |
| Monthly bills generated twice | [#19](#issue-19-duplicate-bills-generated) |
| Simulator crashes after vehicle deleted | [#20](#issue-20-simulator-crashes-after-vehicle-deleted) |

---

## Issue 1: Maven Not Found

**Symptom:**
```
'mvn' is not recognized as an internal or external command
bash: mvn: command not found
```

**Fix A — Automated (Windows):**
```bash
fix-maven.bat
```
Downloads, installs Maven, and adds it to your current session PATH.

**Fix B — Manual:**
```bash
# 1. Download: https://maven.apache.org/download.cgi
#    Choose: Binary zip archive (e.g. apache-maven-3.9.x-bin.zip)
# 2. Extract to: C:\Program Files\Apache\maven\
# 3. Add C:\...\maven\bin to System PATH → restart terminal
mvn -v   # Verify
```

**Fix C — Use Maven Wrapper (no install needed):**
```bash
# Windows:
mvnw.cmd spring-boot:run

# macOS / Linux:
./mvnw spring-boot:run
```

---

## Issue 2: Node.js Not Found

**Symptom:**
```
'node' is not recognized as an internal or external command
'npm' is not recognized as an internal or external command
```

**Fix:**
```bash
# Windows automated:
install-nodejs.bat

# Manual: https://nodejs.org/ → LTS → install → restart terminal
node -v    # v18.x.x or higher
npm -v
```

---

## Issue 3: MySQL Connection Failed

**Symptoms:**
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
java.sql.SQLException: Access denied for user 'root'@'localhost'
Connection refused to host: localhost, port: 3306
```

**Fix A — MySQL service not running:**
```bash
# Windows: Win+R → services.msc → MySQL80 → Start
# macOS:   brew services start mysql
# Linux:   sudo systemctl start mysql
```

**Fix B — Wrong credentials in `.env`:**
```bash
# Open your .env file and verify:
DB_USERNAME=root
DB_PASSWORD=your_actual_password

# Common mistakes:
# Quotes around value:  DB_PASSWORD="pass"   ← WRONG
# Spaces around =:      DB_PASSWORD = pass   ← WRONG
# Wrong password — verify manually:
mysql -u root -p    # try your password here
```

**Fix C — MySQL port is not 3306:**
```properties
# In application.properties, change the URL:
spring.datasource.url=jdbc:mysql://localhost:YOUR_PORT/tolling_system?createDatabaseIfNotExist=true
```

---

## Issue 4: Port 8080 Already in Use

**Symptom:**
```
Web server failed to start. Port 8080 was already in use.
```

**Fix A — Kill the process:**
```bash
# Windows:
netstat -ano | findstr :8080
taskkill /PID <pid> /F

# macOS / Linux:
lsof -ti:8080 | xargs kill -9
```

**Fix B — Change the backend port:**
```properties
# application.properties:
server.port=8081
```
Then update `frontend/vite.config.js`:
```javascript
proxy: { '/api': { target: 'http://localhost:8081', changeOrigin: true } }
```

---

## Issue 5: Port 3000 Already in Use

```bash
# Windows:
netstat -ano | findstr :3000
taskkill /PID <pid> /F

# OR change in vite.config.js:
server: { port: 3001 }
```

---

## Issue 6: npm Install Fails

**Fix A — Clear cache and reinstall:**
```bash
cd Initial/frontend
npm cache clean --force
rd /s /q node_modules     # Windows
rm -rf node_modules       # macOS/Linux
del package-lock.json     # Windows
rm package-lock.json      # macOS/Linux
npm install
```

**Fix B — Corporate/college proxy:**
```bash
npm config set proxy http://your-proxy-server:port
npm config set https-proxy http://your-proxy-server:port
npm install
```

**Fix C — Node.js version too old:**
```bash
node -v   # Must be 18.x.x or higher
# If lower: run install-nodejs.bat or download from nodejs.org
```

---

## Issue 7: CORS Errors

**Symptom (browser console):**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/...' from origin
'http://localhost:3000' has been blocked by CORS policy
```

**Fix A — Backend not running:**
Most common cause. The frontend can't reach the backend.
```bash
curl http://localhost:8080/api/users
# If this fails — start the backend first
```

**Fix B — Wrong proxy config in vite.config.js:**
```javascript
// Verify this is present:
proxy: {
  '/api': { target: 'http://localhost:8080', changeOrigin: true }
}
```

**Root cause (already fixed in code):** `CorsConfig.java` explicitly permits `*` origins and all HTTP methods on `/api/**`. If you ever see CORS errors, the backend is most likely not running.

---

## Issue 8: Map Visualization Not Rendering

**Symptom:** GPS route map area appears blank.

**Fix A — No GPS data exists yet:**
```sql
SELECT COUNT(*) FROM location_tracking WHERE vehicle_id = 1;
-- If 0: start the IoT simulator first
```

**Fix B — Google Maps iframe blocked:**
`VehicleTrackingModal.jsx` uses a Google Maps embed iframe. Some networks block iframe embeds.
- Try opening the map in a different browser
- Try disabling browser extensions that block iframes

**Fix C — JavaScript error:**
```
1. F12 → Console tab → look for red errors
2. Common: "vehicleId is undefined" → check the vehicle ID being passed to the modal
```

---

## Issue 9: Notification Bell Not Working

**Symptom:** Bell shows no badge even after admin action.

**Fix A — Wrong API path:**
```
⚠️ CRITICAL: Correct path is /api/notifications
             NOT /api/user-notifications (missing prefix!)

Test:
GET http://localhost:8080/api/notifications/user/1
```

**Fix B — Wrong user ID passed:**
```
1. F12 → Network tab → filter by "notifications"
2. Wait 30 seconds — watch for the polling request
3. Check the URL: /api/notifications/user/{id} — is {id} correct?
```

**Fix C — Polling not running:**
```
1. F12 → Network tab → filter "unread-count"
2. Wait 30+ seconds — a request should fire every 30s
3. If none: check Header.jsx for the setInterval/useEffect
```

**Fix D — Notification not created in DB:**
```sql
SELECT * FROM user_notifications WHERE user_id = 1 ORDER BY created_at DESC LIMIT 5;
-- If empty after admin action: check VehicleRequestService.approve() method
```

---

## Issue 10: Request Stuck in Pending

```
1. Go to Admin Dashboard → Pending Requests section
2. Find the request → Approve or Reject it

If not visible in Admin UI despite being in DB:
SELECT * FROM vehicle_requests WHERE status = 'PENDING';
-- Check browser console for errors loading the admin requests component
```

---

## Issue 11: Simulator Shows No Vehicles

**Root cause:** Simulator syncs vehicles from the main backend on boot.

**Fix A — Main backend not running when simulator started:**
```
1. Start main backend FIRST (wait for "Started TollingSystemApplication")
2. THEN start run-simulator.bat
```

**Fix B — No approved vehicles exist:**
```
Simulator only syncs vehicles from GET /api/vehicles on the main backend.
Complete Scenario 1 in TESTING_GUIDE.md first (create + approve a vehicle).
Then restart the simulator.
```

**Fix C — Verify sync endpoint:**
```bash
curl http://localhost:8080/api/vehicles
# Must return at least one vehicle for the simulator to have something to show
```

---

## Issue 12: No GPS Data Being Stored

**Fix A — Simulator not broadcasting to main backend:**
```
Check simulator's application.properties:
core.api.url=http://localhost:8080/api/iot/data
core.api.base=http://localhost:8080

Check main backend terminal (blue window) for incoming POST /api/iot/data requests.
```

**Fix B — Vehicle ID mismatch:**
```
The simulator sends coreVehicleId values. If these don't match real
vehicle IDs in MySQL, every ping is rejected with a 404.
Restart the simulator after ensuring vehicles are approved — it re-syncs on boot.
```

---

## Issue 13: Wallet Not Deducting

**Fix A — Vehicle not crossing a highway boundary:**
```sql
SELECT is_on_highway, COUNT(*) FROM location_tracking
WHERE vehicle_id = 1 GROUP BY is_on_highway;
-- If all is_on_highway = false: the GPS route doesn't cross any highway boundary.
-- Register a highway whose GPS bounds overlap the simulated route.
```

**Fix B — No highways registered:**
```sql
SELECT * FROM highways;
-- If empty: POST /api/highways with coordinates matching the simulator route
```

---

## Issue 14: Backend Crashes on Startup

**Symptom:**
```
APPLICATION FAILED TO START
Description: Failed to configure a DataSource
```
→ MySQL is not running or credentials are wrong. See [Issue 3](#issue-3-mysql-connection-failed).

---

## Issue 15: Database Not Created

**Symptom:** `Unknown database 'tolling_system'`

**Fix:**
```sql
mysql -u root -p
CREATE DATABASE IF NOT EXISTS tolling_system;
EXIT;
```
Then restart the backend — tables are created automatically.

> **Why this happens:** The `createDatabaseIfNotExist=true` JDBC parameter normally handles this, but it can fail on some MySQL configurations where the user lacks `CREATE DATABASE` permissions.

---

## Issue 16: Frontend Blank Page

**Fix A — JavaScript error on load:**
```
F12 → Console → look for red errors → fix the import path or missing component
```

**Fix B — Dependencies not installed:**
```bash
cd Initial/frontend && npm install && npm run dev
```

---

## Issue 17: SPA Routing 404s on Deployment

**Symptom:** Direct links (e.g. `/admin` or `/dashboard`) return a 404 when deployed to GitHub Pages or a static server.

**Root cause:** GitHub Pages treats URL paths as actual directory paths. If no `.html` file exists at `/admin/`, it returns a 404.

**Fix (already implemented):** A custom `docs/404.html` file exists in the repository containing a JavaScript redirect. It reads the URL path, converts it to a query string, bounces back to `index.html`, and lets React Router re-parse the route client-side.

**For the GitHub Pages build:** Run:
```bash
npm run build -- --config vite.config.github.js
```
This uses `vite.config.github.js` which sets `base: './'` for relative asset paths and outputs to `docs/main-app/`.

---

## Issue 18: Anomaly Detection Not Firing

**Symptom:** Stopped the simulator but no anomaly notification appeared.

**Root cause:** Default detection threshold is 2 hours of missing GPS data — too long for testing.

**Fix for testing (temporary):**
```java
// In AnomalyDetectionService.java, find the missing data threshold constant:
// Change from 2 hours to 2 minutes temporarily:
private static final long MISSING_DATA_THRESHOLD_MINUTES = 2; // was 120

// Also for inactivity detection, the vehicle must move < 50m in 30 minutes
// Reduce the 30-minute window for faster testing
```
Restart the backend after making this change. Remember to revert before submission.

**Also verify:**
```sql
-- Check if anomaly was detected but notification wasn't created:
SELECT * FROM data_anomalies ORDER BY detected_at DESC LIMIT 5;
SELECT * FROM user_notifications ORDER BY created_at DESC LIMIT 5;
```

---

## Issue 19: Duplicate Bills Generated

**Symptom:** A user has two bills for the same month.

**Root cause:** This was a known bug during development — the scheduler could generate duplicate bills if triggered multiple times in the same month.

**Fix (already implemented in code):** The `MonthlyBillingScheduler` now calls `getBillByVehicleAndMonth()` before generating. If a bill for the same vehicle + same `billMonth` (e.g. `"2026-04"`) already exists, generation is skipped. The `auto_deduct_attempted` flag also prevents the `BillDeductionScheduler` from deducting the same bill twice.

**If you see duplicates in your database:**
```sql
-- Find duplicates:
SELECT user_id, bill_month, COUNT(*) as count
FROM bills
GROUP BY user_id, bill_month
HAVING count > 1;

-- Delete the duplicate (keep the lower bill_id):
DELETE FROM bills WHERE bill_id = <higher_duplicate_id>;
```

---

## Issue 20: Simulator Crashes After Vehicle Deleted

**Symptom:** Simulator throws a NullPointerException or HTTP 404 when trying to broadcast GPS data for a vehicle that was deleted from the main backend.

**Root cause:** The simulator keeps vehicles in its in-memory `ActiveVehicleRegistry` even after they are deleted from MySQL.

**Fix:** Restart the simulator. On boot, `syncLocalDbWithMainBackend()` will detect the missing vehicle (it no longer appears in `GET /api/vehicles`) and remove it from the H2 `simulated_vehicles` table and the active registry.

**Permanent fix (future enhancement):** Add a webhook or polling check in the simulator to detect deleted vehicles and stop their simulation automatically without requiring a restart.

---

## 🔬 Advanced Diagnostics

### Check All Services at Once

```bash
curl http://localhost:8080/api/users           # Backend
curl http://localhost:3000                     # Frontend (browser only)
curl http://localhost:8082/api/simulation/status  # Simulator
mysql -u root -p -e "SHOW DATABASES;"         # MySQL
```

### Enable Full SQL Logging (Temporary)

Add to `application.properties` while debugging, remove before submission:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```
> Note: `spring.jpa.show-sql=true` is already set in the current `application.properties`. Set `logging.level.org.hibernate.SQL=DEBUG` for even more detail.

### Useful Diagnostic SQL Queries

```sql
-- Check all users:
SELECT user_id, name, email FROM users;

-- Check vehicles and owners:
SELECT v.vehicle_id, v.vehicle_number, v.vehicle_type, v.status, u.name
FROM vehicles v JOIN users u ON v.user_id = u.user_id;

-- Check wallet balances:
SELECT u.name, w.balance, w.last_updated FROM wallets w JOIN users u ON w.user_id = u.user_id;

-- Last 10 GPS pings:
SELECT * FROM location_tracking ORDER BY id DESC LIMIT 10;

-- Open highway sessions (vehicle still on highway):
SELECT * FROM highway_usage WHERE exit_timestamp IS NULL;

-- All pending requests:
SELECT * FROM vehicle_requests WHERE status = 'PENDING';

-- Unread notifications for user 1:
SELECT * FROM user_notifications WHERE user_id = 1 AND is_read = false;

-- Recent anomalies:
SELECT * FROM data_anomalies ORDER BY detected_at DESC LIMIT 10;

-- Bills this month:
SELECT * FROM bills WHERE bill_month = '2026-05';
```

---

*Troubleshooting Guide — Smart Highway Tolling System*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
