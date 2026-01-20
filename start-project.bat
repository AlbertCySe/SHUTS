@echo off
echo Starting Smart Highway Tolling System...
echo.
pause
echo If you see this, the batch file is working!
pause

color 0A
echo ========================================
echo Smart Highway Tolling System
echo MCA Final Year Project
echo ========================================
echo.
pause

REM Set the project root directory
set PROJECT_ROOT=%~dp0
echo Project root: %PROJECT_ROOT%
pause

REM Check if Node.js is installed
echo.
echo [CHECK 1/3] Checking Node.js...
pause
node --version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo.
    echo ERROR: Node.js is NOT installed!
    echo Please install from: https://nodejs.org/
    echo.
    pause
    pause
    exit /b 1
)
echo Node.js: OK
node --version
pause

REM Check if Maven is installed
echo.
echo [CHECK 2/3] Checking Maven...
pause
mvn --version >nul 2>&1
if errorlevel 1 (
    color 0E
    echo.
    echo ========================================
    echo WARNING: Maven is NOT installed!
    echo ========================================
    echo.
    echo QUICKEST SOLUTION:
    echo - Use IntelliJ IDEA or Eclipse to run the backend
    echo - Maven is built-in, no installation needed!
    echo.
    echo MANUAL INSTALL:
    echo 1. Download: https://maven.apache.org/download.cgi
    echo 2. Extract to: C:\Program Files\Apache\maven
    echo 3. Add to PATH and restart computer
    echo.
    pause
    echo.
    choice /C YN /M "Continue with FRONTEND ONLY (without backend)"
    if errorlevel 2 goto END
    if errorlevel 1 goto FRONTEND_ONLY
)

echo Maven: OK
mvn --version | findstr "Apache Maven"
pause

REM Check npm
echo.
echo [CHECK 3/3] Checking npm...
npm --version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo ERROR: npm not found!
    pause
    pause
    exit /b 1
)
echo npm: OK
npm --version
pause

REM Check dependencies
echo.
echo Checking frontend dependencies...
if not exist "%PROJECT_ROOT%frontend\node_modules\" (
    echo Installing npm dependencies...
    cd /d "%PROJECT_ROOT%frontend"
    call npm install
    cd /d "%PROJECT_ROOT%"
    echo Done!
) else (
    echo Dependencies: OK
)
pause

echo.
echo ========================================
echo Starting Backend and Frontend...
echo ========================================
pause

start "Backend" cmd /k "cd /d %PROJECT_ROOT% && color 0B && title Backend && echo Starting backend... && mvn spring-boot:run || pause"
timeout /t 8 /nobreak >nul

start "Frontend" cmd /k "cd /d %PROJECT_ROOT%frontend && color 0D && title Frontend && echo Starting frontend... && npm run dev || pause"
timeout /t 12 /nobreak >nul

start http://localhost:3000
goto SUCCESS

:FRONTEND_ONLY
echo Starting frontend only...
pause
start "Frontend" cmd /k "cd /d %PROJECT_ROOT%frontend && color 0D && title Frontend && npm run dev || pause"
timeout /t 8 /nobreak >nul
start http://localhost:3000

:SUCCESS
echo.
echo Application started!
echo Frontend: http://localhost:3000
pause
goto END

:END
echo Exiting...
pause
exit /b 0
