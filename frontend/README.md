# 🚦 Smart Highway Tolling System (Frontend)

This is the React client application for the Smart Highway Tolling System, built with Vite for lightning-fast HMR and an optimized build process.

---

## 🚀 Quick Start

### 1. Prerequisites
Ensure you have Node.js 18+ installed.

### 2. Install Dependencies
```bash
cd frontend
npm install
```

### 3. Start Development Server
```bash
npm run dev
```
The application will be accessible at: **http://localhost:3000**

---

## 🎨 Technology Stack & Libraries

- **Framework:** React 18
- **Build Tool:** Vite 5
- **Routing:** React Router DOM v6
- **State Management:** React Hooks (`useState`, `useEffect`, Custom Hooks like `usePagination`)
- **API Communication:** Axios interceptors mapped to `http://localhost:8080/api`
- **Charting & Analytics:** Recharts (Responsive lines, bars, and composed charts)
- **Live Maps:** React-Leaflet (Leaflet.js)
- **Styling:** Vanilla CSS3 with CSS Variables for consistent theming

---

## 📁 Source Code Structure

```
frontend/
├── src/
│   ├── components/            # Reusable UI Components
│   │   ├── admin/             # Admin-specific components (VehicleRequests, UserRequests)
│   │   ├── users/             # User Management and Profile Update Modals
│   │   ├── vehicles/          # Vehicle Management and Modification Modals
│   │   ├── Paginator.jsx      # Global pagination handler
│   │   └── NotificationBell.jsx # Live polling notification drop-down
│   │
│   ├── hooks/                 # Custom React Hooks
│   │   └── usePagination.js   # Client-side pagination logic
│   │
│   ├── pages/                 # Full Page Views
│   │   ├── Home.jsx           # Landing / Dashboard based on role
│   │   ├── AdminUsers.jsx     # Admin: Manage Users & Profile Requests
│   │   ├── AdminVehicles.jsx  # Admin: Manage Vehicles & Requests
│   │   ├── Vehicles.jsx       # User: View registered vehicles
│   │   ├── WalletBills.jsx    # User: Wallet & Invoices
│   │   └── Locations.jsx      # Admin/User: Live GPS Tracker Map
│   │
│   ├── services/              # API & Context Logic
│   │   ├── api.js             # Pre-configured Axios instance
│   │   └── auth.js            # Mock session management
│   │
│   ├── App.jsx                # Main Router & Authentication Gateway
│   ├── main.jsx               # React DOM Entry
│   └── index.css              # Global Design System (Tokens, Utilities)
```

---

## ✨ Key Frontend Features

1. **Role-based Authentication Routing:** 
   Guarded components ensure Admins see analytics and request approvals, while normal Users see their wallets and request forms.
   
2. **Interactive Request Modals:** 
   Direct actions are disabled for standard users. Instead, they use smart modals to request additions, deactivations, transfers, and detail modifications.

3. **Live Notification Bell:** 
   A top-nav bell icon continuously polls the backend for approved/rejected requests, dropping down a history of updates for the user.

4. **Live Vehicle Tracker:** 
   Integrates `react-leaflet` to display dynamic markers tracing JSON routes simulated by the IoT module. Distinguishes local roads from National Highways.

5. **Advanced Dashboard Analytics:** 
   Uses `Recharts` inside `Admin.jsx` to process raw location logs and display dynamic visual representations of system health, active anomalies, and recent toll fees.

---

## 🔗 Backend Environment

The frontend relies heavily on the Java Spring Boot API backend. To ensure seamless operation, the backend must be running concurrently at `http://localhost:8080`. Both applications can be launched simultaneously from the project root using the `start-project.bat` script.
