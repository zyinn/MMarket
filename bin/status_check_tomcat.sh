#!/bin/sh
source ./deploy.config

cd ${app_weblogs_dir}
for ((i=1;i<=10;i++)); do 
		if [ $i -eq 10 ]; then
			echo "-----------Failed to start tomcat services，please check log!---------------"`date "+%Y-%m-%d %H:%M:%S"` >> ${deploy_log}
			exit
		else
			LAUNCH_SIGNAL=`cat catalina.out | grep "Deployment of web application directory ${app_dir}/apps/money_market has finished"`
			cat catalina.out | grep "Deployment of web application directory ${app_dir}/apps/money_market has finished"
			LAUNCH_SIGNAL_2=`cat catalina.out | grep "Deployment of web application archive ${app_war} has finished"`
			if [ "x$LAUNCH_SIGNAL$LAUNCH_SIGNAL_2" = "x" ]; then
				sleep 10
			else
				echo "-----------Succeed to start tomcat services-----------------------------"`date "+%Y-%m-%d %H:%M:%S"` >> ${deploy_log}
				break
			fi
		fi
done
