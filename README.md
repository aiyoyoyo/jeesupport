# jeesupprot

## 内容介绍
jeesupport提供了Java开发中常用的一些技术和框架供使用和参考。
开发工具需要安装Lombok。
## 支持内容
> 移除了jees-boot项，直接由jeesupport来管理
> 基于Spring-Boot的学习和使用参考，同时是下面4个的核心依赖项。 
* [jees-jtls](https://github.com/aiyoyoyo/jeesupport/tree/master/jees-jtls)
> 提供了一些可能用到的组件或算法
* [jees-jdbs](https://github.com/aiyoyoyo/jeesupport/tree/master/jees-jdbs)
> 基于Atomikos的分布式数据库解决方案
* [jees-jsts](https://github.com/aiyoyoyo/jeesupport/tree/master/jees-jsts)
> 基于Netty的Socket/WebSocket解决方案
* [jees-jwebs](https://github.com/aiyoyoyo/jeesupport/tree/master/jees-jwebs)
> 基于Spring-Boot的web开发解决方案

## 基本用法
在pom.xml中加入下面2段内容。
```
<repository>
    <url>https://oss.sonatype.org/content/groups/public/</url>
    <!-- 不使用快照版本可以设置false来关闭 -->
    <snapshots>
        <enabled>true</enabled> 
        <updatePolicy>always</updatePolicy>
        <checksumPolicy>fail</checksumPolicy>
    </snapshots>
</repository>
<parent>
	<groupId>com.github.aiyoyoyo</groupId>
	<artifactId>jees-boot</artifactId>
	<version>1.2.0</version>
</parent>
```

## 其他
论坛没时间没架设，欢迎加QQ群讨论：8802330  
本来想发布正式版，无奈签名始终验证失败，暂时放弃了。

## 关于项目下载后的环境配置
### Maven-3.5.3
1. 版本其实考虑最新的就行，主要用于同步Spring和JEES等第三方包来使用。
2. 无需特殊配置，部分特殊配置我写在了jees-boot的pom文件中，直接可以使用。

### SpringBoot-2.3.5.RELEASE
1. 目前也是最新正式版，随着时间推移，也会升级相关的其他第三方版本。
2. SpringBoot的配置内容，在相关的Resources目录下可以找到。
3. 部分内容固定成了Java类。命名目前可能有点乱，请见谅。大致在config包下或者类名以Config结尾。

### IDEA-COMMUNITY 2021.2
1. 版本基本上没太大影响，我把功能环境基本独立出来，除了依赖Lombok，其他的没有影响。
2. 其实建立基础项目，对应下载目录jeesupport，然后通过jees-boot的pom.xml导入maven信息即可，不会太复杂。
3. 请在idea设置中设置工程环境等均为UTF-8以保证中文可以正常使用。
> 工程环境说明，以D盘举例：<br/>
```
d:/jeesupport                  -- 项目目录
   .idea                       -- IDEA项目配置目录
   jees-[mod]                  -- JBOOT根模块位置
       src.main
            java               -- 源文件夹
            resources          -- 资源文件夹
                config         -- 项目配置文件夹
       src.test                -- 测试源文件夹
   jees-jdbs                   -- JDBS 子模块位置
   jees-jsts                   -- JSTS 子模块位置
   jees-jtls                   -- JTLS 子模块位置
   jees-webs                   -- JWEBS子模块位置
```
4. 其实引用的JEES的项目也可以参考JBOOT来写，可以把JEES当作父模块，也可以作为参考把SpringBoot当作父模块。

### JDK-1.8
1. 必须1.8，我尝试升级到1.10，发现有问题暂时放弃研究。

### Lombok-IDEA插件
1. Bean类型的文件基本都是用Lombok来生成Getter/Setter方法的，请一定不要漏掉该插件。

### 其他想到了在补充
