# jees-jdbs

## 内容介绍
本工具基于Spring和Atomikos实现分布式数据库配置方案，后续见更新说明。

## 基本用法
> pom.xml
```
<dependency>
	<groupId>com.github.aiyoyoyo</groupId>
	<artifactId>jees-jdbs</artifactId>
	<version>${jees.jdbs.version}</version>
</dependency>
```

## 配置文件说明
> application.yml
```
jees:
 jdbs:
  #指定默认的数据库
  defaultDB: testa
  #设定数据库连接的别名清单，程序通过别名引用
  dbNames: testa, testb
  trans:
   timeout: 3000
   allowCustomIsolationLevels: true
  config:
   #基于别名的数据库配置
   testa:
    # 根据数据类型和ORM类型使用对应的配置方案，暂时仅支持MySQL和Hibernate
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
   # 基于别名的数据库配置
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

## 部分内容说明
* com.jees.core.database.config
现已有原来的多配置文件形式简化到application.yml中。详情请参考Github中示例。
* com.jees.core.database.repository
该内容为JPA方式的数据库操作，部分内容尚未生效。
* com.jees.core.database.support
AbsSupportDao调整为基于Hibernate5版本的操作方式。详情参考TestController的absTest方法。

## 更新内容
### 1.2.1-SNAPSHOT <font color='red'>新版本</font>
1. 优化日志文件
2. 优化打包内容
### 1.1.6-SNAPSHOT
1. 加入Redis数据操作相关函数
2. 优化相关内容
### Ver 1.1.5-SNAPSHOT
1. 优化配置细节，详情见AbstractDataSorceBean得配置项
### 1.1.4-SNAPSHOT
1. 更新了jdbs配置方案，由多文件形式转为集中到application.yml中。详细内容见测试代码和配置文件。
2. 程序配置使用了较多的约定形式，如果不理解，请参考SpringBoot官方文档，或加群咨询。
3. 建议自定义Dao继承BaseDao来重写自己所需的数据库操作。


