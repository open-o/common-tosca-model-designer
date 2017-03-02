@REM
@REM Copyright 2017 ZTE Corporation.
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
set RUNHOME=%~dp0
title catalog-service
set allparam=
:param
set str=%1
if "%str%"=="" (
    goto end
)
set allparam=%allparam% %str%
shift /0
goto param

:end
@REM echo ### RUNHOME: %RUNHOME%
@REM echo ### Starting catalog-service
set main_path=%RUNHOME%..\
@REM cd /d %main_path%
set JAVA="%JAVA_HOME%\bin\java.exe"
set port=8312
set jvm_opts=-Xms50m -Xmx128m
@REM set jvm_opts=%jvm_opts% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=%port%,server=y,suspend=n
set class_path=%main_path%;%main_path%modeldesigner-verification-plugin.jar
@REM echo ### jvm_opts: %jvm_opts%
@REM echo ### class_path: %class_path%
%JAVA% -classpath %class_path% %jvm_opts% org.openo.commontosca.catalog.verification.mdserver.cmd.CmdMain %allparam%

