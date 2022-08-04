# jees-webs

## 内容介绍
本工具基于Spring和Thymeleaf搭建应用于Web开发环境。
## 基本用法
在pom.xml中加入下面2段内容。
```
<dependency>
	<groupId>com.github.aiyoyoyo</groupId>
	<artifactId>jees-webs</artifactId>
	<version>${jees.webs.version}</version>
</dependency>
```
## 配置文件说明
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
## 部署问题
> 我尝试了4种方式运行项目，总结了一些问题如下
### IDEA社区版 内置Tomcat方式运行
1. 项目正常启动
2. 控制台乱码修改logback中编码为UTF-8
3. classpath路径相对项目路径
### 内置Tomcat打包运行
1. 打包前需要拷贝src.test目录的类至src.core中
2. 项目通过start.bat指定JwebsApplication运行，正常启动
3. 路径结构为templates和config在jar外部
4. classpath路径相对项目路径
```
打包命名参考
clean package -f pom.xml
```
### IDEA社区版 SmartTomcat运行
1. 需要修改pom-war.xml中的maven-war-plugin属性，指定对应的目录
2. 项目通过Application类，正常启动，模版无法加载
3. classpath路径存在问题，原因是设置导致根路径位置不统一
```
<webappDirectory>src.resources</webappDirectory>
<workDirectory>src.resources/WEB-INF/classes</workDirectory>
```
### 外部Tomcat打包运行
1. 通过Tomcat下startup.bat启动, 控制台乱码修改logback中编码为GBK
2. 项目正常启动，模版无法加载
3. 原因同第二种方式, classpath指向了tomcat/lib目录，导致根路径不统一
4. 为了不影响正常文件结构，我修改了打包路径
```
<webappDirectory>webapps/jees-webs</webappDirectory>
<workDirectory>webapps/jees-webs/WEB-INF/classes</workDirectory>
```
```
打包命名参考
clean package -pom-war.xml
```
### 对于3、4的处理方式
1. 可以考虑固定路径的方式来重写template等静态模版的加载
2. 动态获取相对路径寻找更好的写法

## 更新历史
### 1.7.0-SNAPSHOT <font color='red'>新版本</font>
1. 重新设计了底层框架
### 1.6.0-SNAPSHOT 
1. 重做了系统登录的权限认证，简单认证、数据库认证方式
2. 相关配置调整
```
    # 启用安装程序
    jees.webs.install.enable = true
    # 验证启用
    jees.webs.verify.enable = true
    # 验证方式 simple/database
    jees.webs.verify.model = simple
```
3. 简单验证 config/verify.cfg
```
    # 配置会自动继承继承上级配置,并追加人员
    # 默认配置
    [*] 
    # *-全允许; 空白-全拒绝
    user =
    # 根路径 等于[/index]
    [/]
    # 所有人可以访问
    user = *
    [/manager]
    # admin可以访问
    user = admin
    [/manager/roles]
    # admin/john可以访问
    user = john
    [/manager/menus]
    # admin/lina可以访问
    user = lina
    [/super]
    # admin/john,lina可以访问, marri不可以访问
    user = admin,john,lina
    deny = marri
    # /super页面的元素#BTN_SUBMIT访问权限
    [/super$#BTN_SUBMIT]
    # admin/john可以访问, lina/marri不可以访问
    user = admin,john
```
4. 
### 1.5.0-SNAPSHOT 
1. 优化了pom配置文件，针对4种运行方式做了测试。开发工具为idea社区版2020.1和smart tomcat

### 1.2.1-SNAPSHOT
1. 优化日志文件
2. 优化打包内容
### 1.2.0
1. 新增了打包配置package.xml，通过maven的package功能分离配置文件形式打包。
2. 静态文件夹配置spring.resources.static-locations不可用assets指定。
### 1.2.0-SNAPSHOT
1. 旧的MySQL支持暂时不再更新，保留MySQL用法的版本为1.1.1-SNAPSHOT
2. 新得版本使用Redis代替数据库使用，请参考JDBS中的示例
3. 优化实现细节，由于DWR泛型的问题，放弃了SuperUser类的id类型使用自定义泛型的方式，改为long类型。
4. 新增了系统部署安装模版，开发完成后可以通过基础安装来完成初始应用的设定。
### 1.2.0-SNAPSHOT 
1. 旧的MySQL支持暂时不再更新，保留MySQL用法的版本为1.1.1-SNAPSHOT
2. 新得版本使用Redis代替数据库使用，请参考JDBS中的示例
3. 优化实现细节，由于DWR泛型的问题，放弃了SuperUser类的id类型使用自定义泛型的方式，改为long类型。
4. 新增了系统部署安装模版，开发完成后可以通过基础安装来完成初始应用的设定。
### 1.1.1-SNAPSHOT
1. 提供一套基础的权限及登陆演示模版
2. 支持动态配置数据库权限管理
3. 演示代码可以直接使用
### 1.1.0-SNAPSHOT
1. 提供一套初级WEB开发框架，通过较少的约定完成一套多模版多主题的WEB开发结构。
2. 基于SpringBoot和Thymeleaf搭建的完整模版框架。
3. 提供一套SpringSecurity数据库权限和栏目配置方案。
4. 数据服务基于JDBS实现。
