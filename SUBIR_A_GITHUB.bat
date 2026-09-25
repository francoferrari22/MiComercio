@echo off
setlocal EnableExtensions EnableDelayedExpansion
title Mi Comercio - Subir a GitHub
cd /d "%~dp0"

cls
echo.
echo ==========================================
echo       MI COMERCIO - SUBIR A GITHUB
echo       Solo este proyecto Android
echo ==========================================
echo.
echo Pegue UNICAMENTE el LINK COMPLETO del repositorio de GitHub.
echo Ejemplo: https://github.com/francoferrari22/MiComercio
 echo.
set "REMOTE="
set /p "REMOTE=Link del repositorio: "

if not defined REMOTE (
  echo.
  echo Debes indicar el link del repositorio.
  pause
  exit /b 1
)

rem Limpiar comillas y espacios accidentales.
set "REMOTE=!REMOTE:\"=!"
set "REMOTE=!REMOTE: =!"

rem Aceptar HTTPS o SSH y convertir todo a HTTPS.
set "INPUT=!REMOTE!"
set "REMOTE="

if /I "!INPUT:~0,19!"=="https://github.com/" set "REMOTE=!INPUT!"
if /I "!INPUT:~0,18!"=="http://github.com/" set "REMOTE=https://github.com/!INPUT:~18!"
if /I "!INPUT:~0,15!"=="git@github.com:" set "REMOTE=https://github.com/!INPUT:~15!"

if not defined REMOTE (
  echo.
  echo Link no valido. Debe ser un link de GitHub.
  pause
  exit /b 1
)

rem Normalizar .git y la barra final.
if /I "!REMOTE:~-4!"==".git" set "REMOTE=!REMOTE:~0,-4!"
if "!REMOTE:~-1!"=="/" set "REMOTE=!REMOTE:~0,-1!"
set "REMOTE=!REMOTE!.git"

rem Inicializar Git local.
if not exist ".git" git init
if errorlevel 1 goto :git_error

git config user.name "francoferrari22"
git config user.email "francoferrari22@users.noreply.github.com"
git branch -M main

git add .
if errorlevel 1 goto :git_error

git diff --cached --quiet
if errorlevel 1 (
  git commit -m "Mi Comercio Android - version actual"
  if errorlevel 1 goto :git_error
) else (
  echo.
  echo No hay cambios nuevos para confirmar.
)

git remote remove origin >nul 2>&1
git remote add origin "!REMOTE!"
if errorlevel 1 goto :git_error

echo.
echo Repositorio destino:
echo !REMOTE!
echo.
echo Subiendo Mi Comercio a GitHub...
echo.

rem Descargar la referencia remota si el repositorio ya existe.
rem No se hace pull/rebase: esta carpeta es la version que debe quedar en el repositorio.
git fetch origin main >nul 2>&1

rem Forzar main para evitar el error "fetch first / non-fast-forward".
rem Esto hace que el contenido de ESTE proyecto sea el contenido de main del repositorio.
git push -u origin main --force
if not errorlevel 1 goto :ok

rem Si el repositorio todavia no existe y GitHub CLI esta instalado y autenticado,
rem intentar crearlo automaticamente usando el mismo link proporcionado.
where gh >nul 2>&1
if errorlevel 1 goto :push_error

gh auth status >nul 2>&1
if errorlevel 1 goto :push_error

for /f "tokens=4 delims=/" %%A in ("!REMOTE!") do set "GH_OWNER=%%A"
for /f "tokens=5 delims=/" %%A in ("!REMOTE!") do set "GH_REPO=%%A"
set "GH_REPO=!GH_REPO:.git=!"

if not defined GH_REPO goto :push_error

echo.
echo El repositorio no existe o GitHub rechazo el acceso.
echo Intentando crearlo automaticamente...
gh repo create "!GH_OWNER!/!GH_REPO!" --private --source=. --remote=origin --push
if not errorlevel 1 goto :ok

goto :push_error

:git_error
echo.
echo ==========================================
echo ERROR DE GIT
echo ==========================================
echo.
echo No se pudo preparar el proyecto para subirlo.
pause
exit /b 1

:push_error
echo.
echo ==========================================
echo NO SE PUDO SUBIR EL PROYECTO
echo ==========================================
echo.
echo El proyecto quedo preparado correctamente.
echo.
echo Si GitHub pide autenticacion, completa el inicio de sesion de GitHub
echo en esta PC y vuelve a ejecutar este mismo BAT.
echo.
pause
exit /b 1

:ok
echo.
echo ==========================================
echo PROYECTO SUBIDO CORRECTAMENTE
echo ==========================================
echo.
echo !REMOTE!
echo.
pause
exit /b 0
