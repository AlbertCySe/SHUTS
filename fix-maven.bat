@echo off
SETLOCAL EnableDelayedExpansion
color 0E
title Maven Auto-Fixer

cls
echo ╔════════════════════════════════════════════════════════════╗
echo ║          MAVEN AUTO-FIXER - For Students                  ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo This tool will automatically fix the Maven issue.
echo Just follow the simple steps below!
echo.
timeout /t 2 /nobreak >nul

REM Check if Maven already works
echo [Step 1/3] Checking if Maven is already working...
call mvn -v >nul 2>&1
if %errorlevel% EQU 0 (
    echo ✓ Maven is already working! No fix needed.
    echo.
    echo You can now run start-project.bat
    pause
    exit /b 0
)

echo Maven needs to be set up.
echo.

REM Check if we can download (test internet connectivity)
echo [Step 2/3] Testing internet connection...
powershell -Command "try { $null = Invoke-WebRequest -Uri 'https://www.google.com' -UseBasicParsing -TimeoutSec 5; exit 0 } catch { exit 1 }" >nul 2>&1
if %errorlevel% NEQ 0 (
    goto :no_internet
)

echo ✓ Internet connection OK
echo.

REM Download and install Maven automatically
echo [Step 3/3] Downloading Maven automatically...
echo.
echo This will take 2-3 minutes. Please wait...
echo.

REM Create a temporary PowerShell script to download with progress
set "MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
set "MAVEN_DIR=C:\maven-portable"
set "TEMP_ZIP=%TEMP%\maven.zip"

echo Downloading Maven from Apache...
powershell -Command "$ProgressPreference = 'SilentlyContinue'; Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%TEMP_ZIP%' -UseBasicParsing"

if not exist "%TEMP_ZIP%" (
    goto :download_failed
)

echo ✓ Download complete!
echo.
echo Extracting Maven...

REM Extract Maven
powershell -Command "Expand-Archive -Path '%TEMP_ZIP%' -DestinationPath '%MAVEN_DIR%' -Force"

REM Find the extracted folder
for /d %%i in ("%MAVEN_DIR%\apache-maven-*") do set "MAVEN_HOME=%%i"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    goto :extract_failed
)

echo ✓ Maven extracted successfully!
echo.

REM Update the project to use this Maven
echo Configuring your project to use Maven...

REM Create a batch file to set MAVEN_HOME for this project
(
    echo @echo off
    echo set "MAVEN_HOME=%MAVEN_HOME%"
    echo set "PATH=%%MAVEN_HOME%%\bin;%%PATH%%"
) > "%~dp0..\maven-env.bat"

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                  ✓ SUCCESS!                                ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo Maven has been installed and configured!
echo Location: %MAVEN_HOME%
echo.
echo You can now run start-project.bat
echo.
pause
exit /b 0

:no_internet
cls
echo ╔════════════════════════════════════════════════════════════╗
echo ║          OFFLINE MODE - Manual Setup Required             ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo Your computer cannot connect to the internet right now.
echo.
echo ════════════════════════════════════════════════════════════
echo   EASY FIX FOR STUDENTS - Follow these 3 simple steps:
echo ════════════════════════════════════════════════════════════
echo.
echo Step 1: Download Maven on a computer with internet
echo   → Open this link on ANY computer with internet:
echo   → https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip
echo   → Copy the downloaded file to a USB drive
echo.
echo Step 2: Copy to this computer
echo   → Plug the USB drive into THIS computer
echo   → Copy the downloaded ZIP file to your Desktop
echo.
echo Step 3: Run the Auto-Installer
echo   → Double-click: install-maven-offline.bat
echo   → It will handle everything automatically!
echo.
echo ════════════════════════════════════════════════════════════
echo.
echo Press any key to close this window...
pause >nul
exit /b 1

:download_failed
echo.
echo ❌ Download failed.
echo.
echo This might be because:
echo   - Your school/college firewall is blocking downloads
echo   - Internet connection is unstable
echo.
echo → Try the OFFLINE method above
echo.
pause
exit /b 1

:extract_failed
echo.
echo ❌ Extraction failed.
echo.
echo → Try the OFFLINE method or contact your instructor
echo.
pause
exit /b 1
