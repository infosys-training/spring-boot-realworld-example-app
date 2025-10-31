@echo off
REM Startup script for RealWorld Spring Boot + Next.js application (Windows)
REM This script starts both the backend (Spring Boot) and frontend (Next.js) concurrently

echo ======================================
echo Starting RealWorld Application
echo ======================================
echo.
echo Backend will run on: http://localhost:8080
echo Frontend will run on: http://localhost:3000
echo.
echo Close the terminal windows to stop the applications
echo.

REM Start backend in a new window
echo Starting backend (Spring Boot)...
echo --------------------------------------
start "RealWorld Backend (Spring Boot)" cmd /c "gradlew.bat bootRun"

REM Give backend a moment to start
timeout /t 3 /nobreak >nul

REM Start frontend in a new window
echo.
echo Starting frontend (Next.js)...
echo --------------------------------------
start "RealWorld Frontend (Next.js)" cmd /c "cd frontend && npm install && npm run dev"

echo.
echo ======================================
echo Both applications are starting...
echo ======================================
echo.
echo Backend window: "RealWorld Backend (Spring Boot)"
echo Frontend window: "RealWorld Frontend (Next.js)"
echo.
echo Close this window or press any key to exit
echo (Note: This will NOT stop the applications)
pause
