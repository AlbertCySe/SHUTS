# 🚦 Smart Highway Usage-Based Tolling System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.0-blue.svg)](https://reactjs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Node.js](https://img.shields.io/badge/Node.js-18+-green.svg)](https://nodejs.org/)
[![License](https://img.shields.io/badge/License-Academic-blue.svg)](LICENSE)

## 📌 Project Overview

The **Smart Highway Usage-Based Tolling System** is a **full-stack web application** designed to revolutionize highway toll collection by calculating charges based on actual distance traveled rather than fixed toll booth payments. This system eliminates the need for physical toll plazas and provides a fair, transparent, and automated billing experience.

**Developed as:** MCA Final Year Project  
**Tech Stack:** Spring Boot (Backend) + React (Frontend) + MySQL (Database)  
**Type:** IoT-Simulated Web Application

---

## 🎯 Why This Project?

### Problems with Current Toll System
- ❌ Fixed charges regardless of distance
- ❌ Long queues at toll booths
- ❌ Cash handling and corruption
- ❌ No transparency in billing
- ❌ Unfair to short-distance travelers

### Our Solution
- ✅ **Distance-based** toll calculation using GPS
- ✅ **Automated billing**  without toll booths
- ✅ **Digital wallet** for seamless payments
- ✅ **Monthly consolidated** bills
- ✅ **Real-time tracking** (IoT simulated)
- ✅ **Admin dashboard** for monitoring
- ✅ **Email notifications** for bills

---

## 🧠 How It Works

```
1. Vehicle → GPS Data → Backend API
2. Backend → Detect if on Highway → Haversine Distance Calculation
3. Backend → Calculate Toll (Vehicle Type × Distance × Rate)
4. Backend → Deduct from Digital Wallet
5. Monthly Scheduler → Generate Bill → Email to User
6. Admin → Monitor Revenue & Deficit Wallets
```

---

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17 | Programming Language |
| **Spring Boot** | 3.2.1 | Application Framework |
| **Spring Data JPA** | 3.2.1 | Database ORM |
| **MySQL** | 8.0+ | Relational Database |
| **Maven** | 3.6+ | Build Tool |
| **Lombok** | 1.18.30 | Code Reduction |
| **Spring Mail** | 3.2.1 | Email Service |
| **Spring Scheduler** | 3.2.1 | Automated Jobs |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| **React** | 18.2 | UI Library |
| **Vite** | 5.0 | Build Tool |
| **React Router** | 6.x | Navigation |
| **Axios** | 1.6 | HTTP Client |
| **CSS3** | — | Styling |

---

## ⚙️ System Architecture

```
┌─────────────┐      ┌──────────────┐      ┌─────────────┐
│   React     │ HTTP │ Spring Boot  │ JDBC │   MySQL     │
│  Frontend   │◄────►│   Backend    │◄────►│  Database   │
│ (Port 3000) │      │ (Port 8080)  │      │ (Port 3306) │
└─────────────┘      └──────────────┘      └─────────────┘
```

**Flow:**
- Frontend sends HTTP requests to Backend REST APIs
- Backend processes business logic and database operations
- Backend returns JSON responses
- Frontend displays data in user-friendly UI

---

## 📦 Project Structure

```
smart-highway-tolling-system/
│
├── backend/ (Spring Boot)
│   ├── src/main/java/com/highway/tolling/
│   │   ├── controller/         # REST API Controllers (5)
│   │   ├── model/             # JPA Entities (8)
│   │   ├── repository/        # Database Repositories (6)
│   │   ├── service/           # Business Logic (11)
│   │   ├── scheduler/         # Automated Jobs (1)
│   │   └── TollingSystemApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── frontend/ (React + Vite)
│   ├── src/
│   │   ├── pages/            # React Pages (7)
│   │   ├── components/       # Reusable Components
│   │   ├── services/         # API Service Layer
│   │   ├── App.jsx           # Main App Component
│   │   └── main.jsx          # Entry Point
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── start-project.bat         # One-click Windows launcher
├── README.md                 # This file
└── .gitignore               # Git ignore rules
```

---

## 🗄️ Database Schema

### Tables (6)

1. **`users`**
   - `user_id` (PK), `name`, `email`, `phone_number`, `created_at`

2. **`vehicles`**
   - `vehicle_id` (PK), `vehicle_number`, `vehicle_type` (ENUM), `user_id` (FK), `registered_at`

3. **`wallets`**
   - `wallet_id` (PK), `balance`, `minimum_balance`, `user_id` (FK)

4. **`highways`**
   - `highway_id` (PK), `highway_name`, `start_latitude`, `start_longitude`, `end_latitude`, `end_longitude`, `rate_per_km_for_car/bike/bus/truck`

5. **`location_tracking`**
   - `id` (PK), `vehicle_id` (FK), `latitude`, `longitude`, `timestamp`

6. **`bills`**
   - `bill_id` (PK), `user_id` (FK), `bill_month`, `total_distance`, `total_amount`, `status` (ENUM), `due_date`, `created_at`

### Relationships
```
User (1) ←→ (Many) Vehicle
User (1) ←→ (1) Wallet
User (1) ←→ (Many) Bill
Vehicle (1) ←→ (Many) LocationTracking
```

---

## 🚀 Quick Start Guide

### Prerequisites

✅ **Java 17+** - [Download](https://adoptium.net/)  
✅ **Node.js 18+** - [Download](https://nodejs.org/) *(Installed ✓)*  
✅ **MySQL 8.0+** - [Download](https://dev.mysql.com/downloads/)  
⚠️ **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi) *(OR use IntelliJ IDEA)*

---

### Installation Steps

#### 1️⃣ Clone the Repository
```bash
git clone https://github.com/your-username/smart-highway-tolling-system.git
cd smart-highway-tolling-system
```

#### 2️⃣ Setup MySQL Database
```sql
CREATE DATABASE tolling_system;
```

#### 3️⃣ Configure Database Credentials

**Option A: Environment Variables** (Recommended)
```bash
# Windows (PowerShell)
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"

# Linux/Mac
export DB_USERNAME="root"
export DB_PASSWORD="your_password"
```

**Option B: Edit `application.properties`**
```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

#### 4️⃣ Install Frontend Dependencies
```bash
cd frontend
npm install
cd ..
```

---

### Running the Application

#### **Option 1: Using Batch File** (Windows - Easiest!)
```bash
# Double-click this file:
start-project.bat
```

**What it does:**
- ✅ Checks Node.js installation
- ✅ Checks Maven installation (or suggests IntelliJ)
- ✅ Installs npm dependencies if needed
- ✅ Starts backend (Spring Boot)
- ✅ Starts frontend (React)
- ✅ Opens browser automatically

---

#### **Option 2: Manual Start** (All Platforms)

**Terminal 1 - Backend:**
```bash
mvn spring-boot:run
```
Backend runs at: http://localhost:8080

**Terminal 2 - Frontend:**
```bash
cd frontend
npm run dev
```
Frontend runs at: http://localhost:3000

---

#### **Option 3: Using IntelliJ IDEA** (Recommended if no Maven)

1. Open project in IntelliJ IDEA
2. Right-click `TollingSystemApplication.java`
3. Select **"Run TollingSystemApplication"**
4. Backend starts automatically!

Then start frontend manually:
```bash
cd frontend
npm run dev
```

---

## 🌐 Accessing the Application

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:3000 | React Web UI |
| **Backend API** | http://localhost:8080 | Spring Boot REST API |
| **Database** | localhost:3306 | MySQL Server |

---

## 📡 Complete API Documentation

### 🔵 User Management (`/api/users`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/api/users` | Create new user | `{name, email, phoneNumber}` |
| GET | `/api/users` | Get all users | — |
| GET | `/api/users/{id}` | Get user by ID | — |
| POST | `/api/users/{userId}/vehicles` | Add vehicle to user | `{vehicleNumber, vehicleType}` |

### 🚗 Vehicle Management (`/api/vehicles`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/api/vehicles` | Register vehicle | `{vehicleNumber, vehicleType, userId}` |
| GET | `/api/vehicles` | Get all vehicles | — |
| GET | `/api/vehicles/{id}` | Get vehicle by ID | — |
| GET | `/api/vehicles/health` | Health check | — |

### 🛣️ Highway Management (`/api/highways`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/api/highways` | Create highway | `{highwayName, start/endLat/Lon, rates...}` |
| GET | `/api/highways` | Get all highways | — |
| GET | `/api/highways/{id}` | Get highway by ID | — |
| PUT | `/api/highways/{id}` | Update highway | `{...highway fields}` |
| DELETE | `/api/highways/{id}` | Delete highway | — |

### 📍 Location Tracking (`/api/locations`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/api/locations` | Save GPS location | `{vehicleId, latitude, longitude}` |
| GET | `/api/locations/vehicle/{vehicleId}` | Get vehicle location history | — |

### 👨‍💼 Admin Dashboard (`/api/admin`)

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/api/admin/vehicles` | All registered vehicles | `[{vehicleId, ...}]` |
| GET | `/api/admin/wallets/negative` | Wallets with negative balance | `[{walletId, balance, ...}]` |
| GET | `/api/admin/wallets/deficit` | Wallets in deficit | `[{walletId, ...}]` |
| GET | `/api/admin/toll/total` | Total toll collected | `totalAmount` |
| GET | `/api/admin/stats` | System statistics | `{totalVehicles, totalToll, ...}` |

---

## 🧪 Testing the APIs

### Using cURL

**1. Create a User:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","phoneNumber":"9876543210"}'
```

**2. Register a Vehicle:**
```bash
curl -X POST http://localhost:8080/api/users/1/vehicles \
  -H "Content-Type: application/json" \
  -d '{"vehicleNumber":"MH01AB1234","vehicleType":"CAR"}'
```

**3. Send GPS Location:**
```bash
curl -X POST http://localhost:8080/api/locations \
  -H "Content-Type: application/json" \
  -d '{"vehicleId":1,"latitude":19.0760,"longitude":72.8777}'
```

**4. Get Admin Stats:**
```bash
curl http://localhost:8080/api/admin/stats
```

### Using Postman
1. Download [Postman](https://www.postman.com/)
2. Create new collection
3. Set base URL: `http://localhost:8080`
4. Test all endpoints

---

## 🎨 Frontend Pages

| Page | Route | Description |
|------|-------|-------------|
| **Home** | `/` | Project overview and navigation |
| **Users** | `/users` | User registration and list |
| **Vehicles** | `/vehicles` | Vehicle registration and list |
| **Highways** | `/highways` | Highway management |
| **GPS Tracking** | `/locations` | Submit GPS data and view history |
| **Wallet & Bills** | `/wallet-bills` | View wallet balance and monthly bills |
| **Admin Dashboard** | `/admin` | System statistics and monitoring |

### Features per Page

**Users Page:**
- ✅ Create new user form
- ✅ Display all users in table
- ✅ Real-time validation
- ✅ Success/error messages

**Vehicles Page:**
- ✅ Register vehicle with dropdown (CAR/BIKE/BUS/TRUCK)
- ✅ Display all vehicles with badges
- ✅ Auto-refresh on create

**Highways Page:**
- ✅ Create highway with GPS coordinates
- ✅ Set toll rates for different vehicle types
- ✅ View all highways in compact table

**GPS Tracking Page:**
- ✅ Submit GPS location for any vehicle
- ✅ View location history by vehicle ID
- ✅ IoT simulation for testing

**Wallet & Bills Page:**
- ✅ View wallet balance by user
- ✅ Check deficit status
- ✅ View all monthly bills
- ✅ Color-coded bill status (PAID/PENDING/OVERDUE)

**Admin Dashboard:**
- ✅ System statistics cards
- ✅ Negative balance wallets table
- ✅ All vehicles overview
- ✅ Total toll collected

---

## 🧩 Core Algorithms

### 1. Haversine Formula (Distance Calculation)
```java
// Calculate distance between two GPS points
public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    double R = 6371; // Earth radius in kilometers
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    
    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLon/2) * Math.sin(dLon/2);
               
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c; // Distance in km
}
```

### 2. Highway Detection (Bounding Box)
```java
// Check if GPS point is within highway boundaries
public boolean isOnHighway(double lat, double lon, Highway highway) {
    double minLat = Math.min(highway.getStartLatitude(), highway.getEndLatitude()) - TOLERANCE;
    double maxLat = Math.max(highway.getStartLatitude(), highway.getEndLatitude()) + TOLERANCE;
    double minLon = Math.min(highway.getStartLongitude(), highway.getEndLongitude()) - TOLERANCE;
    double maxLon = Math.max(highway.getStartLongitude(), highway.getEndLongitude()) + TOLERANCE;
    
    return lat >= minLat && lat <= maxLat && lon >= minLon && lon <= maxLon;
}
```

### 3. Toll Calculation
```java
// Calculate toll based on vehicle type and distance
public double calculateToll(VehicleType type, double distance, Highway highway) {
    double rate = switch(type) {
        case CAR -> highway.getRatePerKmForCar();
        case BIKE -> highway.getRatePerKmForBike();
        case BUS -> highway.getRatePerKmForBus();
        case TRUCK -> highway.getRatePerKmForTruck();
    };
    return distance * rate;
}
```

---

## 🔐 Security & Best Practices

### Implemented
- ✅ Environment variables for credentials
- ✅ .gitignore to prevent credential commits
- ✅ Input validation on all forms
- ✅ Error handling with try-catch
- ✅ Clean code with Lombok
- ✅ RESTful API design

### Not Implemented (Out of Scope for MCA)
- ❌ JWT Authentication (Use Spring Security if needed)
- ❌ Payment Gateway Integration
- ❌ Real Hardware IoT Devices
- ❌ Google Maps Integration
- ❌ Production Deployment

---

## 🐛 Troubleshooting Guide

### Backend Issues

**Problem: `mvn: command not found`**
```
Solution:
1. Use IntelliJ IDEA (Maven built-in)
2. OR Download Maven: https://maven.apache.org/download.cgi
3. Add to PATH and restart
```

**Problem: MySQL connection failed**
```
Solution:
1. Verify MySQL is running: systemctl status mysql (Linux)
2. Check database exists: SHOW DATABASES;
3. Verify credentials in application.properties
```

**Problem: Port 8080 already in use**
```
Solution:
- Change port in application.properties:
  server.port=8081
```

### Frontend Issues

**Problem: `npm: command not found`**
```
Solution:
- Install Node.js: https://nodejs.org/
- Restart computer
```

**Problem: `npm run dev` fails**
```
Solution:
1. Delete node_modules and package-lock.json
2. Run: npm install
3. Run: npm run dev
```

**Problem: Port 3000 already in use**
```
Solution:
- Vite will automatically try port 3001
- Check terminal for actual port
```

**Problem: API calls fail (CORS error)**
```
Solution:
- Ensure backend is running on port 8080
- Check vite.config.js proxy settings
```

---

## 📚 Learning Resources

### Spring Boot
- [Official Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Guide](https://spring.io/guides/gs/accessing-data-jpa/)

### React
- [Official Tutorial](https://react.dev/learn)
- [React Router Docs](https://reactrouter.com/)

### GPS & Algorithms
- [Haversine Formula Explained](https://en.wikipedia.org/wiki/Haversine_formula)
- [Geospatial Calculations](https://www.movable-type.co.uk/scripts/latlong.html)

---

## 🎓 Academic Context

### MCA Final Year Project Requirements Met
- ✅ Full-stack application (Backend + Frontend + Database)
- ✅ RESTful API architecture
- ✅ Database design with relationships
- ✅ Business logic implementation
- ✅ Real-world problem solving
- ✅ Clean, documented code
- ✅ Professional README and documentation

### Project Presentation Tips
1. **Demo Flow:** User Registration → Vehicle Registration → GPS Tracking → View Bills
2. **Highlight:** Automated billing scheduler
3. **Show:** Admin dashboard with statistics
4. **Explain:** Haversine formula and highway detection algorithm
5. **Discuss:** Scalability and future enhancements

---

## 🚧 Future Enhancements (Post-MCA)

- 🔐 Add JWT-based authentication and authorization
- 💳 Integrate payment gateway (Razorpay/PayPal)
- 📱 Mobile app for vehicle owners
- 🗺️ Google Maps integration for route visualization
- 📊 Advanced analytics dashboard
- 🔔 Real-time push notifications
- 🌐 Multi-language support
- ☁️ Cloud deployment (AWS/Azure)
- 🤖 AI-based traffic prediction
- 🔗 Blockchain for transparent billing

---

## 👤 Author

**Albert J**  
<<<<<<< HEAD
MCA Student  
📧 Email: [albertcyse@gmail.com](mailto:albertcyse@gmail.com)  
🌍 Location: TamilNadu, India
=======
📧 Email: your-email@example.com  
🎓 Program: MCA (Master of Computer Applications)  
🌍 Location: India  
📅 Year: 2025-2026

---

## 🙏 Acknowledgments

- **Spring Framework Team** - For the amazing Spring Boot framework
- **React Team** - For the powerful React library
- **MySQL Community** - For the reliable database system
- **Stack Overflow Community** - For endless solutions
- **MCA Faculty** - For guidance and support
- **Family & Friends** - For constant encouragement
>>>>>>> 838bcab (Update frontend, and Modified README, gitignore for frontend and backend setup)

---

## 📄 License

This project is intended for **academic use only** as part of MCA Final Year Project submission.

**© 2026 Albert J. All rights reserved.**

---

## 📞 Support & Contact

For any queries or issues related to this project:
- 📧 Email: your-email@example.com
- 💼 LinkedIn: [Your Profile](https://linkedin.com/in/your-profile)
- 🐙 GitHub: [Your GitHub](https://github.com/your-username)

---

## ⭐ Star This Project!

If you find this project helpful for your learning, please give it a star! ⭐

**Made with ❤️ for MCA Final Year Project**

---

**Last Updated:** January 2026  
**Version:** 1.0.0  
**Status:** ✅ Complete & Ready for Submission
