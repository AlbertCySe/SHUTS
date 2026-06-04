# 🚦 Smart Highway Usage-Based Tolling System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2-blue.svg)](https://reactjs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Node.js](https://img.shields.io/badge/Node.js-18+-green.svg)](https://nodejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![H2](https://img.shields.io/badge/H2-2.2.224-darkblue.svg)](https://www.h2database.com/)
[![Status](https://img.shields.io/badge/Status-Active%20Development%20~95%25-brightgreen.svg)]()
[![License](https://img.shields.io/badge/License-Academic-blue.svg)](LICENSE)

---

## 📖 Quick Navigation

| Document | Description |
|----------|-------------|
| 📋 [Complete Setup Guide](docs/SETUP_GUIDE.md) | One-click and automated setup instructions |
| 🔧 [Manual Setup Guide](docs/MANUAL_SETUP.md) | Step-by-step manual installation for all platforms |
| 📁 [Project Structure](docs/PROJECT_STRUCTURE.md) | Every file and folder explained in detail |
| 📡 [API Reference](docs/API_REFERENCE.md) | All REST endpoints with payloads and responses |
| 🗄️ [Database Schema](docs/DATABASE_SCHEMA.md) | All tables, columns, types, and relationships |
| 🧪 [Testing Guide](docs/TESTING_GUIDE.md) | Complete test scenarios with expected results |
| 🐛 [Troubleshooting Guide](docs/TROUBLESHOOTING.md) | Known issues, errors, and their solutions |
| ✨ [Features Deep Dive](docs/FEATURES.md) | Detailed technical breakdown of every feature |

---

## 👨‍💻 About the Developer

| Field | Details |
|-------|---------|
| **Name** | Albert J |
| **Institution** | SRM Institute of Science and Technology — Trichy |
| **Program** | Master of Computer Applications (MCA) |
| **Academic Year** | 2024 – 2026 |
| **Project Type** | Final Year Project |
| **Email** | [albertcyse@gmail.com](mailto:albertcyse@gmail.com) |
| **Location** | Tamil Nadu, India |
| **Specialization** | Full Stack Development · IoT Systems |

**Project Guidance:** Under the supervision of MCA Department Faculty, SRM Institute of Science and Technology — Trichy Campus.

---

## 📌 Project Overview

The **Smart Highway Usage-Based Tolling System** is an innovative full-stack web application that completely reimagines how highway toll collection works. Instead of charging a flat fee at a physical toll booth, this system tracks the exact distance each vehicle travels on a highway using GPS coordinates and charges only for what was actually used — fairly, automatically, and transparently.

> **Think of it like an electricity meter for the highway.** You pay for exactly what you consume, not a flat rate regardless of how short or long your trip was.

The entire system — from vehicle registration to monthly billing — is managed digitally, with no physical toll booths, no cash transactions, and no queues. It is built on a modern full-stack architecture using Java (Spring Boot) on the backend, React on the frontend, MySQL as the primary database, and a decoupled IoT Simulation Engine that behaves like real GPS tracking hardware.

**Project Status:** 🟢 Active Development — **~95% Complete**

> 🚀 **Quick Start:** Run `start-project.bat` for one-click automated setup and launch.
> 🔒 **Secure Setup:** All credentials are stored in a `.env` file — no passwords are ever hardcoded in source code.

---

## 🎯 Problem Statement

### Issues with Traditional Toll Systems

| Problem | Real-World Impact |
|--------|-------------------|
| ❌ **Fixed Flat Charges** | Everyone pays the same rate, regardless of whether they drove 2 km or 200 km |
| ❌ **Long Queues at Booths** | Physical toll booths create traffic bottlenecks, congestion, and wasted fuel |
| ❌ **Cash-Only Transactions** | Leads to corruption, delays, loss of records, and lack of audit trail |
| ❌ **No Billing Transparency** | Users receive no clear breakdown of what they are paying or why |
| ❌ **Unfair Pricing Model** | Short-distance travelers effectively subsidize long-distance travelers |
| ❌ **Environmental Pollution** | Vehicles idling at toll booths produce unnecessary emissions at scale |
| ❌ **No Fraud Detection** | No mechanism to detect abnormal vehicle behavior or ghost registrations |
| ❌ **No Vehicle Lifecycle Oversight** | No formal system for handling sold, scrapped, or deactivated vehicles |

### Our Smart Solution

| Solution | What It Does |
|---------|--------------|
| ✅ **GPS-Based Distance Tracking** | Charges users only for the exact distance they travel on the highway |
| ✅ **Automated Cashless Billing** | No toll booths, no cash — deductions happen from a digital wallet in real time |
| ✅ **Digital Wallet Integration** | Each user has a wallet; toll is deducted automatically as the vehicle moves |
| ✅ **Monthly Consolidated Billing** | Full monthly records with a complete audit trail |
| ✅ **Dual-Mode IoT Simulation** | GPS data fed continuously from a simulation engine behaving like real hardware |
| ✅ **Admin Dashboard & Controls** | Administrators monitor, approve, and manage everything from one place |
| ✅ **Automated Fraud Detection** | Anomaly engine detects suspicious patterns and pushes alerts instantly |
| ✅ **Fair Distance-Based Pricing** | Vehicle type × distance × rate per km = exactly what you owe |
| ✅ **Secure Admin Verification** | All critical vehicle and profile changes require explicit Admin approval |
| ✅ **Smart Request & Approval Workflow** | Structured approval queue for all vehicle lifecycle and profile changes |
| ✅ **Live Notification Bell** | Real-time alerts via top-nav bell — polls every 30 seconds |
| ✅ **Optimized Data Retrieval** | Paging and filtering for thousands of vehicle records without lag |
| ✅ **Decoupled Simulator with H2 DB** | Standalone IoT Simulator syncs vehicle metadata from core DB on boot |
| ✅ **Dynamic Route Visualization** | Custom SVG/CSS3 GPS grid mapping — no external map library needed |

---

## 🧠 How The System Works

```
┌─────────────────────────────────────────────────────────────────┐
│  STEP 1: Smart Registration Request + Vehicle Sync              │
│  User requests → Admin Approves → Wallet Seeded                │
│  IoT Simulator auto-syncs vehicle metadata on boot             │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│  STEP 2: GPS Data Collection (Decoupled IoT Simulation)         │
│  Standalone Simulator (Port 8082, H2 DB)                       │
│  Broadcasts live GPS payloads to main backend                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│  STEP 3: Hybrid Highway Detection                               │
│  IoT Grid Detection — coordinate bounding box check            │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│  STEP 4: Precision Distance Calculation                         │
│  Haversine Formula — geodetic distance accounting for          │
│  Earth's curvature (more accurate than Euclidean)              │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│  STEP 5: Dynamic Toll Calculation                               │
│  Vehicle Type × Distance × Rate/km → Total Toll Amount          │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│  STEP 6: Automated Wallet Deduction                             │
│  Toll instantly deducted → Balance updated → Transaction logged │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│  STEP 7: Consolidated Billing                                   │
│  Monthly scheduler generates audit trail → Dashboard updated    │
└─────────────────────────────────────────────────────────────────┘
```

### Plain English: Step-by-Step

**Step 1 — Smart Registration + Vehicle Sync:**
A user submits a vehicle registration request. It enters the Admin approval queue — it does not take effect immediately. Once the Admin approves it, the vehicle is registered and a digital wallet is auto-created. When the Standalone IoT Simulator next boots, it queries the main backend and syncs all registered vehicles (Number, Type, Owner Name) into its own embedded H2 database automatically.

**Step 2 — Decoupled GPS Data Collection:**
The Standalone Simulator runs as a completely separate Spring Boot application on Port 8082, with its own H2 database. It manages vehicle movement states and broadcasts live GPS coordinate payloads to the main backend — behaving exactly like real physical GPS tracking hardware would in a production environment.

**Step 3 — Highway Detection:**
Every incoming GPS ping is evaluated by the IoT Grid Detection algorithm. It checks if the vehicle's current coordinates fall within the bounding box of any registered highway. If yes, a highway session begins. If the vehicle exits, the session closes.

**Step 4 — Haversine Distance Calculation:**
For consecutive GPS points while on a highway, the Haversine Formula computes the geodetic distance — the actual shortest path on the surface of the Earth. This accounts for Earth's curvature, making it far more accurate than flat Euclidean distance, especially over longer distances.

> 💡 **Viva Tip:** *"The Haversine Formula treats the Earth as a sphere and computes the great-circle distance between two GPS coordinates. This gives us geodetically accurate distance — significantly better than Euclidean approximations for real-world applications."*

**Step 5 — Toll Calculation:**
Toll = Distance × Rate per km for the vehicle's type. Rates are stored per highway — so different highways charge different rates. CAR, BIKE, BUS, and TRUCK each have separate rates. The calculation happens automatically inside the GPS pipeline.

**Step 6 — Wallet Deduction:**
The calculated toll is immediately deducted from the owner's digital wallet. The balance is updated in the same database transaction and every deduction is logged for auditing.

**Step 7 — Monthly Billing:**
At the end of each month, a Spring Scheduler automatically aggregates all highway usage records per user into a consolidated monthly bill, stores it in the `bills` table, and makes it visible on the user's billing dashboard.

---

## 🛠️ Technology Stack

### Backend

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Language** | Java | 17 LTS | Core backend language — stable, enterprise-grade |
| **Framework** | Spring Boot | 3.2.1 | REST API, scheduling, validation, auto-configuration |
| **ORM** | Spring Data JPA | 3.2.1 | Maps Java objects to database tables — no raw SQL |
| **Main Database** | MySQL | 8.0+ | Persistent storage for all platform data |
| **Simulator Database** | H2 | 2.2.224 | Embedded fast-storage inside the Standalone IoT Simulator |
| **Build Tool** | Maven | 3.6+ | Dependency management and packaging |
| **Validation** | Spring Validation | 3.2.1 | Validates GPS coordinates and inputs before processing |
| **Scheduling** | Spring Scheduler | 3.2.1 | Runs automated background tasks (monthly billing) |

### Frontend

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Library** | React | 18.2 | Builds all interactive web dashboards |
| **Build Tool** | Vite | 5.0 | Fast build and instant hot module reload |
| **Routing** | React Router | 6.x | Navigates between pages without full browser reload |
| **HTTP Client** | Axios | 1.6 | Communicates with the Spring Boot backend REST API |
| **Visualization** | Custom SVG/CSS3 | — | GPS route map — no external map services required |
| **Styling** | Vanilla CSS3 (Modular) | — | Per-component stylesheets |

### Additional Modules

| Module | Technology | Purpose |
|--------|-----------|---------|
| **Integrated IoT Engine** | Spring Boot (core) | Simulation bundled with main backend — no extra setup |
| **Standalone IoT Simulator** | Spring Boot + H2 | Fully decoupled GPS simulator on Port 8082 |
| **Fraud Detection Engine** | Custom Java algorithms | Detects missing GPS, disconnections, suspicious patterns |
| **Notification Engine** | Spring Boot + HTTP Polling | 30-second polling delivers alerts to frontend |
| **Request Workflow Engine** | Spring Boot | Admin approval queue — 5 vehicle lifecycle operations |
| **Highway State Processor** | Spring Boot Service | Manages vehicle session entry/exit for accurate billing |

---

## 📁 Project Structure

```
smart-highway-tolling-system/
│
├── Initial/                           ← Main Application Root
│   ├── iot-simulator/                 ← Standalone IoT Simulator (Port 8082)
│   │   ├── src/main/java/             ← Simulator Spring Boot backend
│   │   ├── frontend/                  ← GPS Telemetry Dashboard (HTML5/JS)
│   │   ├── run-simulator.bat          ← One-click launcher for simulator
│   │   └── pom.xml                    ← Standalone Maven build config
│   │
│   ├── src/main/java/com/highway/tolling/
│   │   ├── controller/                ← HTTP endpoint handlers
│   │   ├── model/                     ← JPA database entity classes
│   │   ├── repository/                ← Data access layer (Spring Data)
│   │   ├── service/                   ← Business logic layer
│   │   └── resources/                 ← application.properties + scripts
│   │
│   ├── frontend/                      ← React Dashboards
│   │   └── src/
│   │       ├── pages/                 ← Full page views
│   │       ├── components/            ← Reusable UI components
│   │       ├── hooks/                 ← Custom React hooks
│   │       ├── styles/                ← Modular CSS3
│   │       └── services/api.js        ← API base URL config
│   │
│   ├── docs/                          ← Detailed documentation files
│   ├── .env                           ← MySQL credentials (never committed)
│   ├── .env.example                   ← Credential template
│   ├── start-project.bat              ← One-click system launcher ⭐
│   ├── fix-maven.bat                  ← Auto Maven fixer
│   ├── install-maven-offline.bat      ← Offline Maven installer
│   └── install-nodejs.bat             ← Auto Node.js installer
```

> 📄 **For a complete file-by-file breakdown of every `.java`, `.jsx`, `.css`, and config file:**
> **[📁 docs/PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md)**

---

## 🗄️ Database Schema

The system uses **two databases**: MySQL for the main platform, H2 (embedded) for the Standalone Simulator.

| # | Table | What It Stores |
|---|-------|----------------|
| 1 | `users` | User accounts — name, email, phone |
| 2 | `vehicles` | Registered vehicles — type, plate, owner |
| 3 | `wallets` | Digital wallet balance per user |
| 4 | `highways` | GPS boundaries + 4-tier pricing rates |
| 5 | `location_tracking` | Raw GPS pings from the IoT engine |
| 6 | `highway_usage` | Entry/exit sessions per trip |
| 7 | `bills` | Monthly consolidated toll bills |
| 8 | `data_anomalies` | Fraud flags — auto-detected, auto-notified |
| 9 | `vehicle_requests` | Admin queue: ADD, SELL, SCRAP, DEACTIVATE, MODIFY |
| 10 | `profile_requests` | Admin queue for profile change approvals |
| 11 | `user_notifications` | Real-time alerts for admin decisions and anomalies |

> 📄 **Complete schema with column types, constraints, relationships, and the H2 simulator schema:**
> **[🗄️ docs/DATABASE_SCHEMA.md](docs/DATABASE_SCHEMA.md)**

---

## 🚀 Quick Start

> ⚡ **The fastest way: one double-click.**

```
1. Run install-nodejs.bat    ← First time only (if Node.js missing)
2. Run fix-maven.bat         ← First time only (if Maven missing)
3. Run start-project.bat     ← LAUNCHES EVERYTHING ⭐
```

`start-project.bat` automatically verifies prerequisites, loads credentials, starts the backend (blue terminal), starts the frontend (yellow terminal), and opens http://localhost:3000 in your browser.

> 📋 **Full automated setup guide:** [docs/SETUP_GUIDE.md](docs/SETUP_GUIDE.md)
> 🔧 **Manual setup (non-Windows or advanced):** [docs/MANUAL_SETUP.md](docs/MANUAL_SETUP.md)

---

## 🪜 Step-by-Step First-Time Setup

### Step 1 — Get the Project

```bash
git clone https://github.com/albertcyse/smart-highway-tolling-system.git
# OR download and extract the ZIP. Navigate into the 'Initial' folder.
```

### Step 2 — Configure Your Database Credentials

```bash
# Automatic — the launcher asks on first run:
start-project.bat
# Enter MySQL Username: root
# Enter MySQL Password: your_password

# OR manual — copy the template:
copy .env.example .env
# Open .env and fill in DB_USERNAME and DB_PASSWORD
```

> ⚠️ `.env` is in `.gitignore` — your password will never be committed.
> ℹ️ The database `tolling_system` is created automatically on first run.

### Step 3 — Launch

```bash
# Windows (recommended):
start-project.bat

# Manual backend:
cd Initial && mvn spring-boot:run

# Manual frontend (new terminal):
cd Initial/frontend && npm run dev
```

### Step 4 — Optional: Start the Standalone IoT Simulator

```bash
cd Initial/iot-simulator
run-simulator.bat
# Telemetry dashboard opens at http://localhost:8082
```

### Step 5 — First Use

1. Open http://localhost:3000
2. Create a User account
3. Submit a Vehicle Registration Request
4. In Admin view — approve the request (wallet auto-created)
5. GPS simulation begins — toll deducts in real time

> 📋 If any step fails → **[docs/SETUP_GUIDE.md](docs/SETUP_GUIDE.md)**
> 🔧 Non-Windows or advanced setup → **[docs/MANUAL_SETUP.md](docs/MANUAL_SETUP.md)**

---

## 🌐 Application URLs

| Service | URL | Notes |
|---------|-----|-------|
| **Frontend (React)** | http://localhost:3000 | Main user and admin web interface |
| **Backend REST API** | http://localhost:8080 | All core API endpoints |
| **Standalone IoT Simulator** | http://localhost:8082 | Decoupled simulator + telemetry dashboard |
| **Main Database (MySQL)** | localhost:3306 | Internal — accessed by backend only |

---

## 📡 API Reference (Summary)

| API Group | Prefix | Description |
|-----------|--------|-------------|
| Users | `/api/users` | Create and retrieve user accounts |
| Vehicles | `/api/vehicles` | Paginated vehicle listings |
| Highways | `/api/highways` | Register highways with 4-tier pricing |
| IoT / GPS | `/api/iot/data` | Ingest GPS telemetry |
| Simulator | `/api/simulation/vehicles` *(8082)* | Live vehicle status from simulator |
| Vehicle Requests | `/api/vehicle-requests` | Lifecycle approval workflow |
| Profile Requests | `/api/profile-requests` | Profile change approval workflow |
| Notifications | `/api/notifications` | Fetch and mark user alerts |
| Highway Usage | `/api/highway-usage` | Distance summaries and journey history |
| Admin / Billing | `/api/admin` | Wallet seeding and admin ops |

> 📡 **Full API reference with complete payloads, response bodies, and error codes:**
> **[📡 docs/API_REFERENCE.md](docs/API_REFERENCE.md)**

---

## ✨ Key Features

| # | Feature | Status |
|---|---------|--------|
| 1 | Dual-Mode IoT Simulation (Integrated + Standalone with H2 DB) | ✅ Complete |
| 2 | Haversine Geodetic Distance Calculation | ✅ Complete |
| 3 | IoT Grid Highway Boundary Detection | ✅ Complete |
| 4 | Automated Toll Calculation Pipeline | ✅ Complete |
| 5 | Smart Request & Admin Approval System (5 operations) | ✅ Complete |
| 6 | Proactive Fraud & Anomaly Detection | ✅ Complete |
| 7 | Real-Time Notification Bell (30-second polling) | ✅ Complete |
| 8 | Consolidated Monthly Billing (Spring Scheduler) | ✅ Complete |
| 9 | Dynamic Route Visualization (Custom SVG/CSS3) | ✅ Complete |
| 10 | Optimized Paging & Filtering | ✅ Complete |
| 11 | Payment Gateway Mock UI (wallet top-up) | 🚧 In Progress |
| 12 | PDF Export for Monthly Bills | 🚧 In Progress |

> ✨ **Technical deep dive into every feature's implementation:**
> **[✨ docs/FEATURES.md](docs/FEATURES.md)**

---

## 🧪 Testing

| # | Scenario | Tests |
|---|----------|-------|
| 1 | Registration Flow | Admin approval, wallet auto-seeding |
| 2 | IoT Simulation Flow | Simulator vehicle sync, telemetry |
| 3 | Tolling & Billing Flow | Wallet deduction, bill generation |
| 4 | Fraud Detection Flow | Anomaly detection, notification push |
| 5 | Smart Request Workflow | Request queue, admin notes delivery |
| 6 | Profile Update Request | Approval and notification |

> 🧪 **Full scenarios with pre-conditions, steps, and expected results:**
> **[🧪 docs/TESTING_GUIDE.md](docs/TESTING_GUIDE.md)**

---

## 🐛 Troubleshooting (Quick Reference)

| Symptom | Fix |
|---------|-----|
| `mvn: command not found` | Run `fix-maven.bat` |
| MySQL connection refused | Start MySQL service; check `.env` credentials |
| Port 8080 in use | `netstat -ano \| findstr :8080` then kill the PID |
| `npm install` fails | `npm cache clean --force` → delete `node_modules/` → retry |
| CORS error in browser | Verify backend on 8080; check `vite.config.js` proxy |
| Map/visualization blank | Check browser console; verify data in `location_tracking` |
| Notification bell stuck | Correct path is `/api/notifications` (not `/api/notifications`) |
| Simulator shows no vehicles | Approve at least one vehicle in main project first |

> 🐛 **Complete troubleshooting with exact error messages and step-by-step fixes:**
> **[🐛 docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)**

---

## ✨ Key Technical Highlights — Viva Reference

| Feature | Implementation | What to Say at Viva |
|---------|---------------|---------------------|
| **Distance Calculation** | Haversine Formula | "Geodetically accurate — accounts for Earth's curvature" |
| **Highway Detection** | IoT Grid bounding box | "Scalable coordinate check — works for any number of highways" |
| **Toll Calculation** | Rate × Haversine distance | "Fully automated inside the GPS pipeline — zero manual steps" |
| **Fraud Detection** | Rule-based anomaly engine | "Proactive — pushes alerts automatically, no manual review" |
| **Admin Approval** | Request queue + state machine | "Prevents unauthorized data changes at the architecture level" |
| **Billing** | Spring Scheduler + BillService | "Fully automated monthly audit trail" |
| **Notifications** | HTTP polling every 30s | "Pragmatic real-time — simpler and reliable vs WebSockets at this scale" |
| **Route Visualization** | Custom SVG/CSS3 | "Built from scratch — no paid APIs, demonstrates custom rendering" |
| **Standalone Simulator** | Spring Boot + H2 | "Mirrors real hardware decoupling — syncs metadata, acts like GPS devices" |
| **Data Governance** | All changes via request queue | "No user can tamper with data unilaterally" |

---

## 🗺️ Project Status & Roadmap

**Status:** 🟢 Active Development — ~95% Complete

### ✅ Completed
- [x] Full backend REST API (controllers, services, repositories)
- [x] MySQL schema — 11 tables, fully normalized
- [x] GPS ingestion and processing pipeline
- [x] IoT Grid highway boundary detection
- [x] Haversine formula distance calculation
- [x] Automated toll calculation (embedded in pipeline)
- [x] Rule-based fraud and anomaly detection
- [x] Dual-Mode IoT Simulation — integrated + standalone with H2 DB
- [x] Complete frontend (Admin + User dashboards)
- [x] Highway usage session tracking
- [x] Smart Request System — 5 lifecycle operations
- [x] Real-time notification polling + notification bell UI
- [x] Dynamic route visualization (custom SVG/CSS3)
- [x] Modular CSS3 architecture
- [x] Paging and filtering for large datasets
- [x] Admin approval workflow with notes + notification delivery
- [x] Strict data governance — all changes require admin approval

### 🚧 In Progress
- [ ] Payment gateway mock UI — wallet top-up
- [ ] PDF export for monthly bills
- [ ] Code cleanup for academic submission

### 📋 Future Enhancements
- [ ] JWT role-based authentication and authorization
- [ ] Email/SMTP notification delivery
- [ ] WebSocket real-time notifications
- [ ] Mobile application (React Native or PWA)
- [ ] Advanced analytics and charting
- [ ] Google Maps / OpenStreetMap integration

---

## 🙏 Acknowledgments

- **SRM Institute of Science and Technology — Trichy** — excellent academic environment and resources
- **MCA Department Faculty** — guidance and mentorship throughout the project
- **Spring Framework Team** — robust, enterprise-grade Spring Boot framework
- **React Team** — powerful and flexible UI library
- **MySQL & H2 Communities** — reliable open-source database engines
- **Stack Overflow Community** — countless community solutions and best practices
- **Family & Friends** — continuous support and encouragement

---

## 📄 License & Usage

**License:** Academic Use Only

Developed as part of the MCA Final Year Project at SRM Institute of Science and Technology — Trichy. Intended for academic evaluation, educational reference, portfolio demonstration, and research inspiration.

> **Copyright © 2026 Albert J. All rights reserved.**
> Unauthorized commercial use, redistribution, or plagiarism is strictly prohibited.

---

## 📚 Documentation Index

| Document | Contents |
|----------|---------|
| [📋 SETUP_GUIDE.md](docs/SETUP_GUIDE.md) | Automated setup, helper scripts, first-time configuration |
| [🔧 MANUAL_SETUP.md](docs/MANUAL_SETUP.md) | Manual setup for all platforms, advanced configuration |
| [📁 PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md) | Every file and folder: role, dependencies, data flow |
| [📡 API_REFERENCE.md](docs/API_REFERENCE.md) | All REST endpoints, full payloads, response bodies, error codes |
| [🗄️ DATABASE_SCHEMA.md](docs/DATABASE_SCHEMA.md) | All table schemas with column types, constraints, relationships |
| [🧪 TESTING_GUIDE.md](docs/TESTING_GUIDE.md) | Scenario-based testing with pre-conditions and expected outcomes |
| [🐛 TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md) | Common errors, root causes, and step-by-step solutions |
| [✨ FEATURES.md](docs/FEATURES.md) | Technical deep dive into every major feature |

### External References

| Resource | URL |
|---------|-----|
| Spring Boot Docs | https://spring.io/projects/spring-boot |
| React Docs | https://react.dev/learn |
| MySQL Docs | https://dev.mysql.com/doc/ |
| H2 Database | https://www.h2database.com/html/main.html |
| Haversine Formula | https://en.wikipedia.org/wiki/Haversine_formula |
| Vite Docs | https://vitejs.dev/ |

---

**Made with ❤️ for MCA Final Year Project**
**SRM Institute of Science and Technology — Trichy**

*Last Updated: May 2026 · Version: 4.0.0 · Completion: ~95%*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
