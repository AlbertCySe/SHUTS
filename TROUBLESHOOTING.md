# ⚠️ TROUBLESHOOTING GUIDE

## Backend Won't Start - "Connection refused"

### Problem
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
Connection refused: no further information
```

### Solution: MySQL Service Not Running

#### Check MySQL Status
```bash
# Windows Command Prompt
sc query MySQL80
```

If it says **STOPPED**, start it:

```bash
# Start MySQL (as Administrator)
net start MySQL80
```

#### Using Windows Services GUI
1. Press `Win + R`
2. Type: `services.msc`
3. Find "MySQL80" or "MySQL"
4. Right-click → Properties
5. Startup type: **Automatic**
6. Click "Start"
7. Click "OK"

### Verify MySQL is Working
```bash
mysql -u root -p
# Enter your password
# You should see: mysql>
```

If successful, run `start-project.bat` again - backend will work!

---

## Other Common Issues

### Port 8080 Already in Use
Backend fails with "port already in use"

**Solution:**
```bash
# Find what's using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID)
taskkill /PID <process_id> /F
```

### Port 3000 Already in Use  
Frontend fails to start

**Solution:** Vite will automatically try port 3001, check terminal for actual port

### MySQL Password Forgotten

**Solution:** Search for "MySQL reset root password Windows" - requires MySQL reinstall or password reset procedure

---

## First-Time Setup Checklist

- [ ] Java 17+ installed
- [ ] Node.js 18+ installed  
- [ ] Maven installed OR using IntelliJ IDEA
- [ ] **MySQL 8.0+ installed**
- [ ] **MySQL service is RUNNING** ⭐
- [ ] Database `tolling_system` created
- [ ] Database credentials set in `application.properties` or environment variables

Run `start-project.bat` and both services should start successfully!
