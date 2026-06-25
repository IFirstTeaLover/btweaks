@rem Run this only after Brapi Library update or initial setup!

@echo off

cd /d D:\brapi
call gradlew build

if %errorlevel% neq 0 exit /b %errorlevel%

cd /d D:\btweaks
call gradlew runClient