#!/bin/sh
source ./deploy.config

echo "-----------Start tomcat service-----------------------------------------"`date "+%Y-%m-%d %H:%M:%S"` >> ${deploy_log}
./start_service_tomcat.sh
echo "-----------Start check tomcat service status----------------------------"`date "+%Y-%m-%d %H:%M:%S"` >> ${deploy_log}
./status_check_tomcat.sh