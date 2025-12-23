@echo off
cd /d "%~dp0"
echo ===============================
echo   QUIZ APPLICATION
echo ===============================
echo.

echo Step 1: Compiling Java files...
javac -cp "src;lib\mysql-connector-j-9.5.0.jar" src\org\example\*.java

echo.
echo Step 2: Starting application...
java -cp "src;lib\mysql-connector-j-9.5.0.jar" org.example.Main

echo.
pause