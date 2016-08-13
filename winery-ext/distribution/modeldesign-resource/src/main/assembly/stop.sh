#!/bin/bash

DIRNAME=`dirname $0`
RUNHOME=`cd $DIRNAME/; pwd`
echo @RUNHOME@ $RUNHOME

if [ -f "$RUNHOME/setenv.sh" ]; then
  . "$RUNHOME/setenv.sh"
else
echo "can not found $RUNHOME/setenv.sh"
fi

echo ================== $APP_INFO  =============================================
echo RUNHOME=$RUNHOME
echo Main_JAR=$Main_JAR
echo ===============================================================================
cd $RUNHOME; pwd


function save_app_pid(){
	app_pid=`ps -ef | grep $Main_JAR | grep $RUNHOME | grep -v grep | awk '{print $2}'`
	echo @PID@ $APP_INFO $app_pid
}

function kill_app_process(){
	ps -p $app_pid
	if [ $? == 0 ]; then
		kill -9 $app_pid
	fi
}

save_app_pid;
echo @C_CMD@ kill -9 $app_pid
kill_app_process;