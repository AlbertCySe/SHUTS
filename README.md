# 🚦 Smart Highway Usage-Based Tolling System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Academic-blue.svg)](LICENSE)

## 📌 Project Overview

The **Smart Highway Usage-Based Tolling System** is a backend-driven application designed to calculate highway toll charges based on the actual distance traveled by a vehicle on national highways, rather than fixed toll booth charges.

This project is developed as part of an **MCA Final Year Project** and focuses on improving **fairness**, **transparency**, and **efficiency** in toll collection using simulated IoT GPS data, automated billing, and digital wallet management.

---

## 🎯 Key Objectives

- ✅ Eliminate dependency on physical toll booths
- ✅ Enable distance-based toll calculation
- ✅ Reduce human intervention and corruption
- ✅ Provide transparent and automated billing
- ✅ Support monthly consolidated toll bills
- ✅ Simulate IoT-based vehicle location tracking
- ✅ Real-time wallet management with deficit alerts

---

## 🧠 How the System Works

1. **Vehicles send GPS location data** to the backend (simulated IoT).
2. **The system detects** whether the vehicle is traveling on a highway using geo-fencing.
3. **Distance traveled** on highways is calculated using the Haversine formula.
4. **Toll is computed** based on vehicle type (CAR, BIKE, BUS, TRUCK) and distance.
5. **The toll amount is deducted** from the user's digital wallet (supports negative balance).
6. **Monthly bills are generated** automatically using Spring Scheduler.
7. **Admin users can monitor** system usage, revenue, and user wallets.

---

## 🧩 Core Modules

| Module | Description |
|--------|-------------|
| **User Management** | Register users with email, phone, and profile details |
| **Vehicle Registration** | Register vehicles linked to users with vehicle types |
| **Highway Configuration** | Define highways with GPS coordinates and toll rates |
| **GPS Location Tracking** | IoT simulation for vehicle location data |
| **Highway Detection** | Bounding box geo-fencing to detect highway usage |
| **Distance Calculation** | Haversine formula for accurate distance computation |
| **Toll Calculation Engine** | Calculate toll based on vehicle type and distance |
| **Digital Wallet Management** | Wallet with balance, recharge, and deficit tracking |
| **Monthly Billing Scheduler** | Automated bill generation on 1st of every month |
| **Admin Dashboard** | Monitor vehicles, wallets, and total revenue |
| **Email Notifications** | Send bill alerts and payment reminders |

---

## 🛠️ Technologies Used

- **Java 17** - Programming language
- **Spring Boot 3.2.1** - Application framework
- **Spring Data JPA** - Database ORM
- **MySQL** - Relational database
- **Spring Scheduler** - Automated monthly billing
- **Spring Mail** - Email notifications
- **Maven** - Dependency management
- **Lombok** - Code reduction
- **RESTful APIs** - HTTP communication

---

## 🗄️ Database Schema

### Tables Created

1. **users** - User account information
2. **vehicles** - Registered vehicles with user relationship
3. **wallets** - Digital wallet for each user
4. **highways** - Highway definitions with toll rates
5. **location_tracking** - GPS location data from vehicles
6. **bills** - Monthly toll bills

### Entity Relationships

```
User (1) ←→ (Many) Vehicle
User (1) ←→ (1) Wallet
User (1) ←→ (Many) Bill
```

---

## 🚀 How to Run the Project

### Prerequisites

- **Java 17** or higher ([Download](https://adoptium.net/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **MySQL 8.0+** ([Download](https://dev.mysql.com/downloads/))

### Installation Steps

#### 1. Clone the Repository
```bash
git clone https://github.com/your-username/smart-highway-tolling-system.git
cd smart-highway-tolling-system
```

#### 2. Setup Database
```sql
CREATE DATABASE tolling_system;
```

#### 3. Configure Database Credentials

**Option A: Using Environment Variables** (Recommended)
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

#### 4. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

#### 5. Verify Application

The application runs at: **http://localhost:8080**

Test health endpoint:
```bash
curl http://localhost:8080/api/vehicles/health
```

---

## 📡 API Endpoints

### User Management
- `POST /api/users` - Create user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users/{userId}/vehicles` - Add vehicle to user

### Vehicle Management
- `POST /api/vehicles` - Register vehicle
- `GET /api/vehicles` - Get all vehicles
- `GET /api/vehicles/{id}` - Get vehicle by ID

### Highway Management
- `POST /api/highways` - Create highway
- `GET /api/highways` - Get all highways
- `GET /api/highways/{id}` - Get highway by ID

### Location Tracking
- `POST /api/locations` - Save GPS location
- `GET /api/locations/vehicle/{vehicleId}` - Get vehicle locations

### Admin Dashboard
- `GET /api/admin/vehicles` - View all vehicles
- `GET /api/admin/wallets/negative` - View deficit wallets
- `GET /api/admin/toll/total` - Get total toll collected
- `GET /api/admin/stats` - System statistics

---

## 🧪 API Testing

### Using cURL

**Create a User:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","phoneNumber":"9876543210"}'
```

**Add a Vehicle:**
```bash
curl -X POST http://localhost:8080/api/users/1/vehicles \
  -H "Content-Type: application/json" \
  -d '{"vehicleNumber":"MH01AB1234","vehicleType":"CAR"}'
```

**Save GPS Location:**
```bash
curl -X POST http://localhost:8080/api/locations \
  -H "Content-Type: application/json" \
  -d '{"vehicleId":1,"latitude":19.0760,"longitude":72.8777}'
```

### Using Postman

1. Download [Postman](https://www.postman.com/downloads/)
2. Import the API collection
3. Set base URL: `http://localhost:8080`
4. Test all endpoints

---

## 📊 Project Structure

```
src/main/java/com/highway/tolling/
├── TollingSystemApplication.java
├── controller/
│   ├── AdminController.java
│   ├── HighwayController.java
│   ├── LocationTrackingController.java
│   ├── UserController.java
│   └── VehicleController.java
├── model/
│   ├── Bill.java
│   ├── BillStatus.java
│   ├── Highway.java
│   ├── LocationTracking.java
│   ├── User.java
│   ├── Vehicle.java
│   ├── VehicleType.java
│   └── Wallet.java
├── repository/
│   ├── BillRepository.java
│   ├── HighwayRepository.java
│   ├── LocationTrackingRepository.java
│   ├── UserRepository.java
│   ├── VehicleRepository.java
│   └── WalletRepository.java
├── scheduler/
│   └── MonthlyBillingScheduler.java
└── service/
    ├── AdminService.java
    ├── BillService.java
    ├── DistanceCalculatorService.java
    ├── EmailService.java
    ├── HighwayDetectionService.java
    ├── HighwayService.java
    ├── LocationTrackingService.java
    ├── TollCalculationService.java
    ├── UserService.java
    ├── VehicleService.java
    └── WalletService.java
```

---

## 🎯 Key Features

### 1. Distance-Based Toll Calculation
Uses Haversine formula to calculate accurate distance between GPS coordinates.

### 2. Highway Detection
Bounding box algorithm to detect if a vehicle is traveling on a highway.

### 3. Vehicle Type Support
Different toll rates for:
- 🚗 CAR
- 🏍️ BIKE
- 🚌 BUS
- 🚛 TRUCK

### 4. Digital Wallet
- Add balance
- Auto-deduct toll
- Supports negative balance
- Deficit alerts

### 5. Automated Billing
Monthly scheduler runs on 1st of every month to generate bills automatically.

### 6. Email Notifications
- Bill notifications
- Payment reminders
- Welcome emails

---

## 🎓 Academic Note

This project is developed for **educational purposes** as part of an MCA curriculum. IoT functionality is **simulated** using backend APIs and does not require physical hardware.

### Learning Outcomes
- RESTful API development
- Database design and JPA relationships
- Scheduled tasks with Spring
- Geo-spatial calculations
- Business logic implementation
- Clean code principles

---

## 📸 Screenshots

*Add screenshots here when available*

---

## 🤝 Contributing

This is an academic project and not open for contributions. However, feel free to fork and modify for your own learning purposes.

---

## 👤 Author

**Albert J**  
MCA Student  
📧 Email: [albertcyse@gmail.com](mailto:albertcyse@gmail.com)  
🌍 Location: TamilNadu, India

---

## 📄 License

This project is intended for **academic use only** as part of MCA Final Year Project submission.

---

## 🙏 Acknowledgments

- Spring Boot Framework
- MySQL Community
- Stack Overflow Community
- MCA Faculty and Mentors

---

## 📞 Support

For queries related to this project, please contact the author via email.

---

**⭐ If you find this project helpful, please give it a star!**
