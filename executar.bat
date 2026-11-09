@echo off
setlocal
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
  echo Erro: Java nao encontrado. Instale o JDK 21 ou superior.
  pause
  exit /b 1
)
call mvnw.cmd -U clean javafx:run %*
set "RESULT=%ERRORLEVEL%"
if not "%RESULT%"=="0" pause
exit /b %RESULT%
