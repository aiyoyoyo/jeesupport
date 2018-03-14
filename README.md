# jeesupprot

## 内容介绍
jeesupport提供了Java开发中常用的一些技术和框架供使用和参考。
## 支持内容
* jees-boot 
> 基于Spring-Boot的学习和使用参考  
* jees-jtls
> 提供了一些可能用到的组件或算法
* jees-jdbs
> 基于Atomikos的分布式数据库解决方案
* jees-jsts
> 基于Netty的Socket/WebSocket解决方案

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
	<artifactId>jees-boot</artifactId>
	<version>1.1.3-SNAPSHOT</version>
</dependency>
```
## 其他
后续内容请关注JEES讨论群或者论坛（未开放，主要没时间[懒]） 
QQ群：8802330  
论坛：[http://www.jeesupport.com](http://www.jeesupport.com)

