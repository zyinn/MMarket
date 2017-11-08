#!/bin/sh
source ./deploy.config

cd ${app_dir}/web/logs
for ((i=1;i<=10;i++)); do 
		if [ $i -eq 10 ]; then
			echo "----------Deploy Failed !----------------------"`date "+%Y-%m-%d %H:%M:%S"` >> ${deploy_log}
			exit
		else
			LAUNCH_SIGNAL=`cat catalina.out | grep "Deployment of web application directory ${app_dir}/apps/money_market has finished"` >> ${deploy_log}
			LAUNCH_SIGNAL_2=`cat catalina.out | grep "Deployment of web application archive ${app_dir}/apps/money_market.war has finished"` >> ${deploy_log}
			if [ "x$LAUNCH_SIGNAL$LAUNCH_SIGNAL_2" = "x" ]; then
				sleep 10
			else
				echo "----------Deploy Successfully !--------------------------------------"`date "+%Y-%m-%d %H:%M:%S"` >> ${deploy_log}
				break
			fi
		fi
done
