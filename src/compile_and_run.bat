@echo off
echo Compiling Java source files (packages: interfaces, model, db, service, main)...
javac interfaces/*.java model/*.java db/*.java service/*.java main/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Executing...
java main.Main
pause
