@echo off
echo =====================================================
echo  StoreAlgorithms - Compilation and Run Script
echo =====================================================

REM Check if JDBC driver exists
if not exist "mysql-connector-java-8.0.33.jar" (
    echo ❌ MySQL JDBC driver not found!
    echo 📥 Please download mysql-connector-java-8.0.33.jar
    echo 🔗 Download from: https://dev.mysql.com/downloads/connector/j/
    echo 📁 Place the JAR file in the project root directory
    pause
    exit /b 1
)

echo ✅ JDBC driver found
echo.

REM Clean previous compilation
echo 🧹 Cleaning previous compilation...
if exist "src\*.class" del /q "src\*.class"
if exist "src\**\*.class" del /q /s "src\**\*.class"

echo.
echo 🔨 Compiling Java files...

REM Compile all Java files
javac -cp ".;mysql-connector-java-8.0.33.jar" src\Main.java src\config\*.java src\engine\*.java src\models\*.java src\dsa\*.java src\dao\*.java src\commands\*.java src\utils\*.java

if %ERRORLEVEL% neq 0 (
    echo ❌ Compilation failed!
    echo 🔧 Please check for syntax errors and try again
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.

echo 🚀 Starting StoreAlgorithms...
echo 💡 Make sure MySQL is running and database is set up
echo.

REM Run the application
java -cp ".;mysql-connector-java-8.0.33.jar;src" Main

echo.
echo 👋 Application ended
pause
