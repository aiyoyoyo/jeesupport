# jees-jsts

## 内容介绍
本工具基于Spring和Netty实现Socket服务器，目前支持Socket和WebSocket2中格式。WebSocket还有待完善。
## 基本用法
在pom.xml中加入下面2段内容。
```
<repository>
    <id>oss</id>
    <url>https://oss.sonatype.org/content/groups/public/</url>
    <releases>
        <enabled>true</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
        <checksumPolicy>fail</checksumPolicy>
    </snapshots>
</repository>
```
```
<dependency>
	<groupId>com.github.aiyoyoyo</groupId>
	<artifactId>jees-jsts</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
## 配置文件说明
* 环境配置
> define.cfg
```
#Netty Socket
#socket服务使用的端口
socket.port1	= 8001 
#Netty WebSocket
#websocket服务使用的端口
websocket.port1 = 8002
```
* 日志配置
> log4j2.xml
* 核心文件配置
> jees-core-dispatcher.xml
核心配置主要声明启动时的配置，使用socket服务或使用websocket服务，可以根据需要自行调整。
```
<!-- 使用Netty作为Socket服务器  -->
<context:component-scan base-package="com.jees.jsts.netty.*" />
<!-- 使用Netty作为WebSocket服务器  -->
<context:component-scan base-package="com.jees.jsts.websocket.*" />
```
## 程序实现Demo
```
// 启动服务
public static void main( String[] args ) {  
    new ClassPathXmlApplicationContext( ICommonConfig.CFG_DEFAULT ) ;
    CommonContextHolder.getBean( ISupportSocket.class ).onload();
    CommonContextHolder.getBean( ISupportWebSocket.class ).onload();
}
```
## 其他
后续内容请关注JEES讨论群或者论坛（未开放）  
QQ群：8802330  
论坛：[http://www.jeesupport.com](http://www.jeesupport.com)

