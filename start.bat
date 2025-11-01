@echo off
setlocal enabledelayedexpansion

echo [INFO] Starting RealWorld Example App...
echo.

REM Check Node.js version (important-comment)
echo [INFO] Checking Node.js version...
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed. Please install Node.js version 14-16.
    exit /b 1
)

for /f "tokens=1 delims=." %%a in ('node -v') do set NODE_MAJOR=%%a
set NODE_MAJOR=%NODE_MAJOR:v=%

if %NODE_MAJOR% lss 14 (
    echo [ERROR] Node.js version %NODE_MAJOR% is not supported. Required: 14-16
    echo [INFO] If you have nvm installed, run: cd frontend ^&^& nvm use
    exit /b 1
)

if %NODE_MAJOR% gtr 16 (
    echo [ERROR] Node.js version %NODE_MAJOR% is not supported. Required: 14-16
    echo [INFO] If you have nvm installed, run: cd frontend ^&^& nvm use
    exit /b 1
)

echo [SUCCESS] Node.js version %NODE_MAJOR% detected
echo.

REM Check and install frontend dependencies (important-comment)
if not exist "frontend\node_modules" (
    echo [INFO] Frontend dependencies not found. Installing...
    cd frontend
    call npm install
    cd ..
    echo [SUCCESS] Frontend dependencies installed
    echo.
) else (
    echo [INFO] Frontend dependencies already installed
    echo.
)

REM Start backend (important-comment)
echo [INFO] Starting backend (Spring Boot) on port 8080...
start "Spring Boot Backend" gradlew.bat bootRun
echo [INFO] Backend starting in separate window...
echo.

REM Wait for backend to be ready (important-comment)
echo [INFO] Waiting for backend to be ready...
set MAX_ATTEMPTS=60
set ATTEMPT=0

:wait_backend
if %ATTEMPT% geq %MAX_ATTEMPTS% (
    echo [ERROR] Backend failed to start within %MAX_ATTEMPTS% seconds
    echo [INFO] Check the backend window for details
    exit /b 1
)

powershell -Command "try { Invoke-WebRequest -Uri 'http://localhost:8080/tags' -UseBasicParsing -TimeoutSec 1 | Out-Null; exit 0 } catch { exit 1 }" >nul 2>&1
if %errorlevel% equ 0 (
    goto backend_ready
)

set /a ATTEMPT=%ATTEMPT%+1
echo [INFO] Waiting for backend... (%ATTEMPT%/%MAX_ATTEMPTS% seconds)
timeout /t 1 /nobreak >nul
goto wait_backend

:backend_ready
echo [SUCCESS] Backend is ready!
echo.

REM Start frontend (important-comment)
echo [INFO] Starting frontend (Next.js) on port 3000...
start "Next.js Frontend" cmd /c "cd frontend && npx next dev"
echo [INFO] Frontend starting in separate window...
echo.

echo ========================================
echo [SUCCESS] Both services are running!
echo ========================================
echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:3000
echo.
echo [INFO] To stop the services, close the backend and frontend windows
echo [INFO] Or press Ctrl+C in each window
echo.
echo Press any key to exit this launcher window...
pause >nul
