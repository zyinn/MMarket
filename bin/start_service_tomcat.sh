#!/bin/sh
source ./deploy.config
source /etc/profile
export CATALINA_HOME=${CATALINA_HOME}
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}
export CATALINA_BASE=${CATALINA_BASE}
export CATALINA_OPTS=${CATALINA_OPTS}

chmod -R 777 ${CATALINA_HOME}/bin
${CATALINA_HOME}/bin/startup.sh
sleep 2
