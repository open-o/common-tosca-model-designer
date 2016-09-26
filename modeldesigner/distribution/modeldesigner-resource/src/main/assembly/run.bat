@REM
@REM Copyright 2016 ZTE Corporation.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM     http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@echo off

set RUNHOME=%~dp0%
echo ### RUNHOME: %RUNHOME%

if exist "%RUNHOME%setenv.bat" (
  call "%RUNHOME%setenv.bat"
)


echo ================== ENV_INFO  =============================================
echo RUNHOME=%RUNHOME%
echo JAVA_BASE=%JAVA_BASE%
echo Main_Class=%Main_Class%
echo APP_INFO=%APP_INFO%
echo Main_JAR=%Main_JAR%
echo Main_Conf=%Main_Conf%
echo ==========================================================================

title %APP_INFO%
echo ### Starting %APP_INFO%

IF EXIST "%JAVA_BASE%" (
set JAVA_HOME=%JAVA_BASE%
)

set JAVA="%JAVA_HOME%/bin/java"
set CLASS_PATH=%RUNHOME%;%RUNHOME%%Main_JAR%
set JAVA_OPTS=-Xms50m -Xmx128m
set port=8787
set JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=%port%,server=y,suspend=n
set CATALINA_BASE=%RUNHOME%
set repo_dir=winery-repository
set repo_dir_init=winery-repository-init


echo ================== RUN_INFO  =============================================
echo ### JAVA_HOME: %JAVA_HOME%
echo ### JAVA: %JAVA%
echo ### jvm_opts: %JAVA_OPTS%
echo ### class_path: %CLASS_PATH%
echo ### httpPort: %httpPort%
echo ### CATALINA_BASE: %CATALINA_BASE%
echo ### repo_dir: %repo_dir%
echo ### repo_dir_init: %repo_dir_init%
echo ==========================================================================

cd /d %RUNHOME%



if not exist "%repo_dir%" (
  echo there is no %repo_dir% in %RUNHOME%
  mkdir %repo_dir%
)

if exist %repo_dir_init% (
echo found repository init directory %repo_dir_init% in %RUNHOME% ,copy it to  %repo_dir% ;
xcopy %repo_dir_init%\*.*  %repo_dir% /e/y 
)


echo @JAVA@ %JAVA% %JAVA_OPTS% -jar %RUNHOME%%Main_JAR%
echo @JAVA_CMD@
%JAVA% %JAVA_OPTS% -classpath %CLASS_PATH% -jar %RUNHOME%%Main_JAR% -httpPort %httpPort%