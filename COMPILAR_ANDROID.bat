@echo off
setlocal
title Mi Comercio - Compilar Android
cd /d "%~dp0"
echo.
echo ==========================================
echo       MI COMERCIO - ANDROID
echo       Compilacion exclusiva APK
echo ==========================================
echo.
if not exist "gradlew.bat" (
  echo ERROR: No se encontro Gradle Wrapper.
  pause
  exit /b 1
)
call gradlew.bat clean assembleRelease
if errorlevel 1 (
  echo.
  echo ERROR: La compilacion fallo.
  pause
  exit /b 1
)
echo.
echo APK generado:
echo %CD%\app\build\outputs\apk\release\app-release.apk
echo.
pause
endlocal
