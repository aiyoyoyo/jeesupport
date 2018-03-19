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
<parent>
	<groupId>com.github.aiyoyoyo</groupId>
	<artifactId>jees-boot</artifactId>
	<version>1.1.4-SNAPSHOT</version>
</parent>
```
## 其他
后续内容请关注JEES讨论群或者论坛（未开放，主要没时间[懒]） 
QQ群：8802330  
论坛：[http://www.jeesupport.com](http://www.jeesupport.com)

## 版本说明
在版本2.0.0之前，都会是快照版本。在此之前会逐步完善相关内容。

## 更新内容
### Jees-Boot Ver.1.1.4-SNAPSHOT
> [JDBS] 
1. 更新了jdbs配置方案，由多文件形式转为集中到application.yml中。详细内容见测试代码和配置文件。
2. 程序配置使用了较多的约定形式，如果不理解，请参考SpringBoot官方文档，或加群咨询。
3. 建议自定义Dao继承BaseDao来重写自己所需的数据库操作。
4. 对于Web应用，现在不确定，@Controller尽量还是独立扫描，不要像示例中使用@ComponentScan("com.jees")这种范围较广的方式避免事务失效。

> [JTLS]
1. 增加CommonConfig读取配置时的方法。 

