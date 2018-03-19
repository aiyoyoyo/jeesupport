# jees-jtls

## 内容介绍
本工具基于Spring和其他第三方框架，或者是自己写的一些算法集中在一起编写的工具包。
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
	<artifactId>jees-jtls</artifactId>
	<version>${jees.jtls.version}</version>
</dependency>
```
## 配置文件说明
* 暂无
* 日志配置
> log4j2.xml
## 程序实现Demo
* 实现请参考测试代码，这里不一一举例说明，仅列出相关测试类说明，如果涉及到编码，则统一为UTF-8。
> CryptoTest 加密测试类，包含了：AES/MD5/Base64/DES/RSA这几种。
> LicenseTest 证书本地化测试，待完善
> PaginationTest 分页算法，待完善
> SortListTest 对象排序
## 其他
后续内容请关注JEES讨论群或者论坛（未开放）  
QQ群：8802330  
论坛：[http://www.jeesupport.com](http://www.jeesupport.com)

