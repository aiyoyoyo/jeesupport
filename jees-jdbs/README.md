# jees-jdbs

## 内容介绍
本工具基于Spring和Atomikos实现分布式数据库配置方案，后续见更新说明。
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
	<artifactId>jees-jdbs</artifactId>
	<version>${jees.jdbs.version}</version>
</dependency>
```
## 配置文件说明
* 日志配置
> log4j2.xml
* SpringBoot 配置文件
> application.yml
数据库配置模版参考
```
jees:
 jdbs:
  defaultDB: testa
  dbNames: testa, testb
  trans:
   timeout: 3000
   allowCustomIsolationLevels: true
  config:
   testa:
    dbtype: mysql
    orm: hibernate
    user: root
    password: root
    url: jdbc:mysql://localhost:3306/testa
    pinGlobalTxToPhysicalConnection: true
    uniqueResourceName: datasourceA
    xaDataSourceClassName: com.mysql.jdbc.jdbc2.optional.MysqlXADataSource
    packagesToScan: com.jees.test.entity
    hibernate:
     dialect: org.hibernate.dialect.MySQL55Dialect
     showSql: true
     factoryClass: org.hibernate.transaction.JTATransactionFactory
     hbm2ddl: none
     platform: com.jees.core.database.config.AtomikosJtaPlatform
     coordinatorClass: jta
   testb:
    dbtype: mysql
    orm: hibernate
    user: root
    password: root
    url: jdbc:mysql://localhost:3306/testb
    pinGlobalTxToPhysicalConnection: true
    uniqueResourceName: datasourceB
    xaDataSourceClassName: com.mysql.jdbc.jdbc2.optional.MysqlXADataSource
    packagesToScan: com.jees.test.entity
```
## 程序实现Demo
* DAO继承
```
@Component
public class YourDao extends AbsSupportDao{
    ... 数据库操作实现 ...
    ... AbsSupportDao已经包含了部分基础数据库操作，后续可能重命名为AbsHibernateDao ...
}
```
* 事务
以web开发中的Controller举例，使用@Transactional注解来声明事务范围。  
Spring的事务代理将会以抛出异常到包含该标签的最上层方法作回滚处理。  
```
@Controller
public class MyController{
    @Autowired
    YourDao dao;
    
    @Transactional
    public void () {
        dao.dosomething();
    }
}
```
## 其他
后续内容请关注JEES讨论群或者论坛（未开放）  
QQ群：8802330  
论坛：[http://www.jeesupport.com](http://www.jeesupport.com)

## 新内容说明
* com.jees.core.database.config
现已有原来的多配置文件形式简化到application.yml中。详情请参考Github中示例。
* com.jees.core.database.repository
该内容为JPA方式的数据库操作，部分内容尚未生效。