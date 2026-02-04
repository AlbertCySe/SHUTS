@echo off
SETLOCAL EnableDelayedExpansion

REM Keep window open even on error
if not "%1"=="NESTED" (
    cmd /k "%~f0" NESTED
    exit /b
)

color 0E
title Node.js Auto-Installer for Windows

cls
echo ╔════════════════════════════════════════════════════════════╗
echo ║            NODE.JS AUTO-INSTALLER                          ║
echo ║         Smart Highway Tolling System Helper                ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo This script will download and install Node.js LTS (v20.x)
echo.

REM Check if running as administrator
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ ERROR: This script requires Administrator privileges
    echo.
    echo Please:
    echo   1. Right-click this file: install-nodejs.bat
    echo   2. Select "Run as Administrator"
    echo   3. Try again
    echo.
    pause
    exit /b 1
)

echo ✓ Running with Administrator privileges
echo.

REM Check if Node.js is already installed
echo Checking for existing Node.js installation...
node -v >nul 2>&1
if not errorlevel 1 (
    echo.
    echo ✓ Node.js is already installed!
    node -v
    npm -v
    echo.
    echo No installation needed.
    pause
    exit /b 0
)

echo ❌ Node.js not found - proceeding with installation...
echo.

REM Set Node.js version and download URL
set NODE_VERSION=20.11.0
set NODE_DOWNLOAD_URL=https://nodejs.org/dist/v%NODE_VERSION%/node-v%NODE_VERSION%-x64.msi
set TEMP_DIR=%TEMP%\nodejs-install
set MSI_FILE=%TEMP_DIR%\nodejs.msi
set LOG_FILE=%TEMP_DIR%\nodejs-install.log

echo [Step 1/4] Preparing download...
echo Node.js Version: v%NODE_VERSION% LTS
echo.

REM Create temp directory
if not exist "%TEMP_DIR%" mkdir "%TEMP_DIR%"

REM Download Node.js
echo [Step 2/4] Downloading Node.js installer...
echo This may take 2-5 minutes depending on your internet speed...
echo File size: ~30 MB
echo.

powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $ProgressPreference = 'SilentlyContinue'; Write-Host 'Downloading from: %NODE_DOWNLOAD_URL%'; Invoke-WebRequest -Uri '%NODE_DOWNLOAD_URL%' -OutFile '%MSI_FILE%'; if ($?) {Write-Host '✓ Download complete'} else {Write-Host '❌ Download failed'; exit 1}}"

if not exist "%MSI_FILE%" (
    echo.
    echo ❌ ERROR: Download failed!
    echo.
    echo Please check your internet connection and try again.
    echo Or download manually from: https://nodejs.org/
    echo.
    pause
    exit /b 1
)

echo.
echo ✓ Node.js installer downloaded successfully
echo   File: %MSI_FILE%
echo   Size: 
dir "%MSI_FILE%" | find ".msi"
echo.

REM Install Node.js silently
echo [Step 3/4] Installing Node.js...
echo.
echo Installation mode: SILENT (no popups)
echo This will take 2-3 minutes...
echo.
echo Installing to: C:\Program Files\nodejs
echo Log file: %LOG_FILE%
echo.

REM Run MSI installer with full silent mode and logging
msiexec.exe /i "%MSI_FILE%" /quiet /norestart /log "%LOG_FILE%" ADDLOCAL=ALL

echo.
echo Waiting for installation to complete...
timeout /t 5 /nobreak >nul

REM Check if installation succeeded
if exist "C:\Program Files\nodejs\node.exe" (
    echo ✓ Node.js executable found
) else (
    echo ❌ Installation may have failed
    echo.
    echo Checking log file for errors...
    if exist "%LOG_FILE%" (
        echo.
        echo Last 20 lines of installation log:
        powershell -Command "Get-Content '%LOG_FILE%' | Select-Object -Last 20"
    )
    echo.
    echo Please try installing manually from: https://nodejs.org/
    pause
    exit /b 1
)

REM Refresh environment variables
echo.
echo [Step 4/4] Configuring environment...
echo.
echo DEBUG: Current PATH variable
echo PATH=%PATH%
echo.
echo DEBUG: Adding Node.js to current session PATH
REM Add Node.js to PATH for current session
set "PATH=%PATH%;C:\Program Files\nodejs"
echo.
echo DEBUG: New PATH variable
echo PATH=%PATH%
echo.

REM Verify installation
echo Testing Node.js installation...
echo.
echo Checking if Node.js executable exists...

if exist "C:\Program Files\nodejs\node.exe" (
    echo ✓ Found: C:\Program Files\nodejs\node.exe
) else (
    echo ❌ NOT FOUND: C:\Program Files\nodejs\node.exe
    echo.
    echo Installation failed - executable not found.
    echo.
    echo Please try:
    echo   1. Manual installation from https://nodejs.org/
    echo   2. Check Windows Defender/Antivirus logs
    echo   3. Check installation log: %LOG_FILE%
    echo.
    pause
    exit /b 1
)

echo Testing node command...
"C:\Program Files\nodejs\node.exe" -v
if errorlevel 1 (
    echo ❌ Node.js command failed
    echo.
    pause
    exit /b 1
) else (
    echo ✓ Node.js command works
)

echo.
echo Testing npm command...
if exist "C:\Program Files\nodejs\npm.cmd" (
    echo ✓ Found: C:\Program Files\nodejs\npm.cmd
    "C:\Program Files\nodejs\npm.cmd" -v
    if errorlevel 1 (
        echo ❌ npm command failed
        echo.
        pause
        exit /b 1
    ) else (
        echo ✓ npm command works
    )
) else (
    echo ❌ npm.cmd not found
    echo.
    pause
    exit /b 1
)

echo.
echo ════════════════════════════════════════════════════════════
echo ✓ ALL TESTS PASSED
echo ════════════════════════════════════════════════════════════
echo.
echo Node.js Version:
"C:\Program Files\nodejs\node.exe" -v
echo.
echo npm Version:
"C:\Program Files\nodejs\npm.cmd" -v
echo.

REM Clean up
echo Cleaning up temporary files...
del "%MSI_FILE%" 2>nul
del "%LOG_FILE%" 2>nul
rmdir /Q "%TEMP_DIR%" 2>nul
echo ✓ Cleanup complete
echo.

echo ╔════════════════════════════════════════════════════════════╗
echo ║               INSTALLATION COMPLETE!                       ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo Node.js v%NODE_VERSION% has been installed successfully!
echo.
echo Installation location: C:\Program Files\nodejs
echo.
echo ════════════════════════════════════════════════════════════
echo IMPORTANT: RESTART REQUIRED
echo ════════════════════════════════════════════════════════════
echo.
echo To use Node.js in Command Prompt:
echo.
echo 1. CLOSE ALL Command Prompt windows
echo 2. Open a NEW Command Prompt
echo 3. Test with: node -v
echo 4. Test with: npm -v
echo 5. Run: start-project.bat
echo.
echo The PATH will be updated automatically after restart.
echo.
echo ════════════════════════════════════════════════════════════
echo.
echo Press any key to close this window...
pause >nul
exit /b 0
