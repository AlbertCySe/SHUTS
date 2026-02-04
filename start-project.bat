@echo off
SETLOCAL EnableDelayedExpansion

REM Keep window open on error
if not "%1"=="NESTED" (
    cmd /k "%~f0" NESTED
    exit /b
)

color 0A
title Smart Highway Tolling System Launcher
cls

echo ╔════════════════════════════════════════════════════════════╗
echo ║     SMART HIGHWAY TOLLING SYSTEM - PROJECT LAUNCHER      ║
echo ║              SRM Institute of Technology - Trichy         ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo Developed by: Albert J (MCA Final Year)
echo.

timeout /t 1 /nobreak >nul

REM Check and load .env file
echo [0/4] Checking environment configuration...
if not exist ".env" (
    echo ⚠ .env file not found!
    echo.
    echo ════════════════════════════════════════════════════════════
    echo   DATABASE CREDENTIALS SETUP
    echo ════════════════════════════════════════════════════════════
    echo.
    echo This is your first time running the project.
    echo Please provide your MySQL database credentials:
    echo.
    
    set /p DB_USER="Enter MySQL Username (default: root): "
    if "!DB_USER!"=="" set DB_USER=root
    
    set /p DB_PASS="Enter MySQL Password: "
    if "!DB_PASS!"=="" (
        echo ❌ Password cannot be empty!
        pause
        exit /b 1
    )
    
    echo.
    echo Creating .env file with your credentials...
    (
        echo # Database Configuration
        echo DB_USERNAME=!DB_USER!
        echo DB_PASSWORD=!DB_PASS!
        echo.
        echo # Email Configuration (Optional - for future use^)
        echo EMAIL_USERNAME=your-email@gmail.com
        echo EMAIL_PASSWORD=your-app-password
        echo EMAIL_FROM=noreply@highwaytolling.com
        echo EMAIL_ENABLED=false
    ) > .env
    
    echo ✓ .env file created successfully!
    echo.
)

REM Load environment variables from .env file
echo Loading environment variables...
for /f "usebackq tokens=1,* delims==" %%a in (".env") do (
    set "line=%%a"
    REM Skip comments and empty lines
    if not "!line:~0,1!"=="#" if not "!line!"=="" (
        set "%%a=%%b"
    )
)
echo ✓ Environment variables loaded
echo.

REM Check Java
echo [1/4] Checking Java...
java -version 2>nul
if errorlevel 1 (
    echo ❌ Java NOT found
    echo.
    echo Please install Java 17+: https://adoptium.net/
    pause
    exit /b 1
)
echo ✓ Java installed
echo.

REM Check Node.js
echo [2/4] Checking Node.js...

REM Try node command first (if in PATH)
node -v >nul 2>&1
if not errorlevel 1 (
    echo ✓ Node.js installed
    goto :nodejs_found
)

REM If not in PATH, check installation directory
if exist "C:\Program Files\nodejs\node.exe" (
    echo ✓ Node.js installed (found in C:\Program Files\nodejs)
    REM Add to PATH for this session
    set "PATH=%PATH%;C:\Program Files\nodejs"
    goto :nodejs_found
)

REM Not found anywhere
echo ❌ Node.js NOT found
echo.
echo ════════════════════════════════════════════════════════════
echo   NODE.JS IS REQUIRED FOR FRONTEND
echo ════════════════════════════════════════════════════════════
echo.
echo You have 2 options:
echo.
echo   [Option 1] Auto-Install Node.js (Recommended)
echo   Run: install-nodejs.bat (as Administrator)
echo.
echo   [Option 2] Manual Install
echo   Download from: https://nodejs.org/
echo   Install Node.js 18+ LTS version
echo.
echo ════════════════════════════════════════════════════════════
pause
exit /b 1

:nodejs_found
echo.

REM Check Maven
echo [3/4] Checking Maven...
set MAVEN_FOUND=0

REM Try mvn command first (if in PATH)
mvn -v >nul 2>&1
if not errorlevel 1 (
    echo ✓ Maven found in PATH
    set MAVEN_FOUND=1
    goto :maven_check_done
)

REM Check installation directory
if exist "C:\Program Files\Apache\maven\bin\mvn.cmd" (
    echo ✓ Maven found in C:\Program Files\Apache\maven
    set "PATH=%PATH%;C:\Program Files\Apache\maven\bin"
    set MAVEN_FOUND=1
    goto :maven_check_done
)

REM Not found
echo ⚠ Maven not found in system PATH
echo.
    echo ════════════════════════════════════════════════════════════
    echo   MAVEN IS REQUIRED TO RUN THE BACKEND AUTOMATICALLY
    echo ════════════════════════════════════════════════════════════
    echo.
    echo You have 2 options:
    echo.
    echo   [Option 1] Install Maven (Recommended for auto-start)
    echo   1. Download: https://maven.apache.org/download.cgi
    echo   2. Extract to: C:\Program Files\Apache\maven
    echo   3. Add to PATH: C:\Program Files\Apache\maven\bin
    echo   4. Restart this script
    echo.
    echo   [Option 2] Use IntelliJ IDEA (No Maven install needed)
    echo   1. Open IntelliJ IDEA
    echo   2. Open this project folder
    echo   3. Navigate to: TollingSystemApplication.java
    echo   4. Right-click -^> Run
    echo.
    echo ════════════════════════════════════════════════════════════
    echo.
    choice /C 12 /M "Choose: [1] Install Maven first (Exit) or [2] Continue with manual backend"
    
    if errorlevel 3 (
        echo.
        echo Continuing without Maven...
        echo You'll need to start backend manually.
        echo.
        set MAVEN_FOUND=0
    )
    if errorlevel 2 (
        echo.
        echo Please install Maven manually and run this script again.
        echo.
        pause
        exit /b 0
    )
    if errorlevel 1 (
        echo.
        echo ════════════════════════════════════════════════════════════
        echo   AUTO-INSTALLER INSTRUCTIONS
        echo ════════════════════════════════════════════════════════════
        echo.
        echo 1. Close this window
        echo 2. Right-click: install-maven.bat
        echo 3. Select "Run as Administrator"
        echo 4. Wait for installation to complete
        echo 5. Run start-project.bat again
        echo.
        echo ════════════════════════════════════════════════════════════
        pause
        exit /b 0
    )
)

:maven_check_done
echo.

REM Check frontend
if not exist "frontend\" (
    echo ❌ Frontend folder not found
    pause
    exit /b 1
)

REM Install frontend dependencies
if not exist "frontend\node_modules\" (
    echo Installing frontend dependencies...
    cd frontend
    call npm install
    if errorlevel 1 (
        echo ❌ npm install failed
        cd ..
        pause
        exit /b 1
    )
    cd ..
    echo ✓ Dependencies installed
) else (
    echo ✓ Frontend dependencies ready
)
echo.

echo ╔════════════════════════════════════════════════════════════╗
echo ║                  STARTING SERVICES                         ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

REM Start backend with Maven
if %MAVEN_FOUND%==1 (
    echo [Backend] Starting with Maven...
    start "Backend - Spring Boot" cmd /c "title BACKEND SERVER (Port 8080) & color 0B & echo Starting Spring Boot backend... & echo. & mvn spring-boot:run & echo. & echo Backend stopped. & pause"
    echo ✓ Backend started in BLUE window
    echo   URL: http://localhost:8080
    echo.
    echo Waiting 12 seconds for backend to initialize...
    timeout /t 12 /nobreak >nul
) else (
    echo [Backend] Skipped - Maven not available
    echo.
    echo ⚠ START BACKEND MANUALLY:
    echo   Open IntelliJ IDEA -^> Run TollingSystemApplication.java
    echo.
    pause
)

REM Start frontend
echo [Frontend] Starting with npm...
start "Frontend - React" cmd /c "cd frontend & title FRONTEND SERVER (Port 3000) & color 0E & echo Starting React frontend... & echo. & npm run dev & echo. & echo Frontend stopped. & pause"
echo ✓ Frontend started in YELLOW window
echo   URL: http://localhost:3000
echo.

echo Waiting for frontend to start...
timeout /t 8 /nobreak >nul

echo Opening browser...
start http://localhost:3000
echo.

echo ╔════════════════════════════════════════════════════════════╗
echo ║                    STATUS SUMMARY                          ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
if %MAVEN_FOUND%==1 (
    echo ✓ Backend:  http://localhost:8080 (BLUE window)
    echo ✓ Frontend: http://localhost:3000 (YELLOW window)
    echo.
    echo Both services running! Check the colored terminal windows.
) else (
    echo ⚠ Backend:  NOT STARTED - Start manually with IntelliJ
    echo ✓ Frontend: http://localhost:3000 (YELLOW window)
    echo.
    echo Frontend is running. Start backend manually.
)
echo.
echo Close the colored terminal windows to stop servers.
echo.
echo Press any key to close this launcher...
pause >nul
exit /b 0
