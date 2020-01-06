export JAVA_OPTS="-Xmx1024m"
export CLASSPATH="$CATALINA_HOME/lib/log4j-api-2.11.2.jar:$CATALINA_HOME/lib/log4j-core-2.11.2.jar:$CATALINA_HOME/lib/log4j-jul-2.11.2.jar:$CATALINA_HOME/lib/log4j-slf4j-impl-2.11.2.jar:$CATALINA_HOME/lib/slf4j-api-1.7.21.jar"
export LOGGING_MANAGER="-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager -Dopenejb.log.factory=log4j -Dlog4j.configurationFile=$CATALINA_HOME/conf/log4j2.xml"
