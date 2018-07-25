# jeesupprot

## 内容介绍
jeesupport提供了Java开发中常用的一些技术和框架供使用和参考。
## 支持内容
* jees-boot 
> 基于Spring-Boot的学习和使用参考，同时是下面4个的核心依赖项。 
* jees-jtls
> 提供了一些可能用到的组件或算法
* jees-jdbs
> 基于Atomikos的分布式数据库解决方案
* jees-jsts
> 基于Netty的Socket/WebSocket解决方案
* jees-jwebs
> 基于Spring-Boot的web开发解决方案

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
### Jees-Boot Ver.1.1.5-SNAPSHOT
> [JSTS] Ver 1.1.4-SNAPSHOT
1. 提供了服务器套件，以便于各类项目直接应用。
2. 提供了服务器之间通讯方案，详细使用方法，请参考Demo。

### Jees-Boot Ver.1.1.4-SNAPSHOT
> [JTLS] Ver 1.1.4-SNAPSHOT
1. 增加CommonConfig读取配置时的方法。 
2. 增加RandomUtil工具包。
3. 增加DataUtil工具包

> [JDBS] Ver 1.1.4-SNAPSHOT
1. 更新了jdbs配置方案，由多文件形式转为集中到application.yml中。详细内容见测试代码和配置文件。
2. 程序配置使用了较多的约定形式，如果不理解，请参考SpringBoot官方文档，或加群咨询。
3. 建议自定义Dao继承BaseDao来重写自己所需的数据库操作。

> [JSTS] Ver 1.1.3-SNAPSHOT
1. 偷偷修改了配置参数命名，统一结构。
2. 暂时不会增加更多内容，但可能会做细节优化。

> [WEBS] Ver 1.1.1-SNAPSHOT
1. 提供一套基础的权限及登陆演示模版
2. 支持动态配置数据库权限管理
3. 演示代码可以直接使用

