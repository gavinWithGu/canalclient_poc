#!/bin/bash 

current_path=`pwd`
case "`uname`" in
    Linux)
                bin_abs_path=$(readlink -f $(dirname $0))
                ;;
        *)
                bin_abs_path=`cd $(dirname $0); pwd`
                ;;
esac

base=${bin_abs_path}/..
env_var=$2

pidfile=$base/bin/pid
classpath=$base/lib
java_file_path=$classpath/device-auth-server-1.0-SNAPSHOT.jar
conf_file_prefix_path=$base/conf

#Java args
JAVA_OPTS="-server -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCCause -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$base/gc/outofmem.hprof -XX:+UseGCLogFileRotation -Xloggc:$base/gc/$(date '+%Y%m%d%H%M%S').gc.log -XX:GCLogFileSize=200M -XX:NumberOfGCLogFiles=5 -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -Dclient.encoding.override=UTF-8 -Dfile.encoding=UTF-8"

#pid
#pid=`ps -ef |grep $JAR_FILE_NAME |grep -v grep |cut -c 9-15`  
#pid=`netstat -anpl |grep java|awk '{printf $7}'|cut -d/ -f1`

start(){  
   if [ -f $pidfile ]; then
        echo "Server started, Kill process, pid `cat $pidfile`"  
	kill -9 `cat $pidfile` 2>$base/logs/kill.log
	rm -rf $pidfile
   fi
echo "======$JAVA_FILE_NAME"
   echo $conf_file_prefix_path"/application-"$env_var".yml"

   nohup java -Xms512m -Xmx512m $JAVA_OPTS -jar $java_file_path --logging.config=$conf_file_prefix_path"/logback-spring.xml" --spring.config.location=$conf_file_prefix_path"/application-"$env_var".yml" --spring.profiles.active=$env_var &
   echo $! > $pidfile
   echo "start successful"  
}  
  
stop(){  
   #kill program
   if [ -f $pidfile ]; then
        echo "Kill pid `cat $pidfile`"
	kill -9 `cat $pidfile` 2>$base/logs/kill.log
	rm -rf $pidfile
   else
        echo "No program is running,skip stop."
   fi

}  

restart()  
{  
    echo "stoping ... "  
    stop  
    sleep 2
    echo "staring ..."  
    start  
}

status(){  
   if [ -f $pidfile ]; then
        echo "Program is running. Pid:  `cat $pidfile`"
   else
        echo "No program is running!"
   fi  
}  
 
case $1 in  
   start)  
      if [ $# != 2 ] ; then
	echo "Usage: ./run.sh start {dev|prod}"  
	exit 1;
      fi
      start  
   ;;  
   stop)  
      stop  
   ;;
   restart)  
      if [ $# != 2 ] ; then
	echo "Usage: ./run.sh restart {dev|prod}"  
	exit 1;
      fi
      restart  
    ;;
   status)  
      status  
   ;;  
   *)  
      echo "Usage: ./run.sh  {start|stop|restart|status}"  
   ;;  
esac  
  
exit 0 
