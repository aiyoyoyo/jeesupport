# jees-jdbs

## 内容介绍
本工具基于Spring和Atomikos实现分布式数据库配置方案，后续见更新说明。
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
	<artifactId>jees-jdbs</artifactId>
	<version>1.1.3-SNAPSHOT</version>
</dependency>
```
## 配置文件说明
* Hibernate 策略配置
> persistence-mysql.properties
* 日志配置
> log4j2.xml
* Atomikos配置
> jta.properties
* 核心文件配置
> jees-core-database.xml  
核心配置包含了数据库策略等基本配置方案，可以根据需要自行调整。
```
<!-- 以下为项目配置参考 -->
<!-- 各个数据库配置文件 -->
<import resource="jees-database-testa.xml"/>
<import resource="jees-database-testb.xml"/>
<!-- 这里扫描主要指包含jees-jdbs核心类部分以及@Service、@Entity等需要数据库支持的类部分，
用于WEB项目的话@Controller注解不可以在这里被扫描到。 -->
<context:component-scan  base-package="com.jees.*" />  

<!-- 数据库池，程序通过关键字获取对应的数据库session。ID为固定值，不可修改。 -->
<!-- 
Example:
dao.executeByHQL( key, _hql );
-->
<util:map id="sessionFactoryMap" >
    <!-- key为数据库标识 -->
    <entry key="db_a" value-ref="testSFA"/>
    <entry key="db_b" value-ref="testSFB"/>
</util:map> 
```
* 数据库配置
>jees-database-*.xml  
数据库配置提供了2种配置形式，加密和非加密配置。  
加密内容使用了jees-jtls工具来生成。
```
<!-- 加密的配置 -->
<bean id="xaProperties" class="com.jees.core.database.support.EncryptProperties">
    <!-- 加密关键字，解密失败将会使用配置原文进行连接 -->
    <property name="keys" value="/IAB/wXVJQofu4NZkknDXg=="/>
    <property name="props">
        <props>
            <prop key="user">o5nm/ZhqEU+V4m7jADu5fw==</prop>
            <prop key="password">o5nm/ZhqEU+V4m7jADu5fw==</prop>
            <prop key="url">e5dxhrnLyfhBgYazWb546CPz8ZMe4AK1EDkTy44bvIHmHUY9i+p1KvTUxiTqvjS2</prop>
            <prop key="pinGlobalTxToPhysicalConnection">true</prop>
        </props>
    </property>
</bean>
```
```
<!-- 非加密配置 -->
<bean id="testDSB" parent="abstractXADS">
    <property name="uniqueResourceName" value="dataSourceB" />
    <property name="xaDataSourceClassName" value="com.mysql.jdbc.jdbc2.optional.MysqlXADataSource" />
    <property name="xaProperties">
        <props>
            <prop key="user">root</prop>
            <prop key="password">root</prop>
            <prop key="url">jdbc:mysql://localhost:3306/testb</prop>
            <prop key="pinGlobalTxToPhysicalConnection">true</prop>
        </props>
    </property>
</bean>
```
* SpringBoot 配置文件
> application.yml
关于SpringBoot配置，这里仅作参考，暂不启用。
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
该内容为减少配置化文件所写的内容，部分内容尚未生效。
* com.jees.core.database.repository
该内容为JPA方式的数据库操作，部分内容尚未生效。