#!/bin/sh
#
# Copyright 2016 ZTE Corporation.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#



DIRNAME=`dirname $0`
RUNHOME=`cd $DIRNAME/; pwd`
echo @RUNHOME@ $RUNHOME


if [ -f "$RUNHOME/setenv.sh" ]; then
  . "$RUNHOME/setenv.sh"
else
echo "can not found $RUNHOME/setenv.sh"
fi

echo ================== ENV_INFO  =============================================
echo @RUNHOME@  $RUNHOME
echo @JAVA_BASE@  $JAVA_BASE
echo @Main_Class@  $Main_Class
echo @APP_INFO@  $APP_INFO
echo @Main_JAR@  $Main_JAR
echo ==========================================================================

echo start $APP_INFO ...

JAVA="$JAVA_HOME/bin/java"
JAVA_OPTS="-Xms50m -Xmx128m"
port=8787
JAVA_OPTS="$JAVA_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$port,server=y,suspend=n"
export CATALINA_BASE=$RUNHOME
repo_dir=winery-repository
repo_dir_init=winery-repository-init

echo ================== RUN_INFO  =============================================
echo @JAVA_HOME@ $JAVA_HOME
echo @JAVA@ $JAVA
echo @JAVA_OPTS@ $JAVA_OPTS
echo @httpPort@ $httpPort
echo @CATALINA_BASE@ $CATALINA_BASE
echo @repo_dir@ $repo_dir
echo @repo_dir_init@ $repo_dir_init
echo ==========================================================================

cd $RUNHOME

if [ ! -d "$repo_dir" ]; then 
echo there is no $repo_dir in $RUNHOME
mkdir "$repo_dir" 
fi 

if [ -d "$repo_dir_init" ]; then 
echo found repository init directory $repo_dir_init in $RUNHOME,copy it to $repo_dir;
\cp -rf $repo_dir_init/* $repo_dir
fi 


echo @JAVA@ "$JAVA" $JAVA_OPTS -jar $RUNHOME/$Main_JAR
echo @JAVA_CMD@
"$JAVA" $JAVA_OPTS -jar $RUNHOME/$Main_JAR -httpPort $httpPort

