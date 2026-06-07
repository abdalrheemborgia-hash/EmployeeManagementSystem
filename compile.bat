@echo off
echo ============================================
echo  Employee Management System - Build Script
echo ============================================

:: Create output directory
if not exist "out" mkdir out

:: Compile all Java files (Windows)
echo Compiling Java source files...
javac -cp "lib\sqlite-jdbc-3.47.1.0.jar" -d out -sourcepath src src\Main.java src\model\*.java src\exception\*.java src\database\*.java src\gui\*.java

if %errorlevel% neq 0 (
    echo.
    echo BUILD FAILED. Check error messages above.
    pause
    exit /b 1
)

echo.
echo BUILD SUCCESSFUL!
echo.
echo To run the application:
echo   java -cp "out;lib\sqlite-jdbc-3.47.1.0.jar" Main
echo.
pause
