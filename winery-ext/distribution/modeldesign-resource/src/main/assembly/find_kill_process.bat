echo #jcmd# %1
echo #RUNHOME# %3
echo #Main_JAR# %2

echo %1 | findstr %2 | findstr %3>NUL
echo ERRORLEVEL=%ERRORLEVEL%
IF ERRORLEVEL 1 goto findend
for /f "tokens=1" %%a in (%1) do (  
    echo kill %1
	taskkill /F /pid %%a
)
:findend
