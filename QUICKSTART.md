# 🚀 Quick Start Guide

> **⚠️ CRITICAL:** MySQL must be **running** before starting the backend!

## Pre-Flight Checklist

Before running the project, ensure:
- ✅ Java 17+ installed
- ✅ Node.js 18+ installed
- ✅ **MySQL 8.0+ installed AND RUNNING** ⭐
- ✅ Database `tolling_system` created

---

## Required Software

Before running the project, install these manually:

### 1. Java 17+
- Download: https://adoptium.net/
- Install and verify: `java -version`

### 2. Node.js 18+ LTS
- Download: https://nodejs.org/
- Click the big green "LTS" button
- Install (accept all defaults)
- Verify: `node -v` and `npm -v`

### 3. Maven (Optional - use IntelliJ instead)
- **Option A:** Download Maven from https://maven.apache.org/download.cgi
- **Option B:** Use IntelliJ IDEA (has built-in Maven)

### 4. MySQL 8.0+ ⭐ MOST IMPORTANT
- Download: https://dev.mysql.com/downloads/mysql/
- **Install MySQL Server**
- Set root password during installation (REMEMBER THIS!)
- **Ensure MySQL service is set to auto-start**

#### ✅ VERIFY MySQL is Running (REQUIRED)

**Windows:**
```bash
# Check if MySQL service is running
sc query MySQL80
# If stopped, start it:
net start MySQL80
```

**Alternative: Services GUI**
```
1. Press Win + R
2. Type: services.msc
3. Find "MySQL80" or "MySQL"
4. Status should be "Running"
5. If not: Right-click → Start
6. Set Startup type to: Automatic
```

**Test Connection:**
```bash
mysql -u root -p
# Enter your MySQL password
# You should see: mysql>
exit;
```

> **⚠️ Common Error:** If backend shows "Connection refused" → MySQL is not running!

---

## Running the Project

### Backend (Spring Boot)

**Option 1: Using IntelliJ IDEA (Easiest)**
```
1. Open IntelliJ IDEA
2. File → Open → Select project folder
3. Wait for Maven sync
4. Navigate to: src/main/java/com/highway/tolling/TollingSystemApplication.java
5. Right-click → Run
```

**Option 2: Using Command Line (if Maven installed)**
```bash
mvn spring-boot:run
```

Backend runs at: http://localhost:8080

### Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at: http://localhost:3000

---

## Database Setup

```sql
CREATE DATABASE tolling_system;
```

Set environment variables:
```bash
# Windows
set DB_USERNAME=root
set DB_PASSWORD=your_password

# Or edit: src/main/resources/application.properties
```

---

## Summary

1. Install: Java, Node.js, MySQL (manual downloads - 10 minutes)
2. Run backend in IntelliJ
3. Run frontend: `cd frontend && npm run dev`
4. Open: http://localhost:3000

**That's it!** Manual installation is simpler and more reliable.

---

## 🔧 Troubleshooting

### Backend Error: "Connection refused" or "Communications link failure"

**Problem:** MySQL is not running

**Solution:**
```bash
# Windows - Start MySQL service
net start MySQL80

# OR use Services GUI
# Win + R → services.msc → MySQL80 → Start

# Verify it's running
sc query MySQL80
```

### Backend starts but can't find database

**Problem:** Database `tolling_system` not created

**Solution:**
```bash
mysql -u root -p
CREATE DATABASE tolling_system;
exit;
```

### Can't connect to MySQL at all

**Problem:** Wrong password or MySQL not installed

**Solution:**
```bash
# Test with correct credentials
mysql -u root -p

# If password forgotten, reset it:
# Search online for "MySQL reset root password Windows"
```
