@echo off

set RUNHOME=%~dp0%
echo ### RUNHOME: %RUNHOME%

call "%RUNHOME%/setenv.bat" 

echo ================== ENV_INFO  =============================================
echo RUNHOME=%RUNHOME%
echo JAVA_BASE=%JAVA_BASE%
echo Main_JAR=%Main_JAR%
echo APP_INFO=%APP_INFO%
echo ==========================================================================

title stopping %APP_INFO%
echo ### Stopping %APP_INFO%

cd /d %RUNHOME%

for /f "delims=" %%i in ('%JAVA_HOME%\bin\jcmd') do (
  call find_kill_process "%%i" %Main_JAR% %RUNHOME%
)

