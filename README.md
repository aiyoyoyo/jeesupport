# jeesupprot

## 内容介绍
jeesupport提供了Java开发中常用的一些技术和框架供使用和参考。
开发工具需要安装Lombok，才能正常访问。
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
## 1.1.8 更新说明 <font color='red'>New</font>
### Jees-Boot Ver.1.1.8-SNAPSHOT
> [JTLS] Ver 1.1.5-SNAPSHOT
1. 引入FastJson
> [JDBS] Ver 1.1.6-SNAPSHOT
1. 加入Redis数据操作相关函数
> [JSTS] Ver 1.1.5-SNAPSHOT
1. 细节优化

## 1.1.8 更新说明 
> [JSTS] Ver 1.1.5-SNAPSHOT
1. 使用Json代替原有WebSocket下的消息解析形式
2. 优化了部分相关内容。

## 1.1.7 更新说明
> [JEES] Jees-Boot Ver.1.1.7-SNAPSHOT
1. 升级Spring版本到2.1.0.M2
2. 其他细节更新见各自得更新说明

> [JSTS] Ver 1.1.4-SNAPSHOT
1. 优化MessageException细节
2. 增加连接管理容器SessionService

> [JDBS] Ver 1.1.5-SNAPSHOT
1. 优化配置细节，详情见AbstractDataSorceBean得配置项

## 1.1.5 更新说明
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

## 关于项目下载后的环境配置
### Maven-3.5.2
1. 版本其实考虑最新的就行，主要用于同步Spring和JEES等第三方包来使用。
2. 无需特殊配置，部分特殊配置我写在了jees-boot的pom文件中，直接可以使用。

### SpringBoot-2.1.3.RELEASE
1. 目前也是最新正式版，随着时间推移，也会升级相关的其他第三方版本。
2. SpringBoot的配置内容，在相关的Resources目录下可以找到。
3. 部分内容固定成了Java类。命名目前可能有点乱，请见谅。大致在config包下或者类名以Config结尾。

### IDEA-COMMUNITY 2018.3
1. 版本基本上没太大影响，我把功能环境基本独立出来，除了依赖Lombok，其他的没有影响。
2. 其实建立基础项目，对应下载目录jeesupport，然后通过jees-boot的pom.xml导入maven信息即可，不会太复杂。
> 工程环境说明，以D盘距离：<br/>
> d:/jeesupport                 -- 项目目录<br/>
>   .idea                       -- IDEA项目配置目录<br/>
>   jees-boot                   -- JBOOT根模块位置<br/>
>       src.core                -- 源文件夹<br/>
>       resources               -- 资源文件夹<br/>
>           config              -- 项目配置文件夹<br/>
>       src.test                -- 测试源文件夹<br/>
>   jees-jdbs                   -- JDBS 子模块位置<br/>
>   jees-jsts                   -- JSTS 子模块位置<br/>
>   jees-jtls                   -- JTLS 子模块位置<br/>
>   jees-webs                   -- JWEBS子模块位置<br/>
3. 其实引用的JEES的项目也可以参考JBOOT来写，可以把JEES当作父模块，也可以作为参考把SpringBoot当作父模块。

### JDK-1.8
1. 必须1.8以上，后面可能迁移至1.10。

### Lombok-IDEA插件
1. Bean类型的文件基本都是用Lombok来生成Getter/Setter方法的，请一定不要漏掉该插件。

### 其他想到了在补充
