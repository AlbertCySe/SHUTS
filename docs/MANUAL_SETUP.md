# 🔧 Manual Setup Guide
### Smart Highway Usage-Based Tolling System

> **Back to:** [README.md](../README.md) | **Related:** [Setup Guide](SETUP_GUIDE.md) · [Troubleshooting](TROUBLESHOOTING.md)

---

## 📌 When to Use This Guide

Use this guide if you:
- Are on **macOS or Linux** (the `.bat` scripts only run on Windows)
- Prefer to control each step manually
- Want to understand exactly what each part of the setup does
- Are running the project on a server or CI environment
- Are debugging a failed automated setup

---

## 🧰 Installing Prerequisites

### 1. Java 17 (JDK)

Java 17 is the Long-Term Support (LTS) version used by this project. It must be a JDK (Java Development Kit), not just a JRE.

**Windows:**
```bash
# Download from: https://adoptium.net/
# Choose: Eclipse Temurin 17 (LTS) → Windows x64 → .msi installer
# Run the installer — it configures JAVA_HOME automatically

# Verify:
java -version
# Expected: openjdk version "17.x.x"
```

**macOS (using Homebrew):**
```bash
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
```

**Ubuntu / Debian Linux:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk -y
java -version
```

**Fedora / RHEL Linux:**
```bash
sudo dnf install java-17-openjdk-devel -y
java -version
```

> ⚠️ If you have multiple Java versions installed, set JAVA_HOME explicitly:
> ```bash
> export JAVA_HOME=/path/to/java17
> export PATH=$JAVA_HOME/bin:$PATH
> ```

---

### 2. Node.js 18+ and npm

**Windows:**
```bash
# Download from: https://nodejs.org/ → LTS version → Windows Installer (.msi)
# Run installer — adds node and npm to PATH automatically

node -v   # Should show v18.x.x or higher
npm -v    # Should show npm version
```

**macOS:**
```bash
brew install node@18
node -v
npm -v
```

**Ubuntu / Debian:**
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs
node -v
npm -v
```

**Fedora / RHEL:**
```bash
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo dnf install -y nodejs
node -v
```

---

### 3. MySQL 8.0+

**Windows:**
```bash
# Download from: https://dev.mysql.com/downloads/mysql/
# Run the MySQL Installer
# Choose: Developer Default or Server Only
# Set a root password — write it down, you will need it
# Start the MySQL service during installation

# Verify (open MySQL Command Line Client):
mysql -u root -p
# Enter password when prompted
SHOW DATABASES;
# You should see a list of default databases
```

**macOS:**
```bash
brew install mysql
brew services start mysql
mysql_secure_installation   # Set root password here
mysql -u root -p
```

**Ubuntu / Debian:**
```bash
sudo apt install mysql-server -y
sudo systemctl start mysql
sudo mysql_secure_installation   # Follow prompts to set root password
sudo mysql -u root -p
```

**Fedora / RHEL:**
```bash
sudo dnf install mysql-server -y
sudo systemctl start mysqld
# Get temporary password:
sudo grep 'temporary password' /var/log/mysqld.log
mysql -u root -p   # Use the temporary password
# Then change it:
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourNewPassword';
```

> ℹ️ You do NOT need to manually create the database. The application creates `tolling_system` automatically on first launch.

---

### 4. Maven 3.6+

**Windows:**
```bash
# Download from: https://maven.apache.org/download.cgi
# Choose: Binary zip archive (e.g. apache-maven-3.9.x-bin.zip)
# Extract to C:\Program Files\Apache\maven (or similar)
# Add C:\Program Files\Apache\maven\bin to your System PATH

# Verify:
mvn -v
# Expected: Apache Maven 3.x.x
```

**macOS:**
```bash
brew install maven
mvn -v
```

**Ubuntu / Debian:**
```bash
sudo apt install maven -y
mvn -v
```

**Fedora / RHEL:**
```bash
sudo dnf install maven -y
mvn -v
```

> ℹ️ **Alternatively:** You can skip global Maven installation and use the Maven Wrapper included in the project:
> - On Windows: use `mvnw.cmd` instead of `mvn`
> - On macOS/Linux: use `./mvnw` instead of `mvn`

---

## 🗝️ Configuring Database Credentials

The application reads your MySQL credentials from environment variables. The project provides two ways to configure them:

### Method A — `.env` File (Recommended)

```bash
# Navigate to your project root (Initial folder)
# Copy the template:
cp .env.example .env    # macOS / Linux
copy .env.example .env  # Windows

# Open .env in a text editor and fill in:
DB_USERNAME=root
DB_PASSWORD=your_mysql_password_here
```

### Method B — Export Environment Variables (Linux/macOS)

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password_here

# Add these to ~/.bashrc or ~/.zshrc to make them permanent:
echo 'export DB_USERNAME=root' >> ~/.bashrc
echo 'export DB_PASSWORD=your_password' >> ~/.bashrc
source ~/.bashrc
```

### How the Application Uses These Credentials

The `application.properties` file reads from these environment variables:
```
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.url=jdbc:mysql://localhost:3306/tolling_system?createDatabaseIfNotExist=true
```

The `createDatabaseIfNotExist=true` parameter ensures the `tolling_system` database is created automatically if it does not already exist.

---

## 🔨 Building the Backend

Navigate to the project root (the `Initial` folder):

```bash
cd path/to/smart-highway-tolling-system/Initial

# Download all dependencies (first time — may take a few minutes):
mvn dependency:resolve

# Compile and package:
mvn clean package -DskipTests

# Run the backend:
mvn spring-boot:run

# OR run the packaged JAR directly:
java -jar target/tolling-system-0.0.1-SNAPSHOT.jar
```

**What to look for in the terminal:**

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

:: Spring Boot :: (v3.2.1)

...
Started TollingSystemApplication in 4.3 seconds
```

The backend is ready when you see `Started TollingSystemApplication`.

**Backend is now available at:** http://localhost:8080

---

## 🖥️ Setting Up the Frontend

Open a **new terminal window** (keep the backend running):

```bash
# Navigate to the frontend directory:
cd path/to/smart-highway-tolling-system/Initial/frontend

# Install all npm dependencies (first time only — may take 1-2 minutes):
npm install

# Start the development server:
npm run dev
```

**What to look for:**

```
  VITE v5.x.x  ready in 300 ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

**Frontend is now available at:** http://localhost:3000

---

## 🛰️ Setting Up the Standalone IoT Simulator (Optional)

The Standalone Simulator is a completely separate Spring Boot application. It requires the **main backend to be running first**.

Open a **third terminal window**:

```bash
# Navigate to the simulator directory:
cd path/to/smart-highway-tolling-system/Initial/iot-simulator

# Install frontend dependencies for the simulator dashboard:
cd frontend && npm install && cd ..

# Build and run the simulator backend:
mvn spring-boot:run
```

**Simulator is now available at:** http://localhost:8082

The simulator will automatically:
1. Start its embedded H2 database
2. Query the main backend at http://localhost:8080 to sync vehicle data
3. Begin simulating GPS data for all registered vehicles

---

## ⚙️ Configuration Reference

### Backend: `application.properties`

Location: `Initial/src/main/resources/application.properties`

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | Port the backend listens on |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/tolling_system` | MySQL connection URL |
| `spring.datasource.username` | `${DB_USERNAME}` | Reads from environment variable |
| `spring.datasource.password` | `${DB_PASSWORD}` | Reads from environment variable |
| `spring.jpa.hibernate.ddl-auto` | `update` | Creates/updates tables automatically |
| `spring.jpa.show-sql` | `false` | Set to `true` to log all SQL queries |

> 📋 **Data to be filled from Antigravity — Prompt #4:** Ask Antigravity for the complete `application.properties` file contents and the complete `.env.example` file contents.

### Changing the Backend Port

If port 8080 is in use:

```properties
# In application.properties:
server.port=8081
```

Then update the frontend proxy in `Initial/frontend/vite.config.js`:
```javascript
proxy: {
  '/api': 'http://localhost:8081'  // Update to match
}
```

And update the API base URL in `Initial/frontend/src/services/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8081';
```

---

## 🔍 Verifying the Installation

After starting all services, run these checks:

```bash
# 1. Test backend is responding:
curl http://localhost:8080/api/users
# Expected: [] or a JSON array of users

# 2. Test backend can reach the database:
# Check the backend terminal — no "Connection refused" errors

# 3. Test frontend is serving:
# Visit http://localhost:3000 in your browser

# 4. Test simulator (if running):
curl http://localhost:8082/api/simulation/vehicles
# Expected: JSON list of synced vehicles
```

---

## 🐧 Linux-Specific Notes

### Running MySQL as a Service

```bash
# Start:
sudo systemctl start mysql

# Enable on boot:
sudo systemctl enable mysql

# Check status:
sudo systemctl status mysql
```

### Firewall Considerations

```bash
# If MySQL refuses remote connections:
sudo ufw allow 3306    # Ubuntu
sudo firewall-cmd --add-port=3306/tcp --permanent  # Fedora
```

### Running the Backend in the Background

```bash
# Using nohup:
nohup mvn spring-boot:run > backend.log 2>&1 &
echo "Backend PID: $!"

# Stop it later:
kill $(lsearch backend PID)
```

---

## 🍎 macOS-Specific Notes

### Java Home on macOS

```bash
# List installed JDKs:
/usr/libexec/java_home -V

# Set JAVA_HOME for Java 17:
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### MySQL on macOS

```bash
# If MySQL refuses connection after installation:
brew services restart mysql

# Create a root password if none was set:
mysql -u root
ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_password';
FLUSH PRIVILEGES;
```

---

## ❓ Still Having Issues?

→ See the complete troubleshooting guide: **[🐛 TROUBLESHOOTING.md](TROUBLESHOOTING.md)**

---

*Manual Setup Guide — Smart Highway Tolling System*
*Maintained by Albert J — [albertcyse@gmail.com](mailto:albertcyse@gmail.com)*
