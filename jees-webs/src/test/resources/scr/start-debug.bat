CHCP 65001
echo "启动测试服务器"
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar jees-webs-1.5.0-SNAPSHOT.jar --spring.profiles.active=debug