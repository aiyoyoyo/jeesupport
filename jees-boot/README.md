# jees-boot

## 内容介绍
使用jeesupport项目模块引用的父模块

## 基本用法
> pom.xml:
```
<parent>
	<groupId>com.github.aiyoyoyo</groupId>
	<artifactId>jees-boot</artifactId>
	<version>1.2.0-SNAPSHOT</version>
</parent>
```

## 目录结构
```
d:/jeesupport                  -- 项目目录
   jees-boot                   -- JBOOT根模块位置
       src.core                -- 源文件夹
       resources               -- 资源文件夹
           config              -- 项目配置文件夹
       src.test                -- 测试源文件夹
   jees-jdbs                   -- JDBS 子模块位置
   jees-jsts                   -- JSTS 子模块位置
   jees-jtls                   -- JTLS 子模块位置
   jees-webs                   -- JWEBS子模块位置
```

## 更新内容
### 1.2.0 <font color='red'>New</font>
1. 版本全面升级至1.2.0
2. SpringBoot版本升级到2.3.5.RELEASE版
3. 今后各模块更新内容将放在对应模块目录
