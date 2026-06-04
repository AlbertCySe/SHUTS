@echo off
SETLOCAL EnableDelayedExpansion

title IoT Simulator Launcher
color 0B

echo ╔════════════════════════════════════════════════════════════╗
echo ║            IOT VEHICLE SIMULATOR LAUNCHER                ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

REM 1. Set Path for Node.js if it exists in Default Location
if exist "C:\Program Files\nodejs\node.exe" (
    set "PATH=%PATH%;C:\Program Files\nodejs"
)

REM 2. Identify Maven Command
set MAVEN_CMD=mvn

REM Check local wrapper first
if exist "mvnw.cmd" (
    set MAVEN_CMD=".\mvnw.cmd"
)
if exist "..\mvnw.cmd" (
    set MAVEN_CMD="..\mvnw.cmd"
)

REM Always prioritize the global offline installation if it exists
if exist "C:\maven-portable\apache-maven-3.9.6\bin\mvn.cmd" (
    set MAVEN_CMD="C:\maven-portable\apache-maven-3.9.6\bin\mvn.cmd"
)

echo Start IoT Java Backend (Port 8082)...
start "IoT Simulator Backend" cmd /k "!MAVEN_CMD! spring-boot:run"

echo Waiting for Java backend to start...
timeout /t 8 /nobreak >nul

echo Opening IoT Dashboard...
start http://localhost:8082

echo.
echo Standalone IoT Simulator is booting up securely!
pause >nul
exit /b 0

