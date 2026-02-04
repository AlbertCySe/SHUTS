@echo off
color 0C
title Maven Auto-Installer for Windows

cls
echo ╔════════════════════════════════════════════════════════════╗
echo ║              MAVEN AUTO-INSTALLER                          ║
echo ║         Smart Highway Tolling System Helper                ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo This script will download and install Apache Maven 3.9.6
echo.

REM Check if running as administrator
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠ WARNING: Not running as Administrator
    echo For best results, right-click this file and select "Run as Administrator"
    echo.
    echo Continue anyway? (Installation may fail without admin rights)
    pause
)

REM Check Java first
echo [Step 1/5] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo.
    echo ❌ ERROR: Java is not installed!
    echo.
    echo Maven requires Java. Please install Java 17+ first:
    echo Download: https://adoptium.net/
    echo.
    pause
    exit /b 1
)
echo ✓ Java is installed
echo.

REM Set Maven version and URLs
set MAVEN_VERSION=3.9.5
set MAVEN_DOWNLOAD_URL=https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
set INSTALL_DIR=C:\Program Files\Apache\maven
set TEMP_DIR=%TEMP%\maven-install
set ZIP_FILE=%TEMP_DIR%\maven.zip

echo [Step 2/5] Preparing download...
echo Maven Version: %MAVEN_VERSION%
echo Install Location: %INSTALL_DIR%
echo.

REM Create temp directory
if not exist "%TEMP_DIR%" mkdir "%TEMP_DIR%"

REM Download Maven
echo [Step 3/5] Downloading Maven...
echo This may take 2-3 minutes depending on your internet speed...
echo.
echo URL: %MAVEN_DOWNLOAD_URL%
echo.

powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $ProgressPreference = 'SilentlyContinue'; Invoke-WebRequest -Uri '%MAVEN_DOWNLOAD_URL%' -OutFile '%ZIP_FILE%'; if ($?) {Write-Host '✓ Download complete'} else {Write-Host '❌ Download failed'; exit 1}}"

if not exist "%ZIP_FILE%" (
    echo.
    echo ❌ ERROR: Download failed!
    echo.
    echo Please check your internet connection and try again.
    echo Or download manually from: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

echo ✓ Maven downloaded successfully
echo.

REM Extract Maven
echo [Step 4/5] Extracting Maven...
powershell -Command "& {Expand-Archive -Path '%ZIP_FILE%' -DestinationPath '%TEMP_DIR%' -Force; if ($?) {Write-Host '✓ Extraction complete'} else {Write-Host '❌ Extraction failed'; exit 1}}"

if errorlevel 1 (
    echo.
    echo ❌ ERROR: Extraction failed!
    pause
    exit /b 1
)

REM Move to installation directory
echo.
echo Creating installation directory...
if not exist "%INSTALL_DIR%\.." mkdir "%INSTALL_DIR%\.."

echo Moving Maven to: %INSTALL_DIR%
if exist "%INSTALL_DIR%" (
    echo Removing old Maven installation...
    rmdir /S /Q "%INSTALL_DIR%"
)

move "%TEMP_DIR%\apache-maven-%MAVEN_VERSION%" "%INSTALL_DIR%" >nul

if not exist "%INSTALL_DIR%\bin\mvn.cmd" (
    echo.
    echo ❌ ERROR: Installation failed!
    echo Maven files not found at expected location.
    pause
    exit /b 1
)

echo ✓ Maven installed to: %INSTALL_DIR%
echo.

REM Clean up temp files
echo Cleaning up temporary files...
rmdir /S /Q "%TEMP_DIR%" 2>nul
echo ✓ Cleanup complete
echo.

REM Add to PATH
echo [Step 5/5] Configuring PATH...
echo.
echo Maven needs to be added to your system PATH.
echo.
echo ════════════════════════════════════════════════════════════
echo OPTION 1: Automatic PATH Configuration (Requires Admin)
echo ════════════════════════════════════════════════════════════
echo.
choice /C YN /M "Add Maven to PATH automatically now? (Y/N)"

if errorlevel 2 goto :manual_path
if errorlevel 1 goto :auto_path

:auto_path
echo.
echo Adding Maven to system PATH...
setx /M PATH "%PATH%;%INSTALL_DIR%\bin" >nul 2>&1
if errorlevel 1 (
    echo ❌ Failed to set PATH automatically (requires admin rights)
    goto :manual_path
) else (
    echo ✓ Maven added to PATH successfully!
    echo.
    echo ⚠ IMPORTANT: You must RESTART your terminal/command prompt
    echo    for the PATH changes to take effect.
    goto :verify
)

:manual_path
echo.
echo ════════════════════════════════════════════════════════════
echo OPTION 2: Manual PATH Configuration
echo ════════════════════════════════════════════════════════════
echo.
echo To add Maven to PATH manually:
echo.
echo 1. Press Windows + R
echo 2. Type: sysdm.cpl
echo 3. Click "Environment Variables"
echo 4. Under "System variables", find "Path"
echo 5. Click "Edit"
echo 6. Click "New"
echo 7. Add this line: %INSTALL_DIR%\bin
echo 8. Click OK on all windows
echo 9. Restart your terminal/command prompt
echo.
pause

:verify
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                 INSTALLATION COMPLETE!                     ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo Maven Version: %MAVEN_VERSION%
echo Installed at: %INSTALL_DIR%
echo.
echo ════════════════════════════════════════════════════════════
echo NEXT STEPS:
echo ════════════════════════════════════════════════════════════
echo.
echo 1. CLOSE this window
echo 2. RESTART your Command Prompt or PowerShell
echo 3. Test Maven by running: mvn -v
echo 4. Run start-project.bat again
echo.
echo ════════════════════════════════════════════════════════════
echo.

REM Try to verify if PATH is already set (won't work in same session)
echo Attempting to verify installation...
"%INSTALL_DIR%\bin\mvn.cmd" -v >nul 2>&1
if not errorlevel 1 (
    echo.
    echo ✓ Maven is working! Version check:
    "%INSTALL_DIR%\bin\mvn.cmd" -v
) else (
    echo.
    echo ⚠ Maven installed but not yet in PATH
    echo   Close this window and restart Command Prompt to use Maven
)

echo.
echo Press any key to close this window...
pause >nul
exit /b 0
