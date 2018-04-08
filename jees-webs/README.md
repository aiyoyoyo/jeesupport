# jees-webs

## 内容介绍
本工具基于Spring和Thymeleaf搭建应用于Web开发环境。
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
	<artifactId>jees-webs</artifactId>
	<version>1.1.0-SNAPSHOT</version>
</dependency>
```
## 配置文件说明
* 核心配置
> application.yml
```
jees:
 webs:
  # 超级账号，密码由服务器生成，开启com.jees.webs.support日志DEBUG级可见
  superman: superman
  # 超级账号密码位数，最小为6，最大20
  superpwd: 6
  # 指定默认页，即 /结束的地址    
  def-page: index
  # 指定模版清单，默认第一个为基础模版
  templates: default, other
  # 多模版通用登陆地址
  login: login
  # 各个模版信息，模版间可以同时访问，主题只能通过切换访问其中一个
  # 简单可以理解为默认为前端模版，其他为后端模版
  default:
   # 多主题配置，默认第一个。
   themes: default
   # 资源路径
   assets: assets
   # 基本访问权限， 为false由数据库配置决定，无配置内容可见。true时无配置内容不可见。
   access: false
  other:
   themes: default
   assets: assets
   access: true
#数据库部分参考jees-jdbs
```
* 日志配置
> log4j2.xml
## 程序实现Demo
见src.test
## 一个引用问题：
```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <version>${spring.boot.version}</version>
            <optional>true</optional>
        </dependency>
    </dependencies>
</dependencyManagement>
热更支持需要在项目中以该形式定义，否则会抛出一个异常导致应用停止。
暂时只知道为什么，未明白原因。
```
## 其他
后续内容请关注JEES讨论群或者论坛（未开放）  
QQ群：8802330  
论坛：[http://www.jeesupport.com](http://www.jeesupport.com)

## 最新更新
* 提供基于文件路径的模版抓取方式，并完全了权限认证细节。

