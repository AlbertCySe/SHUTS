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

The **Smart Highway Usage-Based Tolling System** is an innovative full-stack web application that revolutionizes highway toll collection by implementing GPS-based distance tracking. Unlike traditional toll plazas, this system automatically calculates toll charges based on actual distance traveled, eliminating queues and providing a fair, transparent billing experience.

**Project Status:** ✅ **Production Ready**

**Developed By:** Albert J  
**Institution:** SRM Institute of Science and Technology - Trichy  
**Program:** Master of Computer Applications (MCA)  
**Academic Year:** 2024-2026  
**Project Type:** Final Year Project

> **🚀 Quick Start:** Run `start-project.bat` for one-click setup and launch!  
> **🔒 Secure Setup:** Uses `.env` file for credential management - no passwords in code!

---

## 🎯 Problem Statement

### Issues with Traditional Toll Systems:
- ❌ **Fixed charges** regardless of actual distance traveled
- ❌ **Long queues** at toll plazas causing traffic congestion
- ❌ **Cash transactions** leading to corruption and delays
- ❌ **No transparency** in toll calculation
- ❌ **Unfair pricing** for short-distance travelers
- ❌ **Environmental impact** due to vehicle idling at toll booths

### Our Smart Solution:
- ✅ **GPS-based tracking** for precise distance measurement
- ✅ **Automated billing** without physical toll booths
- ✅ **Digital wallet** integration for cashless payments
- ✅ **Monthly consolidated** bills sent via email
- ✅ **Real-time monitoring** with IoT simulation
- ✅ **Admin dashboard** for system oversight
- ✅ **Fraud detection** with anomaly alerts
- ✅ **Fair pricing** based on actual usage
- ✅ **Smart request system** with admin approval workflows
- ✅ **Live notifications** for real-time admin-user communication
- ✅ **Interactive maps** for live vehicle route visualization

---

## 🧠 How The System Works

```
┌─────────────────────────────────────────────────────────────┐
│  STEP 1: Vehicle Registration                               │
│  User registers → Vehicle details stored → Wallet created   │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 2: GPS Data Collection (IoT Simulation)               │
│  Vehicle sends GPS → Latitude, Longitude, Timestamp         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 3: Highway Detection                                  │
│  Backend checks → Is vehicle on highway? (Bounding Box)     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 4: Distance Calculation                               │
│  Haversine Formula → Calculate distance between GPS points  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 5: Toll Calculation                                   │
│  Vehicle Type × Distance × Rate/km → Total Toll Amount      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 6: Wallet Deduction                                   │
│  Deduct toll from digital wallet → Update balance           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 7: Monthly Billing                                    │
│  Scheduler generates bill → Email sent to user              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack

### Backend Technologies
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Language** | Java | 17 | Core programming language |
| **Framework** | Spring Boot | 3.2.1 | Application development framework |
| **ORM** | Spring Data JPA | 3.2.1 | Database object-relational mapping |
| **Database** | MySQL | 8.0+ | Data persistence |
| **Build Tool** | Maven | 3.6+ | Dependency management & build |
| **Utilities** | Lombok | 1.18.30 | Reduce boilerplate code |
| **Validation** | Spring Validation | 3.2.1 | Input validation |
| **Email** | Spring Mail | 3.2.1 | Email notifications |
| **Scheduling** | Spring Scheduler | 3.2.1 | Automated background tasks |

### Frontend Technologies
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Library** | React | 18.2 | UI component library |
| **Build Tool** | Vite | 5.0 | Fast build and hot reload |
| **Routing** | React Router | 6.x | Client-side navigation |
| **HTTP Client** | Axios | 1.6 | API communication |
| **Maps** | React-Leaflet | Latest | Interactive GPS route maps |
| **Styling** | CSS3 | — | Modern responsive design |

### Additional Modules
| Module | Technology | Purpose |
|--------|-----------|---------|
| **IoT Simulator** | Spring Boot | Simulate GPS devices |
| **Fraud Detection** | Custom Algorithms | Anomaly detection system |
| **Notification Engine** | Spring Boot + Polling | Real-time admin-user alerts |
| **Request Workflow** | Spring Boot | Admin approval queue system |

---

## 📦 Complete Project Structure

```
smart-highway-tolling-system/
│
├── Initial/                           # Main Backend Application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/highway/tolling/
│   │   │   │   ├── controller/        # REST API Controllers
│   │   │   │   │   ├── UserController.java
│   │   │   │   │   ├── VehicleController.java
│   │   │   │   │   ├── HighwayController.java
│   │   │   │   │   ├── IoTController.java
│   │   │   │   │   ├── TollCalculationController.java
│   │   │   │   │   ├── HighwayUsageController.java
│   │   │   │   │   ├── AnomalyReviewController.java
│   │   │   │   │   ├── VehicleRequestController.java  # NEW
│   │   │   │   │   └── NotificationController.java    # NEW
│   │   │   │   │
│   │   │   │   ├── model/             # JPA Entity Models
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Vehicle.java
│   │   │   │   │   ├── VehicleType.java (enum)
│   │   │   │   │   ├── Highway.java
│   │   │   │   │   ├── LocationTracking.java
│   │   │   │   │   ├── HighwayUsage.java
│   │   │   │   │   ├── Bill.java
│   │   │   │   │   ├── BillStatus.java (enum)
│   │   │   │   │   ├── DataAnomaly.java
│   │   │   │   │   ├── AnomalyType.java (enum)
│   │   │   │   │   ├── AnomalySeverity.java (enum)
│   │   │   │   │   ├── ReviewStatus.java (enum)
│   │   │   │   │   ├── VehicleRequest.java            # NEW
│   │   │   │   │   ├── VehicleRequestType.java (enum) # NEW
│   │   │   │   │   ├── ProfileRequest.java            # NEW
│   │   │   │   │   └── UserNotification.java          # NEW
│   │   │   │   │
│   │   │   │   ├── repository/        # Data Access Layer
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── VehicleRepository.java
│   │   │   │   │   ├── HighwayRepository.java
│   │   │   │   │   ├── LocationTrackingRepository.java
│   │   │   │   │   ├── HighwayUsageRepository.java
│   │   │   │   │   ├── BillRepository.java
│   │   │   │   │   ├── DataAnomalyRepository.java
│   │   │   │   │   ├── VehicleRequestRepository.java  # NEW
│   │   │   │   │   └── UserNotificationRepository.java # NEW
│   │   │   │   │
│   │   │   │   ├── service/           # Business Logic Services
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   ├── VehicleService.java
│   │   │   │   │   ├── HighwayService.java
│   │   │   │   │   ├── IoTIdentificationService.java
│   │   │   │   │   ├── LocationTrackingService.java
│   │   │   │   │   ├── HighwayDetectionService.java
│   │   │   │   │   ├── DistanceCalculatorService.java
│   │   │   │   │   ├── HighwayUsageService.java
│   │   │   │   │   ├── HighwayUsageAggregationService.java
│   │   │   │   │   ├── TollCalculationService.java
│   │   │   │   │   ├── AnomalyDetectionService.java
│   │   │   │   │   ├── BillGenerationService.java
│   │   │   │   │   ├── VehicleRequestService.java     # NEW
│   │   │   │   │   └── NotificationService.java       # NEW
│   │   │   │   │
│   │   │   │   ├── dto/               # Data Transfer Objects
│   │   │   │   │   ├── IoTDataRequest.java
│   │   │   │   │   └── IoTDataResponse.java
│   │   │   │   │
│   │   │   │   └── TollingSystemApplication.java
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── static/
│   │   │
│   │   └── test/                      # Unit Tests
│   │
│   ├── frontend/                      # React Frontend Application
│   │   ├── src/
│   │   │   ├── pages/
│   │   │   │   ├── Home.jsx
│   │   │   │   ├── AdminUsers.jsx           # NEW
│   │   │   │   ├── AdminVehicles.jsx        # NEW
│   │   │   │   ├── Vehicles.jsx
│   │   │   │   ├── Highways.jsx
│   │   │   │   ├── Locations.jsx
│   │   │   │   ├── WalletBills.jsx
│   │   │   │   └── Admin.jsx
│   │   │   │
│   │   │   ├── components/
│   │   │   │   ├── admin/                   # NEW
│   │   │   │   │   ├── AdminProfileRequests.jsx
│   │   │   │   │   ├── AdminUsersTable.jsx
│   │   │   │   │   ├── AdminVehicleRequests.jsx
│   │   │   │   │   ├── AdminVehiclesTable.jsx
│   │   │   │   │   └── AdminVehicleModal.jsx
│   │   │   │   ├── dashboard/               # NEW
│   │   │   │   ├── vehicles/                # NEW
│   │   │   │   │   ├── VehicleRequestModal.jsx
│   │   │   │   │   └── VehicleTable.jsx
│   │   │   │   ├── wallet/                  # NEW
│   │   │   │   ├── NotificationBell.jsx     # NEW
│   │   │   │   ├── Paginator.jsx            # NEW
│   │   │   │   ├── LoadingFallback.jsx
│   │   │   │   └── Header.jsx
│   │   │   │
│   │   │   ├── hooks/
│   │   │   │   └── usePagination.js         # NEW
│   │   │   │
│   │   │   ├── services/
│   │   │   │   └── api.js
│   │   │   │
│   │   │   ├── App.jsx
│   │   │   ├── main.jsx
│   │   │   └── index.css
│   │   │
│   │   ├── public/
│   │   ├── index.html
│   │   ├── package.json
│   │   ├── vite.config.js
│   │   └── .gitignore
│   │
│   ├── pom.xml                        # Maven Configuration
│   ├── .gitignore
│   ├── README.md                      # This file
│   ├── start-project.bat              # ⭐ ONE-CLICK LAUNCHER (All-in-One)
│
└── iot-simulator/                     # Standalone IoT GPS Simulator
    ├── src/main/java/com/highway/simulator/
    │   ├── model/
    │   │   ├── GPSPoint.java
    │   │   ├── Route.java
    │   │   ├── SimulatedVehicle.java
    │   │   ├── VehicleState.java (enum)
    │   │   └── QueuedGPSData.java
    │   │
    │   ├── service/
    │   │   ├── GPSGenerator.java
    │   │   ├── MovementSimulator.java
    │   │   ├── BackendClient.java
    │   │   └── OfflineStorageService.java
    │   │
    │   ├── config/
    │   │   └── HighwayConfig.java
    │   │
    │   └── SimulatorApplication.java
    │
    ├── src/main/resources/
    │   └── application.properties
    │
    ├── pom.xml
    ├── .gitignore
    └── README.md
```

---

## 🗄️ Database Schema Design

### Entity-Relationship Diagram

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
       │               │  Requests    │ ◄── NEW
       │               │──────────────│
       │               │  id (PK)     │
       │               │  vehicle_id  │
       │               │  user_id     │
       │               │  request_type│
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
┌──────────────┐        ┌──────────────┐
│    Bills     │        │    Data      │
│──────────────│        │  Anomalies   │
│bill_id  (PK) │        │──────────────│
│user_id  (FK) │        │  id (PK)     │
│vehicle_id    │        │  vehicle_id  │
│bill_month    │        │  type        │
│total_distance│        │  description │
│total_amount  │        │  severity    │
│status        │        │  detected_at │
│due_date      │        │  review_stat │
│created_at    │        │  reviewed_at │
└──────────────┘        └──────────────┘

┌──────────────┐        ┌──────────────┐
│   Profile    │        │    User      │
│  Requests    │ NEW    │Notifications │ NEW
│──────────────│        │──────────────│
│  id (PK)     │        │  id (PK)     │
│  user_id     │        │  user_id     │
│  new_name    │        │  message     │
│  new_email   │        │  is_read     │
│  new_phone   │        │  created_at  │
│  status      │        │  linked_req  │
│  admin_notes │        └──────────────┘
└──────────────┘
```

### Table Details

1. **users** - User account information
2. **vehicles** - Registered vehicles (CAR, BIKE, BUS, TRUCK)
3. **wallets** - Digital wallet for toll payments
4. **highways** - Highway definitions with GPS boundaries and rates
5. **location_tracking** - Raw GPS data with highway association
6. **highway_usage** - Entry/exit sessions for billing
7. **bills** - Monthly consolidated toll bills
8. **data_anomalies** - Fraud detection flags
9. **vehicle_requests** *(NEW)* - Pending/Approved/Rejected queues for vehicle lifecycle stages (Add, Scrap, Sell, Deactivate, Modify)
10. **profile_requests** *(NEW)* - Pending user profile modification requests (Phone number, Name, Email)
11. **user_notifications** *(NEW)* - Real-time alerts tied to Admin approval flows

---

## 🚀 Complete Setup Guide (From Scratch)

### Prerequisites Installation

#### 1. Install Java 17 (JDK)
```bash
# Download from: https://adoptium.net/
# After installation, verify:
java -version
# Expected output: java version "17.x.x"
```

#### 2. Install Node.js 18+ and npm
```bash
# Download from: https://nodejs.org/
# After installation, verify:
node -v    # Should show v18.x.x or higher
npm -v     # Should show npm version
```

#### 3. Install MySQL 8.0+
```bash
# Download from: https://dev.mysql.com/downloads/mysql/
# During installation:
# - Set root password (remember this!)
# - Start MySQL server
```

#### 4. Install Maven (Optional - IntelliJ has built-in Maven)
```bash
# Download from: https://maven.apache.org/download.cgi
# After installation, verify:
mvn -v
# Expected output: Apache Maven 3.x.x
```

**⚡ Helper Scripts Available:**
If you don't have Maven or Node.js installed, use our automated installer scripts:

- **`install-maven.bat`** - Auto-downloads and installs Apache Maven
  - Downloads Maven 3.9.5 from Apache archives
  - Extracts to `C:\Program Files\Apache\maven`
  - Offers to add to system PATH

- **`install-nodejs.bat`** - Auto-downloads and installs Node.js LTS
  - Downloads latest Node.js LTS installer
  - Silent installation mode
  - Auto-configures PATH

**Just run these scripts if prerequisites are missing!**


### Step-by-Step Project Setup

#### STEP 1: Download the Project
```bash
# Option A: Clone from Git
git clone https://github.com/albertcyse/smart-highway-tolling-system.git
cd smart-highway-tolling-system

# Option B: Download ZIP
# Extract to desired location
# Navigate to the project folder
```

#### STEP 2: Configure Database Credentials (First-Time Setup)

The system uses a `.env` file for secure credential management.

**Option A: Automatic Setup (Recommended - easiest!)**
```bash
# Just run the launcher - it will prompt for credentials on first run
start-project.bat

# You'll be asked:
# Enter MySQL Username (default: root): root
# Enter MySQL Password: your_password_here

# .env file is created automatically!
```

**Option B: Manual .env Setup**
```bash
# 1. Copy the example file
copy .env.example .env

# 2. Edit .env with your credentials
# Open .env in any text editor and update:
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

> **Note:** The database `tolling_system` will be created automatically when you start the backend!  
> **Security:** `.env` is gitignored - your credentials won't be committed.

#### STEP 3: Run the Project (One-Click Launch!)

**Windows:**
```bash
# Navigate to project root
cd Initial

# Run the all-in-one launcher
start-project.bat

# ✅ Checks all prerequisites (Java, Node.js, Maven, MySQL)
# ✅ Loads credentials from .env
# ✅ Starts backend (port 8080)
# ✅ Starts frontend (port 3000)
# ✅ Opens browser automatically
```

**The launcher will:**
1. Check for Java 17+, Node.js 18+, MySQL service
2. Load database credentials from `.env`
3. Start Spring Boot backend (BLUE terminal)
4. Start React frontend (YELLOW terminal)
5. Open http://localhost:3000 in your browser

**Expected Output:**
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

Starting Backend... ✓
Starting Frontend... ✓
```

#### Manual Startup (Alternative)

If you prefer to run components separately:

**Start Backend:**
```bash
cd Initial
mvn spring-boot:run
# Backend will run on http://localhost:8080
```

**Start Frontend (in new terminal):**
```bash
cd Initial/frontend
npm run dev
# Frontend will run on http://localhost:3000
```

---

**What it does:**
- ✅ Checks Java 17+ installation
- ✅ Checks Node.js 18+ installation
- ✅ Checks Maven (suggests IntelliJ if not found)
- ✅ Auto-installs frontend npm dependencies
- ✅ Starts Backend (Spring Boot) in blue terminal
- ✅ Starts Frontend (React + Vite) in yellow terminal
- ✅ Opens browser automatically to http://localhost:3000
- ✅ Color-coded terminals for easy identification

**Just double-click and go!** 🚀

#### STEP 5: Build and Run Backend

**Option A: Using Maven Command Line**
```bash
# Navigate to Initial folder
cd Initial

# Clean and build project
mvn clean install

# Run Spring Boot application
mvn spring-boot:run

# Backend will start on: http://localhost:8080
# Wait for message: "Started TollingSystemApplication"
```

**Option B: Using IntelliJ IDEA (Recommended)**
```
1. Open IntelliJ IDEA
2. File → Open → Select "Initial" folder
3. Wait for Maven to sync dependencies
4. Navigate to: src/main/java/com/highway/tolling/TollingSystemApplication.java
5. Right-click on file
6. Click "Run 'TollingSystemApplication'"
7. Backend starts automatically!
```

#### STEP 6: Run Frontend (New Terminal)
```bash
# Open NEW terminal (keep backend running)
cd Initial/frontend

# Start Vite development server
npm run dev

# Frontend will start on: http://localhost:3000
# Browser will open automatically
```

#### STEP 7: Run IoT Simulator (Optional - For Testing)
```bash
# Open THIRD terminal
cd iot-simulator

# Build and run simulator
mvn clean install
mvn spring-boot:run

# Follow on-screen menu to simulate GPS data
```

---

## 🌐 Accessing the Application

### Application URLs
| Service | URL | Description | Status |
|---------|-----|-------------|--------|
| **Frontend (React)** | http://localhost:3000 | Main web interface | Primary |
| **Backend API** | http://localhost:8080 | REST API endpoints | Primary |
| **API Health Check** | http://localhost:8080/api/vehicles/health | Server status | Test |
| **Database** | localhost:3306 | MySQL server | Backend |
| **IoT Simulator** | Console Application | GPS data generator | Optional |

### First-Time Access
1. Open browser: http://localhost:3000
2. You'll see the Home page with navigation
3. Start by creating a User
4. Then register a Vehicle
5. Add Highway definitions
6. Test GPS tracking

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

### Vehicle Management APIs

**Register Vehicle**
```http
POST http://localhost:8080/api/vehicles
Content-Type: application/json

{
  "vehicleNumber": "TN01AB1234",
  "vehicleType": "CAR",
  "userId": 1
}
```

**Get All Vehicles**
```http
GET http://localhost:8080/api/vehicles
```

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

### IoT Data Submission

**Send GPS Location**
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

### Highway Usage APIs

**Get Total Distance**
```http
GET http://localhost:8080/api/highway-usage/total/1
```

**Get Distance Breakdown**
```http
GET http://localhost:8080/api/highway-usage/breakdown/1
```

**Get Complete Summary**
```http
GET http://localhost:8080/api/highway-usage/summary/1
```

### Toll Calculation APIs

**Calculate Toll**
```http
POST http://localhost:8080/api/toll/calculate
Content-Type: application/json

{
  "vehicleType": "CAR",
  "distanceKm": 45.5
}
```

### Anomaly Review APIs

**Get Pending Anomalies**
```http
GET http://localhost:8080/api/anomalies/pending
```

**Get Vehicle Anomalies**
```http
GET http://localhost:8080/api/anomalies/vehicle/1
```

### Request Management APIs *(NEW)*

**Get All Pending Vehicle Requests (Admin)**
```http
GET http://localhost:8080/api/vehicle-requests
```

**Submit New Vehicle Request (User)**
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

> **Supported `requestType` values:** `ADD`, `DEACTIVATE`, `SELL`, `SCRAP`, `MODIFY`

**Approve Vehicle Request (Admin)**
```http
PUT http://localhost:8080/api/vehicle-requests/5/approve
Content-Type: application/json

{
  "adminNotes": "Approved ownership transfer."
}
```

**Reject Vehicle Request (Admin)**
```http
PUT http://localhost:8080/api/vehicle-requests/5/reject
Content-Type: application/json

{
  "adminNotes": "Insufficient documentation provided."
}
```

### Notification APIs *(NEW)*

**Get User Notifications**
```http
GET http://localhost:8080/api/notifications/user/1
```

**Mark Notifications as Read**
```http
PUT http://localhost:8080/api/notifications/user/1/mark-read
```

---

## ✨ Key Features Implemented

### 1. GPS Data Processing ✅
- Input validation with Bean Validation
- Timestamp parsing (ISO-8601 format)
- GPS coordinate normalization (6 decimal places)
- Vehicle existence verification

### 2. Highway Detection ✅
- Bounding box algorithm with tolerance
- Distance-to-highway calculation
- Real-time highway identification

### 3. Distance Calculation ✅
- Haversine formula for GPS distance
- Cumulative distance tracking
- Session-based usage monitoring

### 4. Toll Calculation ✅
- Vehicle type-based pricing (CAR, BIKE, BUS, TRUCK)
- Per-kilometer rate application
- Automatic toll computation

### 5. Anomaly Detection ✅
- Missing GPS data (> 2 hours)
- Inactivity on highway (> 30 minutes)
- Sudden disconnection alerts
- Repeated suspicious patterns

### 6. Highway Usage Tracking ✅
- Entry/exit session management
- Distance accumulation per highway
- Complete journey audit trail

### 7. Data Storage & Audit ✅
- Complete GPS data history
- Highway usage sessions
- Toll charge records
- Anomaly flags for review

### 8. IoT Simulator ✅
- Realistic GPS generation
- Movement state machine
- Offline storage with auto-sync
- Multi-vehicle simulation

### 9. Admin Dashboard 🚧
- System statistics
- Vehicle monitoring
- Anomaly review interface
- Request approval queue

### 10. Automated Billing 🚧 (Under Development)
- Monthly bill generation
- Email notifications
- Payment status tracking

### 11. Smart Request System 🚀 *(NEW)*
- **View-only User State:** Direct destructive and modification actions are disabled for standard users for system security
- **Admin Review Queue:** Users submit requests (`Profile Update Request` or `Vehicle Lifecycle Requests`) which queue in the admin dashboard for review
- **Vehicle Lifecycle Management:** Five distinct request types for complete vehicle management: `ADD`, `DEACTIVATE`, `SELL`, `SCRAP`, `MODIFY`
- **Fraud Prevention:** Scraped vehicles cannot be resurrected once marked. Sell requests strictly require valid target User IDs to prevent ghost transfers

### 12. Live Real-Time Polling & Notifications 🛎️ *(NEW)*
- **Top-Nav Notification Bell:** A modern UI component embedded in the top navigation bar that automatically polls the backend (`/api/notifications`) every 30 seconds
- **Action Feedback:** Admins approving or rejecting requests automatically trigger database-level notifications delivered to the relevant user
- **Admin Notes Attached:** When an admin rejects a profile update request, their rejection reason and notes are surfaced directly inside the user's notification drop-down for full transparency

### 13. Interactive IoT Tracking & Maps 🗺️ *(NEW)*
- **React-Leaflet Integration:** The frontend now renders dynamic route tracking maps powered by the React-Leaflet library — no external paid map service required
- **Live Marker Tracking:** Vehicles simulated by the IoT engine visually trace their actual GeoJSON route paths across the map interface in real time, giving administrators a clear view of vehicle movement

---

## 🧪 Testing the System

### Test Scenario 1: Complete User Journey
```bash
1. Open http://localhost:3000
2. Go to "Users" page
3. Create new user: Name="Test User", Email="test@example.com"
4. Go to "Vehicles" page
5. Register vehicle: Number="TN01TEST", Type="CAR", User ID=1
6. Go to "Highways" page
7. Add highway: NH-44 with GPS coordinates and rates
8. Go to "GPS Tracking" page
9. Submit GPS data for your vehicle
10. Check "Highway Usage" to see distance accumulation
11. View "Admin" dashboard for statistics
```

### Test Scenario 2: IoT Simulator
```bash
1. Start backend (Terminal 1)
2. Start IoT simulator (Terminal 2)
3. Select "Single vehicle simulation"
4. Watch GPS data being sent to backend
5. Check location_tracking table in MySQL
6. Verify distance accumulation in highway_usage table
7. Observe live route being drawn on the map in the frontend
```

### Test Scenario 3: Anomaly Detection
```bash
1. Submit normal GPS data
2. Wait for 2+ hours (or adjust time in code for testing)
3. Check anomalies API: GET /api/anomalies/pending
4. Verify MISSING_DATA anomaly is flagged
```

### Test Scenario 4: Smart Request Workflow *(NEW)*
```bash
1. Open http://localhost:3000 and navigate to "Vehicles" page
2. Submit a SELL request for a vehicle (provide a valid target User ID)
3. Navigate to "Admin" dashboard
4. Find the pending request in the approval queue
5. Approve or reject the request with admin notes
6. Switch back to the user view
7. Check the Notification Bell in the top navigation
8. Confirm the approval/rejection notification has appeared
```

### Test Scenario 5: Profile Update Request *(NEW)*
```bash
1. Navigate to "Users" page
2. Attempt to update your profile — note direct edits are blocked
3. Submit a "Profile Update Request" with new name/email/phone
4. Log in as Admin and approve the request
5. Verify the user profile has been updated
6. Check that the user received a notification with admin notes
```

---

## 🐛 Troubleshooting Guide

### Common Issues and Solutions

**Issue 1: "mvn: command not found"**
```
Solution:
- Use IntelliJ IDEA (has built-in Maven)
- OR download Maven from: https://maven.apache.org/download.cgi
- Add Maven to system PATH
- Restart terminal
```

**Issue 2: MySQL connection refused**
```
Solution:
- Check MySQL is running: services.msc (Windows)
- Verify database exists: SHOW DATABASES;
- Check credentials in application.properties
- Ensure port 3306 is not blocked
```

**Issue 3: Port 8080 already in use**
```
Solution:
- Find process using port: netstat -ano | findstr :8080
- Kill process: taskkill /PID <process_id> /F
- OR change port in application.properties: server.port=8081
```

**Issue 4: Frontend npm install fails**
```
Solution:
- Clear npm cache: npm cache clean --force
- Delete node_modules folder
- Delete package-lock.json
- Run: npm install again
```

**Issue 5: CORS errors in browser console**
```
Solution:
- Ensure backend is running on port 8080
- Check frontend proxy settings in vite.config.js
- Verify API base URL in frontend/src/services/api.js
```

**Issue 6: Map not rendering in Locations page** *(NEW)*
```
Solution:
- Ensure react-leaflet and leaflet packages are installed:
  npm install react-leaflet leaflet
- Verify the Leaflet CSS is imported in your component or index.css:
  import 'leaflet/dist/leaflet.css';
- Check that GPS data exists for the vehicle before opening the map view
```

**Issue 7: Notification bell not updating** *(NEW)*
```
Solution:
- Verify backend is running and /api/notifications/user/{id} returns a valid response
- Check browser console for polling errors (network tab)
- Confirm the logged-in user ID is being passed correctly to the notification API call
- Polling interval is 30 seconds — wait or trigger a new admin action to see update
```

**Issue 8: Vehicle request stuck in PENDING** *(NEW)*
```
Solution:
- Log in as Admin and navigate to the Admin dashboard
- Check the "Pending Requests" section for unreviewed items
- Approve or reject the request — status will update immediately
- If the request disappears without notification, check the user_notifications table in MySQL
```

---

## 👨‍💻 Author Information

**Name:** Albert J

**Institution:** SRM Institute of Science and Technology - Trichy  
**Program:** Master of Computer Applications (MCA)  
**Academic Year:** 2025-2026  
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

- **SRM Institute of Science and Technology - Trichy** for providing excellent academic environment and resources
- **MCA Department Faculty** for their valuable guidance and mentorship throughout the project
- **Spring Framework Team** for the robust Spring Boot framework
- **React Team** for the powerful and flexible React library
- **MySQL Community** for the reliable database system
- **React-Leaflet & Leaflet.js Teams** for the open-source interactive mapping library
- **Stack Overflow Community** for countless solutions and best practices
- **Family & Friends** for their continuous support and encouragement

---

## 📄 License & Usage

**License:** Academic Use Only

This project is developed as part of the MCA Final Year Project curriculum at SRM Institute of Science and Technology - Trichy. The source code and documentation are intended for:

- ✅ Academic evaluation and assessment
- ✅ Educational purposes and learning
- ✅ Portfolio demonstration
- ✅ Research and development

**Copyright © 2026 Albert J. All rights reserved.**

Unauthorized commercial use, redistribution, or plagiarism is strictly prohibited.

---

## 🚧 Project Status & Roadmap

### Current Status: Under Active Development

#### ✅ Completed Features (85%)
- [x] Backend API architecture
- [x] Database schema design
- [x] GPS data processing
- [x] Highway detection algorithm
- [x] Distance calculation (Haversine)
- [x] Toll calculation logic
- [x] Anomaly detection system
- [x] IoT simulator module
- [x] Frontend UI pages
- [x] Highway usage tracking
- [x] Smart request system (Vehicle & Profile lifecycle)
- [x] Real-time notification polling & bell UI
- [x] Interactive map with React-Leaflet route visualization

#### 🚧 In Progress (10%)
- [ ] Automated monthly billing
- [ ] Email notification system
- [ ] Admin dashboard enhancements
- [ ] Payment gateway integration
- [ ] User authentication & authorization

#### 📋 Planned Features (5%)
- [ ] Mobile application
- [ ] Real-time notifications (WebSocket upgrade)
- [ ] Advanced analytics
- [ ] Google Maps integration
- [ ] Multi-language support

---

## 📞 Support & Contact

For queries, suggestions, or technical support related to this project:

**Primary Contact:**  
📧 **Email:** albertcyse@gmail.com

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

### Project Documentation (Available in `/brain` folder)
- Highway GPS Tracking Walkthrough
- Anomaly Detection System Guide
- Data Storage Architecture
- IoT Simulator Manual
- Toll Calculation Logic
- API Integration Guide
- Smart Request System Guide *(NEW)*
- Notification System Architecture *(NEW)*

### External Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Official Docs](https://react.dev/learn)
- [MySQL Reference Manual](https://dev.mysql.com/doc/)
- [Haversine Formula](https://en.wikipedia.org/wiki/Haversine_formula)
- [React-Leaflet Documentation](https://react-leaflet.js.org/)

---

## ⭐ Star This Project!

If you find this project helpful for learning or as a reference for your own academic projects, please consider:
- ⭐ Starring the repository
- 🔄 Sharing with fellow students
- 💬 Providing feedback
- 🐛 Reporting issues

---

**Made with ❤️ for MCA Final Year Project**  
**SRM Institute of Science and Technology - Trichy**

---

**Last Updated:** March 30, 2026  
**Version:** 2.1.0-SNAPSHOT  
**Build Status:** 🚧 Under Development  
**Completion:** ~85%

---

**README.md - Comprehensive Project Documentation**  
**Total Lines:** 750+  
**Maintained By:** Albert J