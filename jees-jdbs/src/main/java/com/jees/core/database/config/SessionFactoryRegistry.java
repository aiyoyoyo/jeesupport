package com.jees.core.database.config;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import com.jees.core.database.support.ISupportDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import java.util.Properties;

@Log4j2
public class SessionFactoryRegistry {
    public void registerSessionFactory(String _name) {
        AtomikosDataSourceBean ds = createXADataSource(_name);
        SessionFactoryImpl bean = createSessionFactoryBean( _name, ds );

        ISupportDao dao = CommonContextHolder.getBean(ISupportDao.class);
        dao.putSessionFactory( _name , bean );
    }

    private AtomikosDataSourceBean createXADataSource(String _name) {
        String head = "jees.jdbs.config." + _name + ".";
        String type = CommonConfig.getString(head + "dbtype");
        String bean = _name + "XADataSource";
        Properties xaProperties = new Properties();

        xaProperties.setProperty("user", CommonConfig.getString(head + "user"));
        xaProperties.setProperty("password", CommonConfig.getString(head + "password"));

        AbsXADataSource xaDataSource = new AbsXADataSource();
        xaDataSource.setBeanName( bean );

//        // 这里没有列举AbstractDataSourceBean的所有可用属性
        if (type.equalsIgnoreCase("mysql")) {
            xaProperties.setProperty("url", CommonConfig.getString(head + "url"));
            xaProperties.setProperty("pinGlobalTxToPhysicalConnection",CommonConfig.getString(head + "pinGlobalTxToPhysicalConnection", "true"));
            xaDataSource.setXaDataSourceClassName( CommonConfig.getString(head + "xaDataSourceClassName") );
        }else if (type.equalsIgnoreCase("orcale")) {
            xaProperties.setProperty("URL", CommonConfig.getString(head + "url"));
            xaDataSource.setXaDataSourceClassName( CommonConfig.getString(head + "xaDataSourceClassName") );
        }else if(type.equalsIgnoreCase("sqlserver")){
            String url = CommonConfig.getString(head + "url");
            String database = CommonConfig.getString(head + "com/jees/core/database");
            String use_xa = CommonConfig.getString(head + "xaDataSourceClassName");
            String driver_class = CommonConfig.getString(head + "driverClassName");
            url += ";databaseName=" + database + ";";
            if( !driver_class.isEmpty() ){
                SQLServerXADataSource xa_ds = new SQLServerXADataSource();
                xa_ds.setURL( url );
                xa_ds.setUser( CommonConfig.getString(head + "user") );
                xa_ds.setPassword( CommonConfig.getString(head + "password") );
                xaDataSource.setXaDataSource(xa_ds);
            }else {
                xaDataSource.setXaDataSourceClassName( use_xa );
//                xaDataSource.setBorrowConnectionTimeout(60);
//                atomikosDataSourceBean.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
            }
            xaProperties.setProperty("URL", url);
        }
//        net.ucanaccess.jdbc.UcanaccessDataSource
//        org.hibernate.dialect.H
//        com.h.hxtt.support.hibernate.HxttAccessDialect

        xaDataSource.setUniqueResourceName( CommonConfig.getString(head + "uniqueResourceName") );
        xaDataSource.setXaProperties( xaProperties );
        xaDataSource.setMaxPoolSize(CommonConfig.getInteger(head + "maxPoolSize", 1));
        xaDataSource.setMinPoolSize(CommonConfig.getInteger(head + "minPoolSize", 1));
        xaDataSource.setMaxIdleTime(CommonConfig.getInteger(head + "maxIdleTime", 60));
        xaDataSource.setPoolSize(CommonConfig.getInteger(head + "poolSize", 1));

        log.debug("--创建AbsXADataSource[" + bean + "]。");
        return xaDataSource;
    }

    private SessionFactoryImpl createSessionFactoryBean( String _name, AtomikosDataSourceBean _ds ) {
        String head = "jees.jdbs.config." + _name + ".";
        String hibernate = head + "hibernate.";
        String orm = CommonConfig.getString( head + "orm" );
        String bean = _name + "SessionFactory";

        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.rootBeanDefinition(LocalSessionFactoryBean.class);

        if( orm.equalsIgnoreCase("hibernate") ){
            Properties hibernateProperties = new Properties();

            String type = CommonConfig.getString(head + "dbtype");
            if (type.equalsIgnoreCase("mysql")) {
                hibernateProperties.setProperty("hibernate.dialect",
                        CommonConfig.getString( hibernate + "dialect","org.hibernate.dialect.MySQL55Dialect") );
            }else if(type.equalsIgnoreCase("orcale")){
                hibernateProperties.setProperty("hibernate.dialect",
                        CommonConfig.getString( hibernate + "dialect","org.hibernate.dialect.Oracle8iDialect") );
            }

            hibernateProperties.setProperty("hibernate.show_sql",
                    CommonConfig.getString( hibernate + "showSql","true" ) );
            hibernateProperties.setProperty("hibernate.transaction.factory_class",
                    CommonConfig.getString( hibernate + "factoryClass","org.hibernate.transaction.JTATransactionFactory" ) );
            hibernateProperties.setProperty("hibernate.hbm2ddl.auto",
                    CommonConfig.getString( hibernate + "hbm2ddl", "none" ) );
            hibernateProperties.setProperty("hibernate.transaction.jta.platform",
                    CommonConfig.getString( hibernate + "platform", "com.jees.core.com.jees.core.database.config.AtomikosJtaPlatform" ) );
            hibernateProperties.setProperty("hibernate.transaction.coordinator_class",
                    CommonConfig.getString( hibernate + "coordinatorClass","jta" ) );

            beanDefinitionBuilder.addPropertyValue("dataSource", _ds);
            beanDefinitionBuilder.addPropertyValue("packagesToScan",
                    CommonConfig.getString( head + "packagesToScan" ) );
            beanDefinitionBuilder.addPropertyValue("hibernateProperties", hibernateProperties);
        }

        ConfigurableApplicationContext context = (ConfigurableApplicationContext) CommonContextHolder.getApplicationContext();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();

        log.debug("--创建LocalSessionFactoryBean[" + bean + "]。");

        beanFactory.registerBeanDefinition(bean, beanDefinitionBuilder.getBeanDefinition());

        return CommonContextHolder.getBean( bean );
    }
}
