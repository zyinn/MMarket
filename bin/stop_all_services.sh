#!/bin/sh
source ./deploy.config

echo "-----------Stop tomcat service-----------------------------------------"`date "+%Y-%m-%d %H:%M:%S"` >> ${deploy_log}
./stop_service_tomcat.sh
sleep 2
echo "stop_all_service done!" >> ${deploy_log}
