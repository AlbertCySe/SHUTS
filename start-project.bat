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

echo ####################################################################################
echo #                  SMART HIGHWAY TOLLING SYSTEM - PROJECT LAUNCHER                #
echo #                        SRM Institute of Technology - Trichy                     #
echo ###################################################################################
echo.
echo Developed by: Albert J (MCA Final Year)
echo.

timeout /t 1 /nobreak >nul

REM Check and load .env file
echo [0/3] Checking environment configuration...
if not exist ".env" (
    echo ⚠ .env file not found!
    echo.
    echo ##################################################################################
    echo                             DATABASE CREDENTIALS SETUP
    echo ##################################################################################
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
echo [1/3] Checking Java...
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
echo [2/3] Checking Node.js...

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
echo ###################################################################################
echo                         NODE.JS IS REQUIRED FOR FRONTEND
echo ###################################################################################
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
echo ###################################################################################
pause
exit /b 1

:nodejs_found
echo.

REM Build backend
echo [3/3] Building backend...
echo.

REM Try Maven Wrapper first
echo Attempting build with Maven Wrapper...
call .\mvnw.cmd clean package -DskipTests >nul 2>&1
if %errorlevel% EQU 0 (
    echo ✓ Backend built successfully with Maven Wrapper
    echo.
    goto :build_complete
)

echo Maven Wrapper failed (network/firewall issue)
echo Trying alternative build methods...
echo.

REM Fallback 1: Check for portable Maven (installed by fix-maven.bat)
if not exist "C:\maven-portable\apache-maven-3.9.6\bin\mvn.cmd" goto :try_system_maven

echo Using portable Maven (from fix-maven.bat)...
call "C:\maven-portable\apache-maven-3.9.6\bin\mvn.cmd" clean package -DskipTests
if errorlevel 1 goto :try_system_maven
echo ✓ Backend built successfully with portable Maven
echo.
goto :build_complete

:try_system_maven
REM Fallback 2: Try system Maven
call mvn -v >nul 2>&1
if errorlevel 1 goto :maven_not_found

echo Using system Maven...
call mvn clean package -DskipTests
if errorlevel 1 goto :maven_not_found
echo ✓ Backend built successfully with system Maven
echo.
goto :build_complete

:maven_not_found

REM All build methods failed
echo.
echo ###################################################################################
echo                             ⚠ MAVEN SETUP NEEDED
echo ###################################################################################
echo.
echo Don't worry! This is easy to fix.
echo.
echo ┌──────────────────────────────────────────────────────────┐
echo │                 OPTION 1: AUTO-FIX (Recommended - Takes 3 minutes)             │
echo └──────────────────────────────────────────────────────────┘
echo.
echo   1. Close this window
echo   2. Double-click: fix-maven.bat
echo   3. Wait for it to finish
echo   4. Run start-project.bat again
echo.
echo   The fix-maven.bat tool will:
echo   → Download Maven automatically
echo   → Install it for you
echo   → Configure everything
echo.
echo ┌──────────────────────────────────────────────────────────┐
echo │                  OPTION 2: OFFLINE FIX (If Option 1 doesn't work)              │
echo └──────────────────────────────────────────────────────────┘
echo.
echo   If you see "No internet" or download fails:
echo.
echo   1. On ANY computer with internet, download:
echo      https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip
echo.
echo   2. Copy the ZIP file to THIS computer's Desktop
echo.
echo   3. Double-click: install-maven-offline.bat
echo.
echo ###################################################################################
echo.
echo TIP: Option 1 is easiest! Just run fix-maven.bat
echo.
pause
exit /b 1

:build_complete



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

echo ###################################################################################
echo #                              STARTING SERVICES                                  #
echo ###################################################################################
echo.

REM Start backend with Maven
echo [Backend] Starting backend server...

REM Run the JAR file directly (more reliable than spring-boot:run)
start "Backend - Spring Boot" cmd /c "cd /d "%~dp0" & title BACKEND SERVER (Port 8080) & color 0B & echo Starting Spring Boot backend... & echo. & java -jar target\tolling-system-1.0.0.jar & echo. & echo Backend stopped. & pause"
echo ✓ Backend started in BLUE window
echo   URL: http://localhost:8080
echo.
echo Waiting 12 seconds for backend to initialize...
timeout /t 12 /nobreak >nul





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

echo ###################################################################################
echo #                                STATUS SUMMARY                                   #
echo ###################################################################################
echo.
echo ✓ Backend:  http://localhost:8080 (BLUE window)
echo ✓ Frontend: http://localhost:3000 (YELLOW window)
echo.
echo Both services running! Check the colored terminal windows.
echo.
echo Close the colored terminal windows to stop servers.
echo.
echo Press any key to close this launcher...
pause >nul
exit /b 0
