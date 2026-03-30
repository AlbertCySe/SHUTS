@echo off
SETLOCAL EnableDelayedExpansion
color 0A
title Maven Offline Installer

cls
echo ╔════════════════════════════════════════════════════════════╗
echo ║       MAVEN OFFLINE INSTALLER - For Students              ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo This will install Maven from the ZIP file you downloaded.
echo.

REM Check Desktop for Maven ZIP
set "MAVEN_ZIP="
for %%f in ("%USERPROFILE%\Desktop\apache-maven-*.zip") do set "MAVEN_ZIP=%%f"

if not defined MAVEN_ZIP (
    echo ❌ Maven ZIP file not found on Desktop
    echo.
    echo Please make sure you:
    echo   1. Downloaded: apache-maven-3.9.6-bin.zip
    echo   2. Placed it on your Desktop
    echo.
    echo Then run this script again.
    pause
    exit /b 1
)

echo ✓ Found Maven ZIP: %MAVEN_ZIP%
echo.

set "MAVEN_DIR=C:\maven-portable"
echo Installing Maven to: %MAVEN_DIR%
echo.

REM Extract
echo Extracting... Please wait...
powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_DIR%' -Force"

REM Find extracted folder
for /d %%i in ("%MAVEN_DIR%\apache-maven-*") do set "MAVEN_HOME=%%i"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo ❌ Installation failed
    pause
    exit /b 1
)

echo ✓ Maven installed successfully!
echo.

REM Create environment script
(
    echo @echo off
    echo set "MAVEN_HOME=%MAVEN_HOME%"
    echo set "PATH=%%MAVEN_HOME%%\bin;%%PATH%%"
) > "%~dp0maven-env.bat"

echo ╔════════════════════════════════════════════════════════════╗
echo ║                  ✓ SUCCESS!                                ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo Maven is now installed!
echo You can now run start-project.bat
echo.
pause
exit /b 0
