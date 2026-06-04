# 📋 Complete Setup Guide
### Smart Highway Usage-Based Tolling System

> **Back to:** [README.md](../README.md) | **Related:** [Manual Setup](MANUAL_SETUP.md) · [Troubleshooting](TROUBLESHOOTING.md)

---

## 📌 Before You Begin

| Requirement | Version | Check Command | Get It |
|-------------|---------|--------------|--------|
| Java (JDK) | 17 LTS | `java -version` | https://adoptium.net/ |
| Node.js | 18+ | `node -v` | https://nodejs.org/ |
| npm | 8+ | `npm -v` | Included with Node.js |
| MySQL | 8.0+ | Check Services | https://dev.mysql.com/downloads/mysql/ |
| Maven | 3.6+ | `mvn -v` | **Handled automatically** |
| OS | Windows 10/11 | — | For `.bat` scripts |
| RAM | 4 GB min | — | 8 GB recommended |

> ℹ️ **Maven is handled automatically.** The project includes a Maven wrapper (`mvnw.cmd`) and a `fix-maven.bat` script — no manual Maven installation required.

---

## ⚡ Helper Scripts — What Each One Does

### `start-project.bat` ⭐ — Primary Launcher

Here is exactly what happens, step by step, when you run this script:

**Step 1 — Terminal Setup & Recursion Prevention**
The script re-launches itself in a persistent `cmd /k` window with a green-on-black color scheme (`color 0A`) so the window doesn't close on error. Sets the window title.

**Step 2 — `.env` Bootstrap**
Looks for a `.env` file in the project root.
- **If missing:** Enters interactive setup. Prompts for MySQL username (defaults to `root`) and password. Creates the `.env` file automatically with entered values plus email placeholder defaults.
- **If found:** Loops through every line and exports each `KEY=VALUE` pair as a live Windows environment variable. Spring Boot reads these via `${DB_USERNAME}` and `${DB_PASSWORD}` property placeholders.

**Step 3 — Dependency Validation**
- **Java:** Runs `java -version`. Halts with download link if not found.
- **Node.js:** Runs `node -v`. Falls back to checking `C:\Program Files\nodejs\node.exe`. Halts and directs to `install-nodejs.bat` if not found.

**Step 4 — Backend Compilation (3-tier fallback)**
Compiles the project into a runnable JAR using three fallback strategies:
1. Try the local Maven Wrapper: `.\mvnw.cmd clean package -DskipTests`
2. If wrapper fails: check for portable Maven at `C:\maven-portable\`
3. Try global system Maven: `mvn clean package -DskipTests`
If all three fail — halts with an ASCII-art guide to fix Maven.

**Step 5 — Frontend Dependency Check**
Checks if `frontend/node_modules/` exists. If not (first run), runs `npm install` automatically.

**Step 6 — Process Spawning**
Spawns two separate color-coded terminal windows:
- **Blue window (Backend):** Executes `java -jar target\tolling-system-1.0.0.jar`. Waits 12 seconds for Tomcat to bind to Port 8080.
- **Yellow window (Frontend):** Navigates to `frontend/` and runs `npm run dev`. Waits 8 seconds for Vite to boot.

**Step 7 — Browser Launch**
Runs `start http://localhost:3000` to open your default browser, then prints a success summary.

---

### `fix-maven.bat` 🛠️ — Maven Installer & Fixer

Clears the local `.m2` cache for problematic artifacts and retries the download. Downloads a portable Maven binary to `C:\maven-portable\` if Maven isn't installed at all. Specifically designed to work around common student network environments with proxy/firewall restrictions.

**When to use:** If `mvn` is not found or `mvnw.cmd` fails with dependency download errors.

---

### `install-nodejs.bat` 🚀 — Node.js Installer

Silently downloads the latest LTS Node.js and configures your system PATH. After running, `node` and `npm` work immediately in new terminal windows.

**When to use:** If `node -v` or `npm -v` return "command not found."

---

### `install-maven-offline.bat` 🔌 — Offline Maven Installer

Installs Maven from a bundled archive without requiring an internet connection.

**When to use:** If you are on a slow or completely offline network and `fix-maven.bat` fails.

---

## 🗝️ Credential Configuration

### Option A — Automatic (Recommended)

Run `start-project.bat`. On first run with no `.env` file:
```
No .env file found. Let's create one.

Enter MySQL Username (default: root): root
Enter MySQL Password: ___________

.env file created successfully.
```
Credentials are saved and reused on every future launch.

### Option B — Manual

```bash
copy .env.example .env
# Open .env in any text editor and fill in your values
```

Your `.env` file:
```env
# Database Configuration
DB_USERNAME=root
DB_PASSWORD=your_mysql_password_here

# Email Configuration (Optional)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
EMAIL_FROM=noreply@highwaytolling.com
EMAIL_ENABLED=false
```

> ⚠️ **Rules:**
> - No quotes around values: `DB_PASSWORD=mypass` ✅ — `DB_PASSWORD="mypass"` ❌
> - No spaces around `=`: `DB_PASSWORD=mypass` ✅ — `DB_PASSWORD = mypass` ❌
> - Never commit `.env` — it is in `.gitignore`

### Default Admin Credentials

The admin login uses credentials from `application.properties`:
```
Email:    admin@highway.com
Password: admin123
```
These can be changed by setting `ADMIN_EMAIL` and `ADMIN_PASSWORD` in your `.env` file.

---

## 📄 Full Configuration File Contents

### `application.properties` (Main Backend)

```properties
# Application Configuration
spring.application.name=Smart Highway Tolling System
server.port=8080

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/tolling_system?createDatabaseIfNotExist=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
# update = auto-creates and updates schema from entity classes
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.com.highway.tolling=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG

# Email Configuration (SMTP/Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME:your-email@example.com}
spring.mail.password=${EMAIL_PASSWORD:your-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# Email sender
app.email.from=${EMAIL_FROM:noreply@highwaytolling.com}
app.email.enabled=${EMAIL_ENABLED:true}

# Admin Login Credentials
admin.email=${ADMIN_EMAIL:admin@highway.com}
admin.password=${ADMIN_PASSWORD:admin123}
```

> **Notes:**
> - `spring.jpa.show-sql=true` is currently ON — all SQL queries print to the backend terminal. Useful for debugging. Remove or set to `false` before submission for cleaner logs.
> - `${DB_USERNAME:root}` means: use `DB_USERNAME` env var, fall back to `root` if not set.
> - Email is disabled by default in `.env.example` (`EMAIL_ENABLED=false`). Set to `true` and provide Gmail credentials to enable bill notification emails.

---

### `vite.config.js` (React Frontend)

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

> **The proxy is critical.** Without it, every `/api/*` call from the frontend would be blocked by the browser's CORS policy because it originates from Port 3000 but targets Port 8080. The Vite proxy forwards these requests server-side, bypassing CORS entirely during development.

---

### `.env.example` (Template)

```env
# Database Configuration
# INSTRUCTIONS: Copy this file to .env and update with your actual credentials
DB_USERNAME=root
DB_PASSWORD=your_password_here

# Email Configuration (Optional - for future use)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
EMAIL_FROM=noreply@highwaytolling.com
EMAIL_ENABLED=false

# Admin credentials are set in application.properties
# Default: admin@highway.com / admin123
```

---

### `pom.xml` — Maven Dependencies

```xml
<dependencies>
    <!-- Spring Boot Web — REST API, Tomcat server -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA — database ORM -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL JDBC Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok — reduces boilerplate (optional) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- DevTools — hot reload during development -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- Spring Mail — email notifications -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>

    <!-- Validation — @Valid @NotNull etc. on DTOs -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Test framework -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 🚀 Step-by-Step Launch

### Step 1 — Get the Project
```bash
git clone https://github.com/albertcyse/smart-highway-tolling-system.git
# OR extract the ZIP. Navigate into the 'Initial' folder.
```

### Step 2 — Install Prerequisites (First Time Only)
```bash
install-nodejs.bat    # If node/npm not found
fix-maven.bat         # If mvn not found
```

### Step 3 — Set Credentials (First Time Only)
```bash
start-project.bat     # Will prompt for MySQL password on first run
```

### Step 4 — Launch
```bash
start-project.bat     # Starts everything and opens browser
```

**Expected terminal output:**
```
[0/4] Loading environment...  ✓ .env loaded
[1/4] Checking Java...        ✓ Java 17 found
[2/4] Checking Node.js...     ✓ Node.js v18.x found
[3/4] Checking Maven...       ✓ Maven found
[4/4] Frontend dependencies...✓ node_modules ready

Starting Backend...  → BLUE terminal (Spring Boot)
Starting Frontend... → YELLOW terminal (Vite)
Opening browser...   → http://localhost:3000
```

### Step 5 — (Optional) Launch Standalone IoT Simulator
```bash
cd Initial/iot-simulator
run-simulator.bat
# Opens telemetry dashboard at http://localhost:8082
```

### Step 6 — First Use
1. Visit http://localhost:3000
2. Create a User account (Register page)
3. Submit a Vehicle ADD Request (Vehicles page)
4. Switch to Admin view → approve the request
5. Wallet is auto-seeded
6. GPS simulation begins — watch toll deduct from wallet

---

## ✅ Post-Launch Verification

| Check | How | Expected |
|-------|-----|---------|
| Backend running | `curl http://localhost:8080/api/users` | `[]` or user list |
| Frontend running | Visit http://localhost:3000 | Login page loads |
| Database connected | Backend terminal | No `Connection refused` errors |
| Simulator running | Visit http://localhost:8082 | Telemetry dashboard loads |
| GPS flowing | Check `location_tracking` table | New rows every few seconds |
| Toll deducting | Wallet balance in User Dashboard | Decreasing while on highway |

---

## 🔄 Restarting & Stopping

**Restart:** Close both terminal windows → run `start-project.bat` again.
**Stop:** Close the blue (backend) and yellow (frontend) terminal windows.
**Data:** All data persists in MySQL between restarts. H2 simulator data persists in `iot_simulator_db.mv.db`.

---

## ❓ Something Not Working?

| Problem | Fix |
|---------|-----|
| Script error | See [Troubleshooting](TROUBLESHOOTING.md) |
| Non-Windows machine | See [Manual Setup](MANUAL_SETUP.md) |
| Want manual control | See [Manual Setup](MANUAL_SETUP.md) |

---

*Setup Guide — Smart Highway Tolling System*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
