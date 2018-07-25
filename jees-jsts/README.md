# jees-jsts

## 内容介绍
本工具基于Spring和Netty实现Socket服务器，目前支持Socket和WebSocket这2种格式。WebSocket还有待完善。
## 基本用法
在pom.xml中加入下面2段内容。
```
<parent>
	<groupId>com.github.aiyoyoyo</groupId>
	<artifactId>jees-boot</artifactId>
	<version>1.1.4-SNAPSHOT</version>
</parent>
```
```
<dependency>
	<groupId>com.github.aiyoyoyo</groupId>
	<artifactId>jees-jsts</artifactId>
	<version>${jees.jsts.version}</version>
</dependency>
```
## 配置文件说明
* 核心配置
> application.yml
```
jees:
 jsts:
  socket:
   standTime: 60000
   port: 8000
  websocket:
   url: /ws
   standTime: 60000
   port: 8001
```
* 日志配置
> log4j2.xml
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

