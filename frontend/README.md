# 🚦 Smart Highway Tolling System — Frontend

[![React](https://img.shields.io/badge/React-18.2.0-blue.svg)](https://reactjs.org/)
[![Vite](https://img.shields.io/badge/Vite-5.0.8-purple.svg)](https://vitejs.dev/)
[![React Router](https://img.shields.io/badge/React%20Router-6.20.0-red.svg)](https://reactrouter.com/)
[![Axios](https://img.shields.io/badge/Axios-1.6.2-teal.svg)](https://axios-http.com/)
[![Status](https://img.shields.io/badge/Status-Active%20Development-brightgreen.svg)]()

> The React client application for the Smart Highway Usage-Based Tolling System.
> Built with Vite for lightning-fast HMR. Connects to the Spring Boot backend at `http://localhost:8080`.

---

## 📖 Quick Navigation

| Section | Description |
|---------|-------------|
| [Quick Start](#-quick-start) | Install and run in 3 commands |
| [Technology Stack](#-technology-stack) | Libraries and versions |
| [Application Architecture](#-application-architecture--routing) | All 17 routes explained |
| [Pages Reference](#-pages-reference) | Every page — UI, API calls, state |
| [Components Reference](#-components-reference) | Every reusable component |
| [Services & Hooks](#-services--hooks) | api.js, auth.js, usePagination |
| [CSS Architecture](#-css-architecture) | 9-module styling system |
| [Key Features](#-key-features) | What makes this frontend special |
| [Changes from Previous Version](#-changes-from-previous-version) | What was added, removed, replaced |

---

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ (`node -v` to check)
- Main backend running at `http://localhost:8080` (see root [Setup Guide](../docs/SETUP_GUIDE.md))

### Install & Run

```bash
# Navigate to the frontend directory
cd Initial/frontend

# Install dependencies
npm install

# Start the development server
npm run dev
```

The application will be available at: **http://localhost:3000**

> 💡 **Recommended:** Use `start-project.bat` from the project root to launch the backend and frontend together automatically.

### Available Scripts

| Command | Description |
|---------|-------------|
| `npm run dev` | Start Vite dev server on port 3000 with HMR |
| `npm run build` | Compile optimized production build to `dist/` |
| `npm run preview` | Preview the production build locally |
| `npm run build -- --config vite.config.github.js` | Build for GitHub Pages static deployment |

---

## 🎨 Technology Stack

| Package | Version | Purpose |
|---------|---------|---------|
| `react` | `^18.2.0` | Core UI library |
| `react-dom` | `^18.2.0` | React DOM renderer |
| `react-router-dom` | `^6.20.0` | Client-side routing and navigation |
| `axios` | `^1.6.2` | HTTP client for backend API communication |
| `vite` | `^5.0.8` | Build tool and dev server (replaces Create React App) |
| `@vitejs/plugin-react` | `^4.2.1` | Vite plugin for JSX compilation |
| `@types/react` | — | TypeScript definitions for VS Code autocomplete |
| `@types/react-dom` | — | TypeScript definitions for VS Code autocomplete |

> ⚠️ **Important corrections from old README:**
> - **`react-leaflet` has been removed** — the map feature now uses an embedded Google Maps `<iframe>` inside `VehicleTrackingModal.jsx`
> - **`recharts` has been removed** — admin analytics now use CSS-styled metric cards and data tables instead of chart libraries

---

## 🗂️ Source Code Structure

```
frontend/
├── src/
│   ├── App.jsx                    ← Router tree + auth gateway
│   ├── main.jsx                   ← React DOM entry point
│   ├── index.css                  ← Master CSS importer (9 modules)
│   │
│   ├── services/
│   │   ├── api.js                 ← Pre-configured Axios instance + 5 helper functions
│   │   └── auth.js                ← Login, session management (sessionStorage)
│   │
│   ├── hooks/
│   │   └── usePagination.js       ← Client-side pagination logic
│   │
│   ├── pages/
│   │   ├── Home.jsx               ← Public landing page
│   │   ├── Login.jsx              ← Tabbed user/admin login form
│   │   ├── Register.jsx           ← New user registration
│   │   ├── Users.jsx              ← User profile + update request history
│   │   ├── UserDashboard.jsx      ← User home hub — summary cards + vehicles
│   │   ├── Vehicles.jsx           ← Vehicle list with IoT status indicator
│   │   ├── WalletBills.jsx        ← Wallet + billing management
│   │   ├── TollHistory.jsx        ← Usage analytics and spending breakdown
│   │   ├── AdminDashboard.jsx     ← Admin home — system metrics + deficit alerts
│   │   ├── AdminUsers.jsx         ← User CRUD management
│   │   ├── AdminVehicles.jsx      ← Vehicle management + lifecycle approvals
│   │   ├── AdminHighways.jsx      ← Highway GPS zone + rate management
│   │   ├── AdminBilling.jsx       ← Manual billing trigger panel
│   │   ├── AdminWallets.jsx       ← Recharge request approvals + top-up
│   │   ├── AdminReports.jsx       ← Deep-dive GPS forensics per vehicle
│   │   ├── AdminProfileRequests.jsx ← Profile change approval queue
│   │   └── DbViewer.jsx           ← Dev tool: floating raw DB viewer
│   │
│   ├── components/
│   │   ├── Header.jsx             ← Top nav bar
│   │   ├── NotificationBell.jsx   ← Real-time alert bell (30s polling)
│   │   ├── Paginator.jsx          ← Reusable pagination controls
│   │   ├── LoadingFallback.jsx    ← Suspense spinner
│   │   │
│   │   ├── dashboard/
│   │   │   ├── DashboardSummaryCards.jsx ← 4 metric cards on user dashboard
│   │   │   └── DashboardMyVehicles.jsx   ← Vehicle list on user dashboard
│   │   │
│   │   ├── vehicles/
│   │   │   ├── VehicleTrackingModal.jsx  ← Live GPS tracking overlay (Google Maps)
│   │   │   ├── VehicleTrackingModal.css  ← Modal-specific styles
│   │   │   ├── VehicleTable.jsx          ← User vehicle list table
│   │   │   └── VehicleFilters.jsx        ← Search and type filter bar
│   │   │
│   │   ├── wallet/
│   │   │   ├── WalletCard.jsx     ← Balance display + recharge form
│   │   │   ├── BillGenerator.jsx  ← Bill generation period selector
│   │   │   └── BillsTable.jsx     ← Paginated bills + CSV export + Pay button
│   │   │
│   │   ├── admin/
│   │   │   ├── AdminUsersTable.jsx          ← Paginated user registry table
│   │   │   ├── AdminUserFormModal.jsx        ← Add/Edit user form modal
│   │   │   ├── AdminUserProfileModal.jsx     ← User stats detail view modal
│   │   │   ├── AdminVehiclesTable.jsx        ← Paginated vehicle registry table
│   │   │   ├── AdminVehicleModal.jsx         ← Add/Edit vehicle form modal
│   │   │   ├── AdminVehicleRequests.jsx      ← Lifecycle request approval queue
│   │   │   └── AdminVehicleTrackingModal.jsx ← Admin version of tracking modal
│   │   │
│   │   └── users/
│   │       └── ProfileEditModal.jsx ← Profile update request form
│   │
│   └── styles/
│       ├── global.css    ├── layout.css    ├── forms.css
│       ├── buttons.css   ├── tables.css    ├── badges.css
│       ├── messages.css  ├── vehicles.css  └── wallet.css
│
├── public/                        ← Static assets
├── index.html                     ← HTML shell for React app
├── vite.config.js                 ← Dev server (port 3000) + API proxy
└── package.json                   ← Dependencies and scripts
```

---

## 🛣️ Application Architecture & Routing

### `App.jsx` Route Configuration

`App.jsx` is the root of the application. It defines all routes, handles authentication guards, and wraps the app in `React.Suspense` for lazy-loaded pages.

| Path | Component | Who Can Access | Lazy Loaded |
|------|-----------|---------------|-------------|
| `/` | `Home` | Public — everyone | ❌ |
| `/login` | `Login` (via `LoginWrapper`) | Public — guests only | ❌ |
| `/register` | `Register` | Public — guests only | ❌ |
| `/users` | `Users` | User, Admin | ❌ |
| `/user-dashboard` | `UserDashboard` | User only | ✅ |
| `/vehicles` | `Vehicles` | User, Admin | ✅ |
| `/wallet-bills` | `WalletBills` | User only | ✅ |
| `/toll-history` | `TollHistory` | User only | ✅ |
| `/admin` | `AdminDashboard` | Admin only | ✅ |
| `/admin/users` | `AdminUsers` | Admin only | ✅ |
| `/admin/vehicles` | `AdminVehicles` | Admin only | ✅ |
| `/admin/highways` | `AdminHighways` | Admin only | ✅ |
| `/admin/billing` | `AdminBilling` | Admin only | ✅ |
| `/admin/wallets` | `AdminWallets` | Admin only | ✅ |
| `/admin/reports` | `AdminReports` | Admin only | ✅ |
| `/admin/profile-requests` | `AdminProfileRequests` | Admin only | ✅ |

> **Note:** `Header` and `DbViewer` are rendered globally **outside** the `<Routes>` block — they appear on every page.

### Authentication Flow

```
User visits any protected route
        ↓
App.jsx checks isAuthenticated() → reads sessionStorage key 'sht_user_session'
        ↓
Not authenticated → redirect to /login
        ↓
Login.jsx → POST /auth/login → success response stored in sessionStorage
        ↓
Redirect based on role:
  role = "user"  → /user-dashboard
  role = "admin" → /admin
```

---

## 📄 Pages Reference

### 🌐 Public Pages

---

#### `Home.jsx` — `/`
**Access:** Everyone | **Lazy:** No

A static landing page introducing the Smart Highway Tolling System. Displays road safety announcements, a features grid, and quick links to login or register.

- **API Calls:** None
- **State:** None
- **Child Components:** None

---

#### `Login.jsx` — `/login`
**Access:** Guests only | **Lazy:** No

A tabbed login form with two modes — User (email + phone number) and Admin (email + password).

- **API Calls:** `POST /auth/login` (via `auth.js`)
- **State managed:**

| Variable | Type | Purpose |
|----------|------|---------|
| `activeTab` | String | `'user'` or `'admin'` — controls which form shows |
| `loading` | Boolean | Disables button during submission |
| `error` | String | Displays login failure reason |
| `email` | String | Email input |
| `passwordOrPhone` | String | Phone (user) or password (admin) input |

---

#### `Register.jsx` — `/register`
**Access:** Guests only | **Lazy:** No

Account creation form. Collects name, email, and phone number. Redirects to `/login` on success.

- **API Calls:** `POST /users`
- **State:** `formData {name, email, phoneNumber}`, `formLoading`, `successMessage`, `formError`

---

### 👤 User Pages

---

#### `Users.jsx` — `/users`
**Access:** User, Admin | **Lazy:** No

The "My Profile" page. Shows the logged-in user's profile details and a history table of all their past profile update requests (PENDING, APPROVED, REJECTED).

- **API Calls:**
  - `GET /users/{userId}` — fetch profile
  - `GET /profile-requests/user/{userId}` — fetch update request history
- **State:** `user`, `requests`, `loading`, `reqLoading`, `error`, `successMessage`, `showEditModal`
- **Child Components:** `<ProfileEditModal />` — opens when user clicks "Edit Profile"
- **What the modal does:** Submits a `POST /profile-requests` request for the change — does NOT directly edit the user record

---

#### `UserDashboard.jsx` — `/user-dashboard`
**Access:** User only | **Lazy:** Yes

The main user home screen. Shows 4 summary metric cards and a table of registered vehicles with live IoT simulation status indicators.

- **API Calls:**
  - `GET /users/{userId}` — user data
  - `GET /users/{userId}/vehicles` — vehicle list
  - `GET /highway-usage/summary/{vehicleId}` — per-vehicle usage stats
  - `GET /wallets/user/{userId}` — wallet balance
  - `GET /bills/user/{userId}` — billing history
  - `GET http://localhost:8082/api/simulation/status` — polls IoT simulator **every 5 seconds**
- **State:** `userData` (aggregated metrics), `myVehicles`, `activeSimIds`, `apiStatus`
- **Child Components:**
  - `<DashboardSummaryCards />` — renders the 4 metric cards (balance, active vehicles, toll this month, last highway)
  - `<DashboardMyVehicles />` — vehicle list with active simulation highlighting

---

#### `Vehicles.jsx` — `/vehicles`
**Access:** User, Admin | **Lazy:** Yes

Dedicated vehicle listing page with search and type filtering. Includes a pulsing green **IoT Connection Indicator** — shows live status if the simulator is broadcasting on port 8082.

- **API Calls:**
  - `GET /users/{userId}/vehicles` (if user) OR `GET /vehicles` (if admin)
  - `GET http://localhost:8082/api/simulation/status` — polls IoT **every 5 seconds**
- **State:** `vehicles`, `searchTerm`, `typeFilter`, `iotConnected`, `simStatus`
- **Child Components:**
  - `<VehicleFilters />` — search bar and vehicle type dropdown filter
  - `<VehicleTable />` — vehicle list with "Track Live" button per row (opens `VehicleTrackingModal`)

---

#### `WalletBills.jsx` — `/wallet-bills`
**Access:** User only | **Lazy:** Yes

The complete financial management page. Covers wallet balance, top-up requests, bill generation, and bill payment.

- **API Calls:**
  - `GET /wallets/user/{userId}` — wallet balance
  - `GET /bills/user/{userId}` — bill history
  - `POST /wallets/user/{userId}/recharge-request` — submit top-up request
  - `POST /bills/generate` — generate a bill for a period
  - `POST /bills/{billId}/pay` — pay a bill from wallet balance
- **State:** `wallet`, `bills`, `vehicles`, recharge states (`addAmount`, `addLoading`, `addMessage`), bill generation states (`genMode`, `genVehicle`, `genCustomFrom`, `genCustomTo`)
- **Child Components:**
  - `<WalletCard />` — balance display + top-up request form
  - `<BillGenerator />` — period selector (Daily, Weekly, Monthly, Custom)
  - `<BillsTable />` — paginated bills list with CSV export and Pay Now button
- **Payment flow:** Uses `window.confirm` before deducting. "Pay Now" calls `POST /bills/{id}/pay` which deducts the amount from the wallet.
- **Export:** Bills can be exported as a CSV file directly from the browser. PDF export is planned but not yet implemented.

---

#### `TollHistory.jsx` — `/toll-history`
**Access:** User only | **Lazy:** Yes

An analytics view showing total money spent, total bills generated, distance traveled per vehicle, and a breakdown of top highways used per vehicle.

- **API Calls:**
  - `GET /bills/user/{userId}` — all bills
  - `GET /users/{userId}/vehicles` — vehicle list
  - `GET /highway-usage/summary/{vehicleId}` — per-vehicle highway breakdown
- **State:** `bills`, `vehicles`, `usageSummaries` (map of vehicleId → usage stats)
- **Child Components:** None

---

### 🔧 Admin Pages

---

#### `AdminDashboard.jsx` — `/admin`
**Access:** Admin only | **Lazy:** Yes

The admin home screen. Focused on **live system monitoring** rather than charts. Features a large pulsing IoT Connection Indicator, total system revenue metrics, and a Deficit Alert table showing users whose wallet balance has dropped below zero.

- **API Calls:**
  - `GET /admin/stats` — system-wide counts
  - `GET /admin/wallets/negative` — wallets in deficit
  - `GET /admin/vehicles` — full vehicle list
  - `GET http://localhost:8082/api/simulation/status` — IoT connection check
- **State:** `stats`, `negativeWallets`, `vehicles`, `iotConnected`, `activeIotCount`

---

#### `AdminUsers.jsx` — `/admin/users`
**Access:** Admin only | **Lazy:** Yes

Full CRUD management for user accounts.

- **API Calls:** `GET /users`, `POST /users`, `PUT /users/{id}`, `DELETE /users/{id}`
- **State:** `users`, `showModal`, `modalMode`, `formData`, `viewingUser`
- **Child Components:**
  - `<AdminUsersTable />` — paginated user list with Edit, View, Delete buttons
  - `<AdminUserFormModal />` — form for adding or editing a user
  - `<AdminUserProfileModal />` — displays detailed stats (vehicles owned, total spent) for a user

---

#### `AdminVehicles.jsx` — `/admin/vehicles`
**Access:** Admin only | **Lazy:** Yes

Vehicle management combined with the lifecycle approval queue.

- **State:** `refreshTrigger`, `showModal`, `modalMode`, `formData`, `trackingVehicle`
- **Child Components:**
  - `<AdminVehicleRequests />` — pending ADD/SELL/SCRAP/DEACTIVATE/MODIFY approval queue
  - `<AdminVehiclesTable />` — full vehicle registry with "Track Live" button
  - `<AdminVehicleModal />` — direct add/edit form (bypasses approval queue — admin privilege)
  - `<AdminVehicleTrackingModal />` — same live tracking modal as user version

---

#### `AdminHighways.jsx` — `/admin/highways`
**Access:** Admin only | **Lazy:** Yes

Full CRUD for highway toll zone definitions. Admins define start/end GPS coordinates and set per-vehicle-type toll rates.

- **API Calls:** `GET /highways`, `POST /highways`, `PUT /highways/{id}`, `DELETE /highways/{id}`
- **State:** `highways` (paginated via `usePagination`), `showModal`, `modalMode`, `formData`
- **Rate fields:** CAR (₹/km), BIKE (₹/km), TRUCK (₹/km) — buses share the truck rate
- **Child Components:** `<Paginator />`

---

#### `AdminBilling.jsx` — `/admin/billing`
**Access:** Admin only | **Lazy:** Yes

A powerful billing control panel. Admins can force-generate bills in bulk or for specific targets. Features autocomplete search boxes for user and vehicle selection.

- **API Calls:**
  - `GET /users`, `GET /admin/vehicles`, `GET /admin/bills/recent`
  - `POST /admin/generate-bills` — bulk all users
  - `POST /admin/generate-all-vehicle-bills` — bulk all vehicles
  - `POST /admin/generate-bill/user/{id}` — single user
  - `POST /admin/generate-bill/vehicle/{id}` — single vehicle
- **State:** `users`, `vehicles`, `recentBills`, `selectedUser`, `selectedVehicle`, `userSearch`, `vehicleSearch`

---

#### `AdminWallets.jsx` — `/admin/wallets`
**Access:** Admin only | **Lazy:** Yes

Manages the wallet recharge approval queue and manual top-ups.

- **API Calls:**
  - `GET /admin/wallets/recharge-requests` — pending UPI requests
  - `POST /admin/wallets/recharge-requests/{id}/{approve|decline}` — process request
  - `POST /admin/wallets/user/{userId}/topup` — manual direct top-up
- **State:** `requests`, `topupUserId`, `topupAmount`
- **Recharge flow:** Users submit a recharge request with a simulated UPI reference. The admin sees it in this page and approves or declines. On approval, the wallet balance is credited.

---

#### `AdminReports.jsx` — `/admin/reports`
**Access:** Admin only | **Lazy:** Yes

Deep-dive forensic reporting on a single vehicle. Shows total distance, all highways used with a percentage breakdown, and every raw GPS ping ever recorded for that vehicle.

- **API Calls:**
  - `GET /highway-usage/summary/{vehicleId}` — usage breakdown by highway
  - `GET /locations/vehicle/{vehicleId}` — raw GPS ping history
- **State:** `searchId`, `reportData`, `locationLogs`

---

#### `AdminProfileRequests.jsx` — `/admin/profile-requests`
**Access:** Admin only | **Lazy:** Yes

The approval queue for user profile change requests (name, email, phone). Admin can filter by status and review requests inline.

- **API Calls:**
  - `GET /profile-requests` — all requests
  - `PUT /profile-requests/{id}/{approve|reject}` — approve or reject with notes
- **State:** `requests` (paginated via `usePagination`), `statusFilter`, `reviewingId`, `adminNotes`
- **Child Components:** `<Paginator />`
- **Review UI:** Clicking "Review" expands an inline panel inside the table row — no separate modal

---

#### `DbViewer.jsx` — Global Floating Overlay
**Access:** Developers/Testers | **Not a route**

A floating, draggable, collapsible panel accessible via a 🗄️ button in the footer. Queries the backend to list and display raw database table contents. Also has a "⚡ Seed" button to instantly populate sample highway usage data.

- **API Calls:**
  - `GET /db-explorer/tables` — schema map of available tables
  - `GET /db-explorer/...` — dynamic data per selected tab
  - `POST /admin/populate-usage` — seed test data
  - `POST /admin/generate-bills` — trigger billing
- **State:** `dbTables`, `activeTab`, `tableData` (cache), `minimized`, `fullscreen`

> ⚠️ This tool is intended for development only. Remove or secure before any production deployment.

---

## 🧩 Components Reference

### Global Components

#### `Header.jsx`
Top navigation bar rendered on every page. Shows the project title, an "ADMIN" watermark if the current session role is admin, and the logged-in user's name.
- **Props:** None — reads session directly from `getSession()`
- **API Calls:** None

---

#### `NotificationBell.jsx`
A clickable bell icon in the header with a red unread-count badge. Opens a floating dropdown showing the notification list.

**Polling strategy (bandwidth-optimized):**
- Polls **only the count** (`/notifications/user/{id}/unread-count`) every **30 seconds** — lightweight
- Fetches the **full notification list** only when the user **actually clicks** the bell to open it

**API Calls:**
- `GET /notifications/user/{userId}/unread-count` — polled every 30s
- `GET /notifications/user/{userId}` — on bell click
- `PUT /notifications/{id}/read` — mark single notification read
- `PUT /notifications/user/{userId}/read-all` — mark all read on open

**State:** `notifications`, `unreadCount`, `open`, `dropdownPos`
**Event listener:** `mousedown` on document — closes dropdown when clicking outside

---

#### `Paginator.jsx`
Reusable pagination control bar. Renders `‹ 1 2 ... 5 ›` with ellipses for large page counts.

**Props:**

| Prop | Type | Purpose |
|------|------|---------|
| `page` | Number | Currently active page |
| `totalPages` | Number | Total page count |
| `rangeLabel` | String | e.g. `"Showing 11–20 of 45"` |
| `onPageChange` | Function | Callback when user changes page |

**How it connects to `usePagination`:** The hook handles the data slicing; this component handles only the UI rendering. The parent page passes the hook's output directly as props to this component.

---

#### `LoadingFallback.jsx`
A centered spinner component used as the `<Suspense>` fallback while lazy-loaded pages are being fetched.

---

### Vehicle Tracking Modal

#### `VehicleTrackingModal.jsx`
The most complex component in the application. A full-screen overlay providing near-real-time vehicle tracking.

**UI features:** Custom SVG speed gauge, highway status indicator (🛣️ On Highway / 🏙️ Off-Highway), live GPS coordinates, session usage summary, embedded Google Maps, and a scrollable table of recent GPS pings.

**How the map works:** Generates an embedded Google Maps `<iframe>` URL dynamically:
```
https://maps.google.com/maps?q={lat},{lng}&z=14&output=embed
```
The lat/lng updates every time a new GPS ping arrives — no external map library needed.

**Dual polling strategy (eliminates database write latency):**

| Poll Target | Endpoint | Interval | Purpose |
|-------------|----------|---------|---------|
| Main backend | `GET /locations/vehicle/{id}/latest` | 3 seconds | Ground truth from database |
| IoT Simulator | `GET http://localhost:8082/api/iot/live-locations` | 3 seconds | Fresher live data — overlays backend data if more recent |

If the simulator has a fresher timestamp than the database record, the live simulator data is displayed immediately without waiting for the DB write.

**Props:**

| Prop | Type | Purpose |
|------|------|---------|
| `vehicle` | Object | The vehicle being tracked |
| `onClose` | Function | Callback to close the modal |

**State:** `location`, `history`, `usageSummary`, `countdown` (3s visual timer), `simulating`, `liveIotData`

**API Calls:**
- `GET /locations/vehicle/{id}/latest`
- `GET /locations/vehicle/{id}/history`
- `GET /highway-usage/summary/{id}`
- `GET http://localhost:8082/api/simulation/status`
- `GET http://localhost:8082/api/iot/live-locations`

---

### Dashboard Components

| Component | Purpose | Props |
|-----------|---------|-------|
| `DashboardSummaryCards.jsx` | Renders 4 metric cards: wallet balance, active vehicles, toll this month, last highway used | `userData` (Object), `apiStatus` (Object) |
| `DashboardMyVehicles.jsx` | Vehicle list on user dashboard highlighting which are actively simulating | `vehicles` (Array), `activeSimIds` (Array) |

---

### Wallet Components

| Component | Purpose |
|-----------|---------|
| `WalletCard.jsx` | Displays current balance, deficit warning if below minimum, recharge request form |
| `BillGenerator.jsx` | Time period selector (Daily, Weekly, Monthly, Custom date range) for generating bills |
| `BillsTable.jsx` | Paginated bill history with status badges (PAID / PENDING / OVERDUE), CSV export, and Pay Now button |

---

### Admin Components

| Component | Purpose |
|-----------|---------|
| `AdminUsersTable.jsx` | Paginated all-users table with Edit, View Profile, Delete buttons per row |
| `AdminUserFormModal.jsx` | Form modal for creating or editing a user account |
| `AdminUserProfileModal.jsx` | Detailed user stats view (vehicles owned, total toll paid) |
| `AdminVehiclesTable.jsx` | Paginated all-vehicles table with Track Live, Edit, Delete buttons |
| `AdminVehicleModal.jsx` | Form modal to directly create or edit a vehicle (admin bypass — no approval queue) |
| `AdminVehicleRequests.jsx` | Pending lifecycle request queue — Approve ✅ or Reject ❌ with notes |
| `AdminVehicleTrackingModal.jsx` | Admin-accessible version of the VehicleTrackingModal |

---

### User Components

| Component | Purpose |
|-----------|---------|
| `ProfileEditModal.jsx` | Form for users to request name/email/phone changes — submits `POST /profile-requests` — does NOT directly modify the user record |
| `VehicleTable.jsx` | User's own vehicle list table with "Track Live" button |
| `VehicleFilters.jsx` | Search bar and vehicle type dropdown filter |

---

## ⚙️ Services & Hooks

### `services/api.js` — Axios HTTP Client

The single source of truth for all backend communication. Every page and component imports from here — no direct `axios` calls outside this file.

**Configuration:**
```javascript
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: { 'Content-Type': 'application/json' },
    timeout: 10000,   // 10 seconds — prevents infinite hangs if backend is down
});
```

**Exported functions:**

| Function | HTTP Method | Usage |
|----------|-------------|-------|
| `getRequest(endpoint)` | GET | Fetch data — returns `response.data` |
| `postRequest(endpoint, data)` | POST | Create records, trigger actions |
| `putRequest(endpoint, data)` | PUT | Full update of a record |
| `patchRequest(endpoint)` | PATCH | Toggle or partial update |
| `deleteRequest(endpoint)` | DELETE | Remove a record |
| `export default api` | — | Raw Axios instance for custom config |

> **Note:** The Vite proxy (`vite.config.js`) forwards all `/api/*` requests from port 3000 to port 8080 during development, so browser CORS is never triggered.

---

### `services/auth.js` — Session Management

Handles real HTTP authentication (not mock data) and browser session persistence.

**Session storage:** Uses `sessionStorage` with key `'sht_user_session'`. This keeps the user logged in on refresh but logs them out when the browser tab is closed — appropriate for a financial application.

**Exported functions:**

| Function | What It Does |
|----------|-------------|
| `login(email, phoneNumber, role)` | Calls `POST /auth/login`, saves response to `sessionStorage` |
| `getSession()` | Returns parsed session object or `null` |
| `clearSession()` | Removes session key — used by logout |
| `isAuthenticated()` | Returns `true`/`false` based on session existence |

**Session object shape (stored):**
```json
{
  "role": "user",
  "userId": 1,
  "name": "Albert J",
  "email": "albert@example.com",
  "phoneNumber": "9876543210"
}
```
For admin sessions, `userId` is `0`.

---

### `hooks/usePagination.js` — Pagination Logic

A custom React hook that handles all the math for chopping a large data array into smaller pages.

**Parameters:**

| Parameter | Type | Default | Purpose |
|-----------|------|---------|---------|
| `items` | Array | `[]` | The full data array to paginate |
| `perPage` | Number | `10` | Items to show per page |

**Returns:**

| Property | Type | Description |
|----------|------|-------------|
| `page` | Number | Currently active page (1-indexed, safe) |
| `setPage` | Function | Change the active page |
| `totalPages` | Number | Total number of pages |
| `paged` | Array | Sliced array for the current page only |
| `rangeLabel` | String | e.g. `"Showing 11–20 of 50"` or `"No records"` |

**Used by:** `AdminHighways.jsx`, `AdminProfileRequests.jsx`, `BillsTable.jsx`, and any page using `<Paginator />`

---

## 🎨 CSS Architecture

The frontend uses a **9-module CSS system** organized for maintainability. `index.css` acts as the master importer — all 9 files load from there.

| File | What It Styles |
|------|---------------|
| `global.css` | CSS resets (`margin: 0`), font definitions, `@keyframes` animations |
| `layout.css` | Main container widths, cards, grid structure |
| `forms.css` | Input fields, labels, focus outlines |
| `buttons.css` | `.btn`, `.btn-primary`, hover and active states |
| `tables.css` | `.custom-data-table`, borders, alternating row colors |
| `badges.css` | Status pills — Active, Inactive, Pending, Approved, Rejected, Paid, Overdue |
| `messages.css` | Success/error alert boxes, empty state text |
| `vehicles.css` | Vehicle card grid and type icon layouts |
| `wallet.css` | Wallet card, balance display, recharge form |

**Design tokens used throughout:**
```css
font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
background: #f5f5f5;   /* App background */
color: #333;           /* Base text */
```

> **Architecture note:** The project uses direct hex codes and class names rather than CSS custom properties (`--color-primary`). This keeps the styling simple and avoids the need for a CSS preprocessor.

---

## ✨ Key Features

### 1. Role-Based Authentication & Route Guards
Authenticated routes check the session role before rendering. Admin routes redirect regular users; unauthenticated users are sent to `/login`. The `LoginWrapper` prevents already-logged-in users from accessing `/login`.

### 2. Live IoT Connection Indicator
Both `UserDashboard.jsx` and `Vehicles.jsx` poll the IoT Simulator (`http://localhost:8082/api/simulation/status`) every **5 seconds**. A pulsing green dot appears when the simulator is active — giving users and admins instant visual confirmation of system health.

### 3. Real-Time Notification Bell
`NotificationBell.jsx` polls the backend every 30 seconds for unread alert counts. Full notifications are only fetched when the user clicks the bell — minimizing unnecessary network requests. Notifications include admin approval/rejection reasons verbatim.

### 4. Live Vehicle Tracking (Google Maps)
`VehicleTrackingModal.jsx` uses a dual-polling strategy: it simultaneously polls the main database and the IoT simulator. If the simulator has fresher coordinates than the database (due to DB write latency), the live simulator data is shown immediately. The map updates via an embedded Google Maps `<iframe>`.

### 5. Data Governance — Request-Based UI
Standard users cannot directly edit vehicles or profile data. The UI enforces this by replacing "Edit" buttons with "Request Change" forms. This mirrors the backend's approval workflow architecture.

### 6. Admin Billing Control Panel
`AdminBilling.jsx` provides a comprehensive billing control panel with autocomplete search. Admins can trigger bill generation for all users, a specific user, a specific vehicle, or all vehicles — all with a single click.

### 7. Floating Dev Tool (DbViewer)
A globally accessible `DbViewer` panel (🗄️ button) lets developers inspect raw database table contents without leaving the browser. Also provides one-click data seeding for testing.

### 8. CSV Export for Bills
`BillsTable.jsx` includes an "Export CSV" button that generates a downloadable CSV of the user's billing history directly in the browser — no backend call needed.

### 9. Client-Side Pagination
The `usePagination` hook + `Paginator` component pair provides consistent, accessible pagination across all list views. Works on client-side data (no backend pagination calls required for most views).

---

## 🔄 Changes from Previous Version

This is a summary of what changed since the old `README.md` was written.

| Old README Said | Current Reality |
|----------------|----------------|
| Uses `react-leaflet` for maps | ❌ **Removed** — replaced by Google Maps `<iframe>` in `VehicleTrackingModal.jsx`. No external map library. |
| Uses `Recharts` for admin analytics | ❌ **Removed** — admin dashboard uses CSS metric cards and data tables instead. |
| `Locations.jsx` is a dedicated map page | ❌ **Removed** — replaced by the `VehicleTrackingModal` overlay accessible from any vehicle list. |
| `auth.js` uses mock session management | ✅ **Replaced** — now makes real `POST /auth/login` calls to the Spring Boot backend. |
| `NotificationBell.jsx` is static | ✅ **Upgraded** — now polls backend every 30 seconds for real-time unread counts. |

### New Pages Added (not in old README)

| Page | Route | What It Does |
|------|-------|-------------|
| `Login.jsx` | `/login` | Real authenticated login with user/admin tabs |
| `Register.jsx` | `/register` | New user account creation |
| `UserDashboard.jsx` | `/user-dashboard` | User home hub with IoT status + summary cards |
| `TollHistory.jsx` | `/toll-history` | Usage analytics and highway breakdown |
| `AdminHighways.jsx` | `/admin/highways` | GPS highway zone management |
| `AdminBilling.jsx` | `/admin/billing` | Manual billing trigger panel |
| `AdminWallets.jsx` | `/admin/wallets` | Recharge approval + manual top-up |
| `AdminReports.jsx` | `/admin/reports` | Deep-dive GPS forensics per vehicle |
| `AdminProfileRequests.jsx` | `/admin/profile-requests` | Profile change approval queue |
| `DbViewer.jsx` | Global overlay | Raw database browser dev tool |

---

## 🚧 Work in Progress

| Feature | Current State | What's Missing |
|---------|--------------|----------------|
| Payment Gateway UI | ✅ Logic complete — users request recharges, admins approve | No visual UPI QR code or credit card interface — user types an amount and simulates the request |
| PDF Bill Export | ✅ CSV export works | "Download PDF Statement" button not yet implemented — planned for final polish |

---

## 🔗 Backend Connection

The frontend depends entirely on the Spring Boot backend being available at `http://localhost:8080`.

| Service | URL | Required? |
|---------|-----|---------|
| Main Backend | `http://localhost:8080` | ✅ Required — all data comes from here |
| IoT Simulator | `http://localhost:8082` | ⚡ Optional — enables live tracking features |

If the backend is not running:
- All API calls will fail with a network error (Axios 10s timeout)
- The app will still load and render the UI, but no data will appear

**Start everything at once from the project root:**
```bash
start-project.bat
```

---

## 🛠️ Configuration

### `vite.config.js`
```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
```

The proxy forwards all `/api/*` requests from port 3000 to port 8080 during development, eliminating CORS errors.

---

*Frontend README — Smart Highway Tolling System*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
*SRM Institute of Science and Technology — Trichy, MCA Final Year Project 2024–2026*