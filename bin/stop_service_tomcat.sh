#!/bin/bash
source ./deploy.config

PID=`ps -ef | grep "catalina.base=${app_dir}" | grep -v grep | awk '{print $2}'`
echo "Killing..." + ${PID}
kill ${PID}
echo "Done!"

