# jees-jtls

## 内容介绍
本工具基于Spring和其他第三方框架，或者是自己写的一些算法集中在一起编写的工具包。

## 基本用法
在pom.xml中加入下面2段内容。
```
<dependency>
	<groupId>com.github.aiyoyoyo</groupId>
	<artifactId>jees-jtls</artifactId>
	<version>${jees.jtls.version}</version>
</dependency>
```

## 配置文件说明
> 暂无
## 测试类说明
* 实现请参考测试代码，这里不一一举例说明，仅列出相关测试类说明，如果涉及到编码，则统一为UTF-8。
> CryptoTest 加密测试类，包含了：AES/MD5/Base64/DES/RSA这几种。
> LicenseTest 证书本地化测试，待完善
> PaginationTest 分页算法，待完善
> SortListTest 对象排序测试类
> RandomTest 随机算法测试类
> DataTest 数据转换测试类

## 更新历史
### 1.5.0-SNAPSHOT <font color='red'>新版本</font>
1. 未做大的改动，仅针对项目结构做了修改。
2. 更新了个别工具类的用法
### 1.3.0-SNAPSHOT 
1. 使用标准目录结构构建项目
2. 修正CommonConfig的部分函数使用的问题
3. 增加一个StringUtil工具类
### 1.2.1-SNAPSHOT
1. 优化日志文件
2. 优化打包内容
### 1.2.0-SNAPSHOT
1. 优化引用的第三方库
2. 部分类做了功能调整和优化
### 1.1.7-SNAPSHOT
1. 新增CustomSystemUtil，读写文件类，比较常见的写法。
2. 优化新增SensitiveWordUtil功能内容。
3. 新增TplUtil，用于将自定义的tpl文件生成指定文件。
### 1.1.6-SNAPSHOT
1. 新增FileUitl，读写文件类，比较常见的写法。
2. 新增SensitiveWordUtil，用于敏感词过滤，DFA算法。
### 1.1.5-SNAPSHOT
1. 引入FastJson
2. RandomUtil增加Set随机方法
### 1.1.4-SNAPSHOT
1. 增加CommonConfig读取配置时的方法。 
2. 增加RandomUtil工具包。
3. 增加DataUtil工具包

