# 🚦 Smart Highway Usage-Based Tolling System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2-blue.svg)](https://reactjs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Node.js](https://img.shields.io/badge/Node.js-18+-green.svg)](https://nodejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![Status](https://img.shields.io/badge/Status-Under%20Development-yellow.svg)]()
[![License](https://img.shields.io/badge/License-Academic-blue.svg)](LICENSE)

---

## 📌 Project Overview

The **Smart Highway Usage-Based Tolling System** is an innovative full-stack web application that completely reimagines highway toll collection. Instead of charging a flat fee at a toll booth, this system tracks the exact distance each vehicle travels on a highway using GPS coordinates and charges only for what was actually used — fairly, automatically, and transparently.

Think of it like an electricity meter for the highway. You pay for exactly what you consume, not a flat rate regardless of how short your trip was. The entire system — from vehicle registration to monthly billing — is managed digitally, without physical toll booths, without cash transactions, and without queues.

**Project Status:** 🚧 **Under Active Development (~85% Complete)**

**Developed By:** Albert J  
**Institution:** SRM Institute of Science and Technology - Trichy  
**Program:** Master of Computer Applications (MCA)  
**Academic Year:** 2024-2026  
**Project Type:** Final Year Project  
**Email:** [albertcyse@gmail.com](mailto:albertcyse@gmail.com)

> **🚀 Quick Start:** Run `start-project.bat` for one-click automated setup and launch!  
> **🔒 Secure Setup:** All credentials are stored in a `.env` file — no passwords are ever hardcoded in the source code.

---

## 🎯 Problem Statement

### Issues with Traditional Toll Systems

Traditional highway toll collection systems have remained largely unchanged for decades and suffer from serious inefficiencies:

| Problem | Description |
|--------|-------------|
| ❌ **Fixed Flat Charges** | Everyone pays the same rate, regardless of whether they drove 2 km or 200 km |
| ❌ **Long Queues** | Physical toll booths create traffic bottlenecks and congestion |
| ❌ **Cash-Only Transactions** | Leads to corruption, delays, and a lack of audit trail |
| ❌ **No Billing Transparency** | Users receive no clear breakdown of what they are paying for |
| ❌ **Unfair Pricing** | Short-distance travelers subsidize long-distance travelers |
| ❌ **Environmental Impact** | Vehicles idling at toll booths produce unnecessary pollution |
| ❌ **No Fraud Detection** | No system to detect abnormal vehicle behavior or missing data |

### Our Smart Solution

This project solves every one of the above problems:

| Solution | What It Does |
|---------|--------------|
| ✅ **GPS-Based Distance Tracking** | Charges users only for the exact distance they travel on the highway |
| ✅ **Automated Cashless Billing** | No toll booths, no cash — deductions happen from a digital wallet |
| ✅ **Digital Wallet Integration** | Each user has a wallet; toll is deducted automatically in real-time |
| ✅ **Monthly Consolidated Billing with Audit Trails** | Full monthly records stored for transparency and reporting |
| ✅ **Real-Time IoT Simulation** | GPS data is continuously fed from vehicles into the system |
| ✅ **Admin Dashboard** | Administrators can monitor, approve, and manage everything |
| ✅ **Automated Fraud Detection** | Anomaly system detects suspicious patterns and alerts the system |
| ✅ **Fair Distance-Based Pricing** | Vehicle type × distance × rate per km = exactly what you owe |
| ✅ **Secure Admin Verification** | All critical vehicle and profile changes require explicit Admin approval, preventing unauthorized data tampering |
| ✅ **Smart Request & Approval Workflow** | Structured admin approval queue for vehicle lifecycle and profile changes |
| ✅ **Live Notifications** | Real-time alerts delivered to users via a top-nav notification bell |
| ✅ **Optimized Data Retrieval** | Advanced paging and filtering to handle thousands of vehicle records smoothly |
| ✅ **Clean & Modular Architecture** | Clear separation between the IoT processing engine and the management dashboards |
| ✅ **Dynamic Route Visualization** | Custom-built responsive GPS grid mapping of vehicle routes |

---

## 🧠 How The System Works

This is the complete end-to-end flow of what happens from the moment a vehicle is registered to when a monthly bill is recorded:

```
┌─────────────────────────────────────────────────────────────┐
│  STEP 1: Smart Registration Request                         │
│  User requests registration → Admin Approval → Wallet Seeding│
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 2: GPS Data Collection (IoT Simulation)               │
│  Vehicle sends GPS → Latitude, Longitude, Timestamp         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 3: Hybrid Highway Detection                           │
│  Backend checks → Is vehicle on highway? (IoT Grid Detection)│
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 4: Precision Distance Calculation                     │
│  Haversine Formula → Geodetic distance between GPS points   │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 5: Dynamic Toll Calculation                           │
│  Vehicle Type × Distance × Rate/km → Total Toll Amount      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 6: Automated Wallet Deduction                         │
│  Real-time deduction from digital wallet → Update balance   │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 7: Consolidated Billing                               │
│  Scheduler generates audit trail → Dashboard Updated        │
└─────────────────────────────────────────────────────────────┘
```

### Step-by-Step Plain English Explanation

**Step 1 — Smart Registration Request:**  
A user fills out a registration form. The request does not take effect immediately — it is sent to the Admin approval queue. Once the Admin reviews and approves the vehicle, the vehicle is formally added to the system and a digital wallet is automatically created and seeded for that user. This prevents unauthorized or ghost registrations.

**Step 2 — GPS Data Collection:**  
Once registered, the system receives continuous GPS updates from the vehicle (simulated via the built-in IoT engine). Each update contains the vehicle's exact latitude, longitude, and timestamp.

**Step 3 — Hybrid Highway Detection:**  
The backend uses IoT Grid Detection logic to determine whether the vehicle is currently on a registered highway. This is a coordinate-based detection algorithm that checks if the vehicle's GPS point falls within the defined boundary of any known highway.

**Step 4 — Precision Distance Calculation:**  
For every two consecutive GPS points while the vehicle is on a highway, the system uses the **Haversine Formula** — a mathematical model for calculating the shortest distance between two points on a sphere (the Earth). This gives geodetically accurate distances rather than rough straight-line estimates.

> 💡 *Haversine Note for Viva:* "My system uses geodetic distance between GPS coordinates for precision tolling, which is significantly more accurate than simple Cartesian or Euclidean distance since it accounts for the curvature of the Earth."

**Step 5 — Dynamic Toll Calculation:**  
Toll = Distance × Rate per km for the vehicle's type. Rates differ based on whether the vehicle is a CAR, BIKE, BUS, or TRUCK. This ensures fair and proportionate charging.

**Step 6 — Automated Wallet Deduction:**  
The calculated toll is instantly deducted from the user's digital wallet. The wallet balance is updated in real-time and the transaction is logged.

**Step 7 — Consolidated Billing:**  
At the end of each month, the built-in scheduler compiles all highway usage records for each user into a consolidated monthly bill. The bill and audit trail are stored and made visible in the user's billing dashboard for complete transparency.

---

## 🛠️ Technology Stack

### Backend Technologies

| Component | Technology | Version | Purpose | Notes |
|-----------|-----------|---------|---------|-------|
| **Language** | Java | 17 | Core application language | LTS version — stable and widely supported |
| **Framework** | Spring Boot | 3.2.1 | Application framework | Auto-configures the server, REST layer, and database |
| **ORM** | Spring Data JPA | 3.2.1 | Database communication | Maps Java objects to MySQL tables without raw SQL |
| **Database** | MySQL | 8.0+ | Data persistence | Stores all users, vehicles, GPS data, bills, etc. |
| **Build Tool** | Maven | 3.6+ | Dependency and build management | Downloads libraries and packages the application |
| **Utilities** | Standard Java | — | Conventional Getters/Setters/Constructors | Java POJOs — no annotation-based code generation |
| **Validation** | Spring Validation | 3.2.1 | Input validation | Ensures GPS coordinates, user inputs are valid before processing |
| **Messaging** | RESTful Services | — | Admin-User interaction | All communication between the frontend and backend is via REST APIs |
| **Scheduling** | Spring Scheduler | 3.2.1 | Automated background tasks | Runs monthly billing generation automatically |

### Frontend Technologies

| Component | Technology | Version | Purpose | Notes |
|-----------|-----------|---------|---------|-------|
| **Library** | React | 18.2 | UI component engine | Builds the interactive web dashboards |
| **Build Tool** | Vite | 5.0 | Fast build and hot module reload | Makes development faster with instant code updates |
| **Routing** | React Router | 6.x | Client-side navigation | Navigates between pages without reloading the browser |
| **HTTP Client** | Axios | 1.6 | API communication | Sends and receives data from the Spring Boot backend |
| **Visualization** | Modular CSS3 | — | Responsive GPS grid mapping | Custom-built SVG/CSS mapping interface for vehicle route tracking |
| **Styling** | Modular CSS3 Architecture | — | Component-level styling | Clean and organized per-component stylesheets (global.css, layout.css, buttons.css, etc.) |

### Additional Modules

| Module | Technology | Purpose | Notes |
|--------|-----------|---------|-------|
| **IoT Engine** | Spring Boot (Integrated) | Simulates GPS device data streams | **Fully integrated into the main backend** — no separate service needed |
| **Fraud Detection** | Custom Java Algorithms | Flags anomalous vehicle behavior patterns | Detects missing GPS, sudden disconnections, suspicious repetition |
| **Notification Engine** | Spring Boot + HTTP Polling | Real-time admin-to-user alert delivery | Frontend polls every 30 seconds for new notifications |
| **Request Workflow Engine** | Spring Boot | Admin approval queue for all vehicle/profile changes | Supports five distinct vehicle lifecycle actions |
| **Highway State Processor** | Spring Boot Service | Manages vehicle on/off-highway transition state and distance logic | Key component for accurate session tracking |

---

## 📦 Complete Project Structure

Below is the exact and verified project file tree. Files marked ❌ were deleted during refactoring. Active files reflect the current production state.

smart-highway-tolling-system/
│
├── Initial/                           # Main Application Core
│   │
│   ├── iot-simulator/                 # Standalone IoT Simulation Tool (Integrated)
│   │   ├── src/main/java/             # Java Backend (Port 8082)
│   │   ├── frontend/                  # React Map Dashboard (Leaflet-powered)
│   │   ├── run-simulator.bat          # Launcher script for standalone mode
│   │   └── pom.xml                    # Standalone build config
│   │
│   ├── src/                           # Core Backend Source (Spring Boot)
│   │   ├── main/java/com/highway/tolling/
│   │   │   ├── controller/            # REST API Controllers
│   │   │   ├── model/                 # JPA Entity Models
│   │   │   ├── repository/            # Data Access Layer
│   │   │   └── service/               # Business Logic Layer
│   │
│   ├── frontend/                      # User & Admin React Dashboards
│   │   ├── src/                       # Frontend source
│   │   └── package.json               # Frontend dependencies
│   │
│   ├── .env                           # Environment configuration
│   └── start-project.bat              # One-click full system launcher
```

---

## ⚡ Quick Start (Integrated Mode)
1. **Prepare Environment**: Run `start-project.bat` from the root.
2. **Access Dashboard**: Log in and go to the "User Dashboard".
3. **Trigger IoT**: A "Simulated Route" will automatically appear for active vehicles.
4. **Live Mapping**: Real-time GPS points are processed by the backend and visualized on the dashboard map.

---

## 🛰️ Standalone Simulator Mode
If you wish to run the simulator as a separate process for testing:
1. Navigate to the `iot-simulator/` folder.
2. Run `run-simulator.bat`.
3. It will launch its own backend on **Port 8082** and its own Map Dashboard on **Port 5173**.
4. Both components will connect to the core backend at **localhost:8080**.
│   │   ├── main/
│   │   │   ├── java/com/highway/tolling/
│   │   │   │   │
│   │   │   │   ├── controller/        # REST API Controllers (HTTP Endpoints)
│   │   │   │   │   ├── UserController.java            # User CRUD operations
│   │   │   │   │   ├── VehicleController.java         # Vehicle management
│   │   │   │   │   ├── HighwayController.java         # Highway definitions and rates
│   │   │   │   │   ├── IoTController.java             # Receives GPS data from IoT devices
│   │   │   │   │   ├── HighwayUsageController.java    # Distance & usage queries
│   │   │   │   │   ├── VehicleRequestController.java  # Vehicle lifecycle request management
│   │   │   │   │   ├── UserNotificationController.java# Notification bell & alerts
│   │   │   │   │   │
│   │   │   │   │   # ❌ REMOVED: TollCalculationController.java (Toll is now automated internally)
│   │   │   │   │   # ❌ REMOVED: AnomalyReviewController.java (Anomalies push to notification feed)
│   │   │   │   │
│   │   │   │   ├── model/             # JPA Entity Models (Database Table Definitions)
│   │   │   │   │   ├── User.java                      # User account entity
│   │   │   │   │   ├── Vehicle.java                   # Vehicle entity
│   │   │   │   │   ├── VehicleType.java (enum)        # CAR, BIKE, BUS, TRUCK
│   │   │   │   │   ├── Highway.java                   # Highway boundaries and per-type rates
│   │   │   │   │   ├── LocationTracking.java          # Raw GPS records
│   │   │   │   │   ├── HighwayUsage.java              # Entry/exit sessions per vehicle
│   │   │   │   │   ├── Bill.java                      # Monthly consolidated bill
│   │   │   │   │   ├── BillStatus.java (enum)         # PENDING, PAID, OVERDUE
│   │   │   │   │   ├── DataAnomaly.java               # Fraud/anomaly log record
│   │   │   │   │   ├── AnomalyType.java (enum)        # MISSING_DATA, INACTIVITY, etc.
│   │   │   │   │   ├── AnomalySeverity.java (enum)    # LOW, MEDIUM, HIGH
│   │   │   │   │   ├── VehicleRequest.java            # Lifecycle request record
│   │   │   │   │   ├── VehicleRequestType.java (enum) # ADD, SELL, SCRAP, DEACTIVATE, MODIFY
│   │   │   │   │   ├── ProfileUpdateRequest.java      # Profile change request record
│   │   │   │   │   └── UserNotification.java          # Notification alert record
│   │   │   │   │
│   │   │   │   │   # ❌ REMOVED: ReviewStatus.java (Anomaly review workflow was removed)
│   │   │   │   │   # ✏️ RENAMED: ProfileRequest.java → ProfileUpdateRequest.java
│   │   │   │   │
│   │   │   │   ├── repository/        # Data Access Layer (Database Query Interfaces)
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── VehicleRepository.java
│   │   │   │   │   ├── HighwayRepository.java
│   │   │   │   │   ├── LocationTrackingRepository.java
│   │   │   │   │   ├── HighwayUsageRepository.java
│   │   │   │   │   ├── BillRepository.java
│   │   │   │   │   ├── DataAnomalyRepository.java
│   │   │   │   │   ├── VehicleRequestRepository.java
│   │   │   │   │   └── UserNotificationRepository.java
│   │   │   │   │
│   │   │   │   ├── service/           # Business Logic Layer (Core Processing)
│   │   │   │   │   ├── UserService.java               # User account operations
│   │   │   │   │   ├── VehicleService.java            # Vehicle management logic
│   │   │   │   │   ├── HighwayService.java            # Highway configuration logic
│   │   │   │   │   ├── IoTIdentificationService.java  # IoT device recognition & routing
│   │   │   │   │   ├── LocationTrackingService.java   # GPS data storage and retrieval
│   │   │   │   │   ├── HighwayDetectionService.java   # Grid-based on/off highway logic
│   │   │   │   │   ├── HighwayStateProcessor.java     # Vehicle entry/exit state + distance tracking
│   │   │   │   │   ├── DistanceCalculatorService.java # Haversine formula implementation
│   │   │   │   │   ├── HighwayUsageService.java       # Per-session distance and billing queries
│   │   │   │   │   ├── HighwayUsageAggregationService.java # Roll-up of usage for billing
│   │   │   │   │   ├── AnomalyDetectionService.java   # Fraud pattern detection
│   │   │   │   │   ├── BillService.java               # Monthly bill generation and management
│   │   │   │   │   ├── VehicleRequestService.java     # Lifecycle request processing
│   │   │   │   │   └── NotificationService.java       # Notification creation and delivery
│   │   │   │   │
│   │   │   │   │   # ❌ REMOVED: TollCalculationService.java (Logic absorbed into core flow)
│   │   │   │   │   # ✏️ RENAMED: BillGenerationService.java → BillService.java
│   │   │   │   │   # ✅ ADDED:   HighwayStateProcessor.java (Key component for highway state)
│   │   │   │   │
│   │   │   │   ├── dto/               # Data Transfer Objects (API Request/Response Shapes)
│   │   │   │   │   ├── IoTDataRequest.java            # Structure of incoming GPS payload
│   │   │   │   │   └── IoTDataResponse.java           # Structure of GPS processing result
│   │   │   │   │
│   │   │   │   └── TollingSystemApplication.java      # Main entry point — starts the application
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── application.properties             # Server config, DB connection settings
│   │   │       └── static/                            # Static assets (if any)
│   │   │
│   │   └── test/                                      # Unit & Integration Tests
│   │
│   ├── frontend/                      # React Frontend Application
│   │   ├── src/
│   │   │   │
│   │   │   ├── pages/                 # Full Page Views (one per route)
│   │   │   │   ├── Home.jsx                       # Landing page with project overview
│   │   │   │   ├── AdminUsers.jsx                 # Admin: user listing and management
│   │   │   │   ├── AdminVehicles.jsx              # Admin: all vehicles with filters
│   │   │   │   ├── AdminHighways.jsx              # Admin: highway management (ACTIVE)
│   │   │   │   ├── AdminProfileRequests.jsx       # Admin: profile update approval queue (ACTIVE)
│   │   │   │   ├── Vehicles.jsx                   # User: vehicle listing and requests
│   │   │   │   └── WalletBills.jsx                # User: wallet balance and billing history
│   │   │   │
│   │   │   │   # ❌ REMOVED: Highways.jsx (superseded by AdminHighways.jsx)
│   │   │   │   # ❌ REMOVED: Locations.jsx (tracking now integrated into dashboards)
│   │   │   │   # ❌ REMOVED: Admin.jsx (replaced by dedicated Admin* pages)
│   │   │   │
│   │   │   ├── components/            # Reusable UI Building Blocks
│   │   │   │   ├── admin/             # Admin-specific components
│   │   │   │   │   ├── AdminProfileRequests.jsx   # Profile request list and action buttons
│   │   │   │   │   ├── AdminUsersTable.jsx        # Sortable/filterable user data table
│   │   │   │   │   ├── AdminVehicleRequests.jsx   # Lifecycle request approval interface
│   │   │   │   │   ├── AdminVehiclesTable.jsx     # Vehicle registry with admin controls
│   │   │   │   │   └── AdminVehicleModal.jsx      # Modal dialog for vehicle detail actions
│   │   │   │   │
│   │   │   │   ├── dashboard/         # Dashboard widgets and summary cards
│   │   │   │   │
│   │   │   │   ├── vehicles/          # Vehicle-related user-facing components
│   │   │   │   │   ├── VehicleRequestModal.jsx    # Form for submitting lifecycle requests
│   │   │   │   │   └── VehicleTable.jsx           # Paginated vehicle listing for users
│   │   │   │   │
│   │   │   │   ├── wallet/            # Wallet and billing display components
│   │   │   │   │
│   │   │   │   ├── NotificationBell.jsx           # Top-nav alert bell with polling
│   │   │   │   ├── Paginator.jsx                  # Reusable pagination control
│   │   │   │   ├── LoadingFallback.jsx            # Loading spinner/placeholder
│   │   │   │   └── Header.jsx                     # Top navigation bar with notification bell
│   │   │   │
│   │   │   ├── hooks/
│   │   │   │   └── usePagination.js               # Custom hook for paging logic
│   │   │   │
│   │   │   ├── services/
│   │   │   │   └── api.js                         # Centralized Axios API configuration
│   │   │   │
│   │   │   ├── styles/                            # ⭐ Modular CSS3 Architecture
│   │   │   │   ├── global.css                     # Site-wide base styles and CSS variables
│   │   │   │   ├── layout.css                     # Grid, flex, and page structure styles
│   │   │   │   ├── buttons.css                    # Button variants and states
│   │   │   │   └── [other component-level CSS files]
│   │   │   │
│   │   │   ├── App.jsx                            # Root component and route definitions
│   │   │   ├── main.jsx                           # React DOM entry point
│   │   │   └── index.css                          # Top-level global styles
│   │   │
│   │   ├── public/
│   │   ├── index.html                             # HTML shell for the React app
│   │   ├── package.json                           # Frontend dependency manifest
│   │   ├── vite.config.js                         # Vite build configuration and proxy setup
│   │   └── .gitignore
│   │
│   ├── pom.xml                                    # Maven dependency and build configuration
│   ├── .env                                       # ⚠️ Secret credentials (gitignored)
│   ├── .env.example                               # Template for credentials — safe to share
│   ├── .gitignore
│   ├── README.md                                  # This documentation file
│   ├── start-project.bat                          # ⭐ ONE-CLICK LAUNCHER (All-in-One)
│   ├── fix-maven.bat                              # 🛠️ Automated Maven fix/install script
│   ├── install-maven-offline.bat                  # 🔌 Offline Maven installer (no internet needed)
│   └── install-nodejs.bat                         # 🚀 Automated Node.js LTS installer
│
│  # ℹ️ NOTE: The IoT Simulator is now FULLY INTEGRATED into the main backend.
│  # There is no longer a separate iot-simulator/ folder.
│  # The simulation engine starts automatically with the backend and is accessible
│  # from the built-in IoT Controller.
```

---

## 🗄️ Database Schema Design

### Entity-Relationship Diagram

The following shows how all the data tables in the MySQL database relate to one another:

```
┌──────────────┐        ┌──────────────┐        ┌──────────────┐
│    Users     │1      *│   Vehicles   │1      *│  Location    │
│──────────────│────────│──────────────│────────│  Tracking    │
│ user_id (PK) │        │vehicle_id(PK)│        │  id (PK)     │
│ name         │        │vehicle_number│        │  vehicle_id  │
│ email        │        │vehicle_type  │        │  latitude    │
│ phone_number │        │user_id (FK)  │        │  longitude   │
│ created_at   │        │registered_at │        │  timestamp   │
└──────────────┘        └──────────────┘        │  highway_id  │
       │1                      │1               │  distance    │
       │                       │*               │  is_on_hwy   │
       │                       ↓                └──────────────┘
       │               ┌──────────────┐
       │               │   Vehicle    │
       │               │  Requests    │
       │               │──────────────│
       │               │  id (PK)     │
       │               │  vehicle_id  │
       │               │  user_id     │
       │               │  request_type│  ← ADD / SELL / SCRAP / DEACTIVATE / MODIFY
       │               │  new_owner_id│
       │               │  reason      │
       │               │  status      │
       │               │  admin_notes │
       │               └──────────────┘
       │1
       ↓
┌──────────────┐        ┌──────────────┐        ┌──────────────┐
│   Wallets    │        │  Highways    │        │  Highway     │
│──────────────│        │──────────────│        │  Usage       │
│wallet_id(PK) │        │highway_id(PK)│        │──────────────│
│balance       │        │highway_name  │        │  id (PK)     │
│min_balance   │        │start_lat/lon │        │  vehicle_id  │
│user_id (FK)  │        │end_lat/lon   │        │  highway_id  │
└──────────────┘        │rate_car      │        │  distance    │
       │1               │rate_bike     │        │  entry_time  │
       │                │rate_bus      │        │  exit_time   │
       │                │rate_truck    │        │  entry_lat   │
       │*               └──────────────┘        │  exit_lat    │
       ↓                                        └──────────────┘
┌──────────────┐        ┌───────────────────────────────────────┐
│    Bills     │        │           Data Anomalies              │
│──────────────│        │  (Optimized for Real-Time Detection)  │
│bill_id  (PK) │        │───────────────────────────────────────│
│user_id  (FK) │        │  id (PK)                              │
│vehicle_id    │        │  vehicle_id                           │
│bill_month    │        │  type                                 │
│total_distance│        │  description                          │
│total_amount  │        │  severity                             │
│status        │        │  detected_at                          │
│due_date      │        └───────────────────────────────────────┘
│created_at    │
└──────────────┘
   # ❌ REMOVED from DataAnomalies: review_status, reviewed_at
   # Reason: Anomalies now automatically push to user_notifications.
   # Manual review workflow replaced by automated alert system.

┌──────────────┐        ┌──────────────┐
│   Profile    │        │    User      │
│  Requests    │        │Notifications │
│──────────────│        │──────────────│
│  id (PK)     │        │  id (PK)     │
│  user_id     │        │  user_id     │
│  new_name    │        │  message     │
│  new_email   │        │  is_read     │
│  new_phone   │        │  created_at  │
│  status      │        │  linked_req  │  ← Links notification to its request
│  admin_notes │        └──────────────┘
└──────────────┘
```

### Table Reference Guide

| # | Table Name | What It Stores | Why It Exists |
|---|-----------|----------------|---------------|
| 1 | `users` | User account information (name, email, phone) | Identifies who owns which vehicles |
| 2 | `vehicles` | Registered vehicles (type, number plate, owner) | Tracks which vehicle is on the highway |
| 3 | `wallets` | Digital wallet balance and minimum threshold | Holds the toll payment funds per user |
| 4 | `highways` | Highway GPS boundaries and per-vehicle-type rates | Defines where tolling applies and at what rates |
| 5 | `location_tracking` | Raw GPS data points with highway association | The raw input feed from IoT devices |
| 6 | `highway_usage` | Entry/exit session records for each journey | Used to calculate exact distance and toll per trip |
| 7 | `bills` | Monthly consolidated toll bills | Provides transparent billing history |
| 8 | `data_anomalies` | Fraud and anomaly detection flags | Optimized for real-time fraud detection with automated logging and instant user notification triggers |
| 9 | `vehicle_requests` | Pending/Approved/Rejected vehicle lifecycle queues | Implements ADD, SCRAP, SELL, DEACTIVATE, MODIFY with admin oversight |
| 10 | `profile_requests` | Pending user profile modification requests | Users cannot modify their own name/email/phone directly — changes require admin approval |
| 11 | `user_notifications` | Real-time alerts tied to admin approval decisions | Delivers admin decisions (with notes) to users via the notification bell |

> **Design Highlight:** The `highways` table stores separate rates for `rate_car`, `rate_bike`, `rate_bus`, and `rate_truck` directly in each highway row. This is a well-normalized design that enables **dynamic per-vehicle-type pricing** without complex join queries.

> **Security Highlight:** The `data_anomalies` table no longer has manual `review_status` or `reviewed_at` fields. This was deliberately removed to shift from a "manual review" model to a fully **automated detection → notification** pipeline.

---

## 🚀 Complete Setup Guide (From Scratch)

### ✅ Recommended: Automated Setup (Quickest Method)

This is the easiest and fastest way to get the project running, especially on a new machine:

1. **Extract All Files** to a folder of your choice (e.g., `D:\Projects\SmartTolling`)
2. **Run `install-nodejs.bat`** if you don't have Node.js installed — it silently downloads and installs the latest LTS version and configures your PATH automatically
3. **Run `fix-maven.bat`** — this auto-downloads and installs Apache Maven if it's missing, and fixes common student network errors that block Maven downloads
4. **Run `start-project.bat`** — this launches the entire system (backend + frontend) in one click!

---

### 🔨 Manual Setup (Standard Installation)

If you prefer to set up everything yourself or are on a non-Windows machine, follow these steps:

#### Prerequisites Installation

**1. Install Java 17 (JDK)**
```bash
# Download from: https://adoptium.net/
# After installation, verify with:
java -version
# Expected output: java version "17.x.x"
```

**2. Install Node.js 18+ and npm**
```bash
# Download from: https://nodejs.org/
node -v    # Should show v18.x.x or higher
npm -v     # Should show the npm version
```

**3. Install MySQL 8.0+**
```bash
# Download from: https://dev.mysql.com/downloads/mysql/
# During installation:
#   - Set a root password (write it down — you'll need it)
#   - Start the MySQL service
#   - The database 'tolling_system' will be created automatically on first run
```

**4. Maven (Handled Automatically)**

Our project includes a built-in Maven wrapper. You do not need to install Maven manually. If any issues arise, run `fix-maven.bat` for an automated resolution.

---

### ⚡ Helper Scripts Available

These scripts are included to make setup as simple as possible, even without a reliable internet connection:

| Script | Purpose | When to Use |
|--------|---------|-------------|
| **`fix-maven.bat`** 🛠️ **Highly Recommended** | Auto-downloads and installs Apache Maven. Automatically fixes common student network errors during Maven setup. | If `mvn` command is not found or Maven fails to download dependencies |
| **`install-maven-offline.bat`** 🔌 **Offline Mode** | Installs Maven without requiring an active internet connection. | If you are on a slow network or offline environment |
| **`install-nodejs.bat`** 🚀 **Node.js Installer** | Automatically downloads and installs the latest Node.js LTS silently. Sets up your system PATH so `npm` works immediately. | If `npm` or `node` commands are not found |

---

### Step-by-Step Project Setup

#### STEP 1: Download the Project
```bash
# Option A: Clone from Git
git clone https://github.com/albertcyse/smart-highway-tolling-system.git

# Option B: Download as ZIP
# Extract the ZIP to your desired location
# Navigate to the 'Initial' project root folder
```

#### STEP 2: Configure Database Credentials (First-Time Only)

The system uses a `.env` file for secure credential management. Your password is never stored in any source code file.

**Option A: Automatic Setup (Recommended)**
```bash
# Simply run the launcher — it will ask for your credentials on first run
start-project.bat

# You will be prompted:
#   Enter MySQL Username (default: root): root
#   Enter MySQL Password: your_password_here
# A .env file is created automatically and reused on all future starts.
```

**Option B: Manual `.env` Setup**
```bash
# 1. Copy the template file
copy .env.example .env

# 2. Open .env in any text editor and fill in your details:
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

> ⚠️ **Security Note:** The `.env` file is listed in `.gitignore` and will never be committed to Git. Your credentials stay on your machine only.

> ℹ️ **Auto-creation:** The `tolling_system` database is created automatically when the backend starts for the first time — no manual SQL commands required.

#### STEP 3: Run the Project (One-Click Launch!)

> **Important:** The `Initial` folder **is** your project root. If you are already inside `Initial`, do not `cd Initial` again — you are already in the right place.

**Windows — Automated (Recommended):**
```bash
# From the project root (Initial folder):
start-project.bat

# This single command:
# ✅ Verifies all prerequisites (Java, Node.js, Maven, MySQL)
# ✅ Loads credentials from .env
# ✅ Starts Spring Boot backend (opens in a BLUE terminal window)
# ✅ Starts React frontend (opens in a YELLOW terminal window)
# ✅ Opens http://localhost:3000 in your browser automatically
```

**What to expect in your terminal:**
```
[0/4] Checking environment configuration...
✓ Environment variables loaded

[1/4] Checking Java...
✓ Java installed

[2/4] Checking Node.js...
✓ Node.js installed

[3/4] Checking Maven...
✓ Maven found in PATH

✓ Frontend dependencies ready

Starting Backend...  → BLUE terminal opens (Spring Boot logs)
Starting Frontend... → YELLOW terminal opens (Vite dev server)
Opening browser...   → http://localhost:3000 opens automatically
```

> 💡 **Pro Tip for Presentation:** The color-coded terminals (BLUE for backend, YELLOW for frontend) are a built-in feature of `start-project.bat` that makes it instantly clear which process is running where. This makes debugging during a live demo much easier.

#### STEP 4: Manual Startup (Alternative)

If you prefer to control each service independently:

**Start Backend:**
```bash
cd Initial
mvn spring-boot:run
# Backend available at: http://localhost:8080
# Wait for: "Started TollingSystemApplication"
```

**Start Frontend (in a new terminal):**
```bash
cd Initial/frontend
npm run dev
# Frontend available at: http://localhost:3000
```

> ℹ️ **Note:** The IoT Simulator is now **fully integrated** into the main backend. It starts automatically when the backend runs. You no longer need a separate terminal or separate project for it. The simulation can be triggered directly from the built-in IoT Controller endpoints or the user dashboard.

#### STEP 5: First-Time Access

Once both services are running:

1. Open your browser at: **http://localhost:3000**
2. You will see the Home page with the navigation menu
3. Start by creating a **User** account
4. Submit a **Vehicle Registration Request** (it will go to Admin queue)
5. Switch to the **Admin view** and approve the request
6. Once approved, GPS simulation can begin automatically
7. Watch the wallet balance update in real-time as toll is deducted

---

## 🌐 Application URLs

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend (React)** | http://localhost:3000 | Main web interface for users and admins |
| **Backend REST API** | http://localhost:8080 | All API endpoints are served from here |
| **Database (MySQL)** | localhost:3306 | Accessed internally by the backend only |
| **IoT Engine** | Integrated into backend | GPS simulation starts automatically with the backend |

> **Note:** The standalone `/api/vehicles/health` health check endpoint has been removed to minimize the publicly exposed API surface and follow the **Principle of Least Privilege** — only exposing what is actually needed.

---

## 📡 Complete API Reference

### User Management APIs

**Create User**
```http
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "9876543210"
}
```

**Get All Users**
```http
GET http://localhost:8080/api/users
```

**Add Vehicle to User**
```http
POST http://localhost:8080/api/users/1/vehicles
Content-Type: application/json

{
  "vehicleNumber": "TN01AB1234",
  "vehicleType": "CAR"
}
```

---

### Vehicle Management APIs

**Get All Vehicles (with paging/filtering)**
```http
GET http://localhost:8080/api/vehicles
```

---

### Highway Management APIs

**Create Highway**
```http
POST http://localhost:8080/api/highways
Content-Type: application/json

{
  "highwayName": "NH-44",
  "startLatitude": 12.9716,
  "startLongitude": 77.5946,
  "endLatitude": 13.0716,
  "endLongitude": 77.6946,
  "ratePerKmForCar": 2.50,
  "ratePerKmForBike": 1.50,
  "ratePerKmForBus": 5.00,
  "ratePerKmForTruck": 5.00
}
```

---

### IoT Data Submission API

**Send GPS Location (from vehicle/simulator)**
```http
POST http://localhost:8080/api/iot/data
Content-Type: application/json

{
  "vehicleId": 1,
  "latitude": 12.9800,
  "longitude": 77.6000,
  "timestamp": "2026-02-04T14:30:00"
}
```

---

### Highway Usage APIs

**Get Total Distance for Vehicle**
```http
GET http://localhost:8080/api/highway-usage/total/1
```

**Get Distance Breakdown by Highway**
```http
GET http://localhost:8080/api/highway-usage/breakdown/1
```

**Get Complete Usage Summary**
```http
GET http://localhost:8080/api/highway-usage/summary/1
```

---

### Admin Wallet API

**Seed/Initialize All Wallets**
```http
POST http://localhost:8080/api/admin/seed-wallets
```
> Returns: Total number of wallets initialized. Use this to ensure all registered users have a wallet before testing.

---

### Request Management APIs

**Get All Pending Vehicle Requests (Admin)**
```http
GET http://localhost:8080/api/vehicle-requests
```

**Submit New Vehicle Lifecycle Request (User)**
```http
POST http://localhost:8080/api/vehicle-requests
Content-Type: application/json

{
  "userId": 1,
  "requestType": "SELL",
  "vehicleId": 4,
  "newOwnerUserId": 12,
  "reason": "Selling my truck to a friend"
}
```

> **Supported `requestType` values:**

| Type | What It Does |
|------|-------------|
| `ADD` | Request to register a new vehicle |
| `SELL` | Transfer ownership to another registered user |
| `SCRAP` | Permanently retire a vehicle (irreversible) |
| `DEACTIVATE` | Temporarily disable a vehicle from tracking |
| `MODIFY` | Request changes to vehicle details |

**Approve a Vehicle Request (Admin)**
```http
PUT http://localhost:8080/api/vehicle-requests/5/approve
Content-Type: application/json

{
  "adminNotes": "Approved ownership transfer."
}
```

**Reject a Vehicle Request (Admin)**
```http
PUT http://localhost:8080/api/vehicle-requests/5/reject
Content-Type: application/json

{
  "adminNotes": "Insufficient documentation provided."
}
```

---

### Profile Update Request APIs

**Submit a Profile Update Request (User)**
```http
POST http://localhost:8080/api/profile-requests
Content-Type: application/json

{
  "userId": 1,
  "newName": "Albert Joseph",
  "newEmail": "albert@example.com",
  "newPhone": "9876543210"
}
```

**Get Pending Profile Request Count (for Admin notification bell)**
```http
GET http://localhost:8080/api/profile-requests/pending/count
```

---

### Notification APIs

**Get All Notifications for a User**
```http
GET http://localhost:8080/api/user-notifications/user/1
```

**Mark All Notifications as Read**
```http
PUT http://localhost:8080/api/user-notifications/user/1/mark-read
```

> ⚠️ **Correction from earlier versions:** The base path is `/api/user-notifications` — not `/api/notifications`. Using the old path will result in a 404 error.

---

### Removed APIs (No Longer Available)

The following endpoints were removed during architectural optimization:

| Removed Endpoint | Reason |
|-----------------|--------|
| `POST /api/toll/calculate` | Toll calculation is now fully automated within the IoT processing pipeline. Users do not manually trigger calculations. |
| `GET /api/anomalies/pending` | The manual anomaly review workflow was removed. Anomalies now auto-push to the User Notifications system. |
| `GET /api/vehicles/health` | Removed to minimize API surface area and follow least-privilege security principles. |

---

## ✨ Key Features Implemented

### 1. GPS Data Processing ✅
- Input validation using Spring Bean Validation annotations
- Timestamp parsing in standard ISO-8601 format
- GPS coordinate normalization to 6 decimal places for precision
- Vehicle existence and registration status verification before any processing

### 2. Highway Detection ✅
- **IoT Grid Detection** algorithm that checks if a vehicle's GPS coordinate falls inside the geographic boundary of any registered highway
- Distance-to-highway calculation for boundary tolerance
- Real-time highway identification on every GPS ping

### 3. Precision Distance Calculation ✅
- **Haversine Formula** for geodetically accurate GPS distance — accounts for Earth's curvature, unlike flat Euclidean models
- Cumulative distance tracking per journey session
- Session-based highway usage monitoring with entry and exit points

### 4. Automated Toll Calculation ✅
- Vehicle type-based pricing: CAR, BIKE, BUS, TRUCK each have distinct per-km rates
- Rate is defined per highway — different highways can charge different rates
- Toll computation is **fully automated** and embedded in the GPS processing pipeline — no manual trigger required

### 5. Automated Fraud & Anomaly Detection ✅
- **Missing GPS Data:** Flags vehicles that haven't sent data in over 2 hours
- **Highway Inactivity:** Flags vehicles stationary on a highway for over 30 minutes
- **Sudden Disconnection:** Detects abrupt loss of GPS signal
- **Repeated Suspicious Patterns:** Identifies recurring anomalies for a specific vehicle
- All detected anomalies are automatically logged and delivered to the relevant user as a notification

### 6. Highway Usage Tracking ✅
- Entry and exit session management per vehicle per highway
- Distance accumulation across the entire session
- Complete journey audit trail for every trip

### 7. Data Storage & Audit Trail ✅
- Full GPS point history stored in `location_tracking`
- Journey sessions stored in `highway_usage`
- Toll records tied to each session
- Anomaly flags logged automatically

### 8. Integrated IoT Simulation Engine ✅
- Realistic GPS data generation simulating vehicle movement
- Movement state machine (accelerating, highway cruise, decelerating, stopped)
- **Fully integrated into the main backend** — no separate application needed
- Accessible via the built-in IoT Controller directly from the user dashboard

### 9. Real-Time Anomaly Notification Feed ✅
- Replaces the old manual anomaly review interface
- Anomalies detected by the system are **proactively pushed** to the affected user as notifications
- No need for users or admins to periodically check an anomaly list — the system alerts them automatically

### 10. Consolidated UI Billing Summaries 🚧
- Monthly bill generation via `MonthlyBillingScheduler` (implemented)
- Bills and audit trails are visible in the user's dashboard
- Email delivery is planned for a future release once SMTP is configured

### 11. Smart Request & Lifecycle Management System 🚀
- **View-Only User State:** Standard users cannot directly make destructive changes to vehicles or profile data — all changes must be requested
- **Admin Approval Queue:** All requests queue in the Admin dashboard for review before any data is modified
- **Vehicle Lifecycle Management:** Five distinct operations supported — `ADD`, `DEACTIVATE`, `SELL`, `SCRAP`, `MODIFY`
- **Fraud Prevention:** Scrapped vehicles are permanently retired and cannot be reactivated, preventing "ghost vehicle resurrection"
- **Ownership Transfer Validation:** SELL requests require a valid, verified target User ID — preventing ghost transfers to non-existent users

### 12. Live Real-Time Polling & Notification Bell 🛎️
- **Top-Nav Notification Bell:** Embedded in the navigation bar, automatically polls `/api/user-notifications` every **30 seconds**
- **Action Feedback:** Admin approvals or rejections automatically trigger database-level notifications for the relevant user
- **Admin Notes Delivery:** When a request is rejected, the admin's rejection reason and notes are surfaced directly inside the user's notification dropdown
- **Unread Count Badge:** The bell icon displays a live count of unread notifications

### 13. Dynamic Route Visualization 🗺️
- **Custom-Built Responsive Map Interface:** The frontend renders vehicle routes using SVG/CSS3 rendering — built entirely from scratch without relying on external paid map services
- **Live Position Tracking:** Vehicles simulated by the IoT engine visually trace their routes across the map interface, giving administrators a clear real-time view of vehicle movement
- This is more technically impressive than using a pre-built library as it demonstrates custom rendering capability

### 14. Secure Admin Verification 🔐
- All critical actions — vehicle registration, profile changes, ownership transfer, scrapping — require **explicit Admin approval**
- No user can modify sensitive data unilaterally
- Admin decisions are logged with notes and communicated back to users via the notification system

### 15. Optimized Data Retrieval with Paging & Filtering ⚡
- Advanced paging controls for all vehicle and user listing APIs
- Custom `usePagination.js` React hook for consistent frontend paging behavior
- `Paginator.jsx` reusable component for uniform UI across all list views
- Designed to handle thousands of vehicle records without performance degradation

---

## 🧪 Testing the System

Use these scenarios to verify that all major features are working correctly after setup:

### Test Scenario 1: Complete User Journey
```
1. Open http://localhost:3000
2. Navigate to "Users" page
3. Create a new user: Name="Test User", Email="test@example.com", Phone="9876543210"
4. Navigate to "Vehicles" page
5. Submit a vehicle ADD request: Number="TN01TEST", Type="CAR", User ID=1
6. Navigate to "Admin Dashboard" to manage the request
7. Approve the ADD request → vehicle is now registered and wallet is seeded
8. Return to "User Dashboard" — watch the automatic GPS tracking begin
9. Check the "Wallet & Bills" page to see the toll being deducted in real-time
10. View the Admin Dashboard for system-wide statistics and monitoring
```

### Test Scenario 2: IoT Simulation
```
1. Start the backend using start-project.bat (or mvn spring-boot:run)
2. The IoT simulation engine starts automatically with the backend — no separate terminal needed
3. GPS data is sent continuously by the simulation engine to the backend
4. Check the location_tracking table in MySQL to verify GPS points are being stored
5. Verify distance accumulation in the highway_usage table
6. Watch the User Dashboard for the live route visualization updating in real-time
```

### Test Scenario 3: Anomaly Detection
```
1. Submit normal GPS data for a vehicle
2. Stop sending data (simulate vehicle disconnection)
3. Wait for 2+ hours — or temporarily reduce the detection threshold in AnomalyDetectionService for faster testing
4. Check the Notification Bell in the Top Navbar — a MISSING_DATA anomaly notification should appear
5. Verify the data_anomalies table in MySQL for the logged record
```

### Test Scenario 4: Smart Request Workflow
```
1. Open http://localhost:3000 and navigate to the "Vehicles" page
2. Submit a SELL request for a vehicle (provide a valid target User ID)
3. Navigate to "Admin Dashboard"
4. Locate the pending SELL request in the approval queue
5. Approve or reject the request, adding admin notes in the notes field
6. Switch back to the user view
7. Check the Notification Bell in the top navigation bar
8. Confirm that the approval/rejection notification has appeared with the admin's notes
```

### Test Scenario 5: Profile Update Request
```
1. Navigate to the "Users" page
2. Attempt to directly update your profile — direct edits are intentionally blocked for security
3. Submit a "Profile Update Request" with your new name, email, or phone number
4. Log in as Admin and navigate to the Profile Requests section
5. Approve the request
6. Verify the user's profile has been updated in the database
7. Check that the user received a notification confirming the update, including any admin notes
```

---

## 🐛 Troubleshooting Guide

### Common Issues and Solutions

**Issue 1: `mvn: command not found`**
```
Solution:
- Run fix-maven.bat — it will automatically download and install Maven for you
- OR use IntelliJ IDEA, which has Maven built-in and requires no PATH setup
- OR download Maven manually from: https://maven.apache.org/download.cgi
  and add its bin/ folder to your system PATH, then restart your terminal
```

**Issue 2: MySQL Connection Refused**
```
Solution:
- Ensure MySQL service is running: Open services.msc (Windows) and start MySQL80
- Verify the database exists: Log in to MySQL and run: SHOW DATABASES;
- Check that your credentials in the .env file are correct
- Ensure port 3306 is not blocked by a firewall or another application
```

**Issue 3: Port 8080 Already in Use**
```
Solution:
- Find what's using the port: netstat -ano | findstr :8080
- Kill the process: taskkill /PID <process_id> /F
- OR change the port in application.properties: server.port=8081
  (remember to update your frontend API base URL too if you do this)
```

**Issue 4: Frontend `npm install` Fails**
```
Solution:
- Clear the npm cache: npm cache clean --force
- Delete the node_modules/ folder
- Delete package-lock.json
- Run: npm install
If behind a corporate proxy, configure npm proxy:
  npm config set proxy http://your-proxy:port
```

**Issue 5: CORS Errors in Browser Console**
```
Solution:
- Ensure the backend is running on port 8080
- Check the proxy settings in frontend/vite.config.js
- Verify the API base URL in frontend/src/services/api.js points to http://localhost:8080
```

**Issue 6: Map / Route Visualization Not Rendering**
```
Solution:
- The map visualization uses custom SVG/CSS3 rendering — no external library is required
- If the map area appears blank, check the browser console for JavaScript errors
- Verify that GPS data exists for the vehicle in the location_tracking table before viewing the route
- Check for any ID or variable name mismatches in the visualization component
- If you are experimenting with adding react-leaflet separately, install it with:
    npm install leaflet react-leaflet
  and import the CSS in your component: import 'leaflet/dist/leaflet.css';
```

**Issue 7: Notification Bell Not Updating**
```
Solution:
- Verify the backend is running and the following URL returns a valid response:
    GET http://localhost:8080/api/user-notifications/user/{id}
  (Note: the correct path is /api/user-notifications — not /api/notifications)
- Open the browser DevTools → Network tab and watch for polling requests every 30 seconds
- Confirm the logged-in user's ID is being passed correctly to the notification API call
- The polling interval is 30 seconds — trigger a new admin action to see an update sooner
```

**Issue 8: Vehicle Request Stuck in PENDING**
```
Solution:
- Log in as Admin and navigate to the Admin Dashboard
- Check the "Pending Requests" section for any unreviewed items
- Approve or reject the request — the status will update immediately
- If the request disappears without a user notification appearing, 
  inspect the user_notifications table in MySQL to verify the record was created
```

---

## ✨ Key Features Highlights (Quick Reference for Viva)

| Feature | Technical Implementation | Talking Point |
|---------|--------------------------|---------------|
| Distance Calculation | Haversine Formula | "Geodetically accurate — accounts for Earth's curvature" |
| Highway Detection | IoT Grid Detection (Coordinate bounding) | "More technical and scalable than simple radius checks" |
| Toll Calculation | Per-type rate × Haversine distance | "Fully automated in the processing pipeline — zero manual steps" |
| Fraud Detection | Rule-based anomaly engine | "Proactive — pushes alerts rather than waiting for manual review" |
| Admin Approval | Request queue with state machine | "Prevents unauthorized data tampering at the architecture level" |
| Billing | Spring Scheduler + BillService | "Automated monthly audit trail generation" |
| Notifications | 30-second HTTP polling | "Pragmatic real-time alternative to WebSockets for this scale" |
| Map Visualization | Custom SVG/CSS3 rendering | "Built entirely from scratch — demonstrates custom rendering skill" |

---

## 👨‍💻 Author Information

**Name:** Albert J  
**Institution:** SRM Institute of Science and Technology - Trichy  
**Program:** Master of Computer Applications (MCA)  
**Academic Year:** 2024-2026  
**Project Type:** Final Year Project

**Contact Information:**  
📧 Email: [albertcyse@gmail.com](mailto:albertcyse@gmail.com)  
🌍 Location: Tamil Nadu, India  
🎓 Specialization: Full Stack Development, IoT Systems

**Project Guidance:**  
Under the supervision of MCA Department Faculty  
SRM Institute of Science and Technology - Trichy Campus

---

## 🙏 Acknowledgments

This project would not have been possible without the support and guidance of:

- **SRM Institute of Science and Technology - Trichy** — for providing an excellent academic environment and resources
- **MCA Department Faculty** — for valuable guidance and mentorship throughout the project lifecycle
- **Spring Framework Team** — for the robust, enterprise-grade Spring Boot framework
- **React Team** — for the powerful and flexible React UI library
- **MySQL Community** — for the reliable and widely-used database engine
- **Stack Overflow Community** — for countless community-sourced solutions and best practices
- **Family & Friends** — for continuous support and encouragement throughout the process

---

## 📄 License & Usage

**License:** Academic Use Only

This project is developed as part of the MCA Final Year Project curriculum at SRM Institute of Science and Technology - Trichy. The source code and documentation are intended for:

- ✅ Academic evaluation and assessment
- ✅ Educational purposes and learning reference
- ✅ Portfolio demonstration
- ✅ Research and development inspiration

**Copyright © 2026 Albert J. All rights reserved.**

Unauthorized commercial use, redistribution, or plagiarism is strictly prohibited.

---

## 🚧 Project Status & Roadmap

### Current Status: Under Active Development

#### ✅ Completed Features (~85%)
- [x] Backend REST API architecture (all controllers, services, repositories)
- [x] Database schema design (11 tables, fully normalized)
- [x] GPS data ingestion and processing pipeline
- [x] IoT Grid Detection for highway identification
- [x] Haversine Formula distance calculation
- [x] Automated toll calculation (embedded in pipeline)
- [x] Anomaly detection system (rule-based, proactive)
- [x] Integrated IoT simulation engine
- [x] Complete frontend UI pages (Admin + User views)
- [x] Highway usage session tracking
- [x] Smart request system (Vehicle & Profile lifecycle with 5 request types)
- [x] Real-time notification polling & notification bell UI (30-second interval)
- [x] Dynamic route visualization (custom SVG/CSS3)
- [x] Modular CSS3 architecture (global, layout, buttons, component-level)
- [x] Paging and filtering for large datasets
- [x] Admin approval workflow with notes and notification delivery

#### 🚧 In Progress (~10%)
- [ ] Full automated monthly billing pipeline (scheduler exists, UI integration pending)
- [ ] Email/SMTP notification delivery (planned — SMTP config pending)
- [ ] Enhanced admin analytics dashboard
- [ ] Payment gateway integration
- [ ] Role-based authentication and authorization (JWT)

#### 📋 Planned Features (~5%)
- [ ] Mobile application (React Native or PWA)
- [ ] Real-time WebSocket notifications (upgrade from HTTP polling)
- [ ] Advanced analytics and reporting charts
- [ ] Google Maps or OpenStreetMap API integration
- [ ] Multi-language support

---

## 📞 Support & Contact

For queries, suggestions, or technical support related to this project:

**Primary Contact:**  
📧 **Email:** [albertcyse@gmail.com](mailto:albertcyse@gmail.com)

**Response Time:**
- Academic queries: Within 24 hours
- Technical issues: Within 48 hours
- General inquiries: Within 72 hours

**Best Ways to Reach:**
1. Email (preferred)
2. Project documentation review
3. In-person discussion (SRM Trichy campus)

---

## 📚 Documentation & Resources

### Project Documentation

All technical guides and architecture documents are available in the `/docs` folder in the project root:

| Document | Description |
|---------|-------------|
| Highway GPS Tracking Walkthrough | End-to-end explanation of the GPS data flow |
| Anomaly Detection System Guide | How fraud patterns are detected and surfaced |
| Data Storage Architecture | Database design decisions and table relationships |
| IoT Simulation Engine Manual | How the integrated IoT engine works |
| Toll Calculation Logic | Step-by-step breakdown of the billing formula |
| API Integration Guide | How the frontend communicates with the backend |
| Smart Request System Guide | Vehicle lifecycle and profile request workflows |
| Notification System Architecture | How the polling and alert delivery system works |

### External References

| Resource | URL |
|---------|-----|
| Spring Boot Documentation | https://spring.io/projects/spring-boot |
| React Official Docs | https://react.dev/learn |
| MySQL Reference Manual | https://dev.mysql.com/doc/ |
| Haversine Formula | https://en.wikipedia.org/wiki/Haversine_formula |
| Vite Documentation | https://vitejs.dev/ |

---

## ⭐ Star This Project!

If you find this project helpful for learning or as a reference for your own academic projects, please consider:
- ⭐ Starring the repository
- 🔄 Sharing it with fellow students
- 💬 Providing feedback or suggestions
- 🐛 Reporting any issues you find

---

**Made with ❤️ for MCA Final Year Project**  
**SRM Institute of Science and Technology - Trichy**

---

**Last Updated:** March 31, 2026  
**Version:** 2.1.0-SNAPSHOT  
**Build Status:** 🚧 Under Active Development  
**Completion:** ~85%

---

**README.md — Comprehensive Project Documentation**  
**Maintained By:** Albert J