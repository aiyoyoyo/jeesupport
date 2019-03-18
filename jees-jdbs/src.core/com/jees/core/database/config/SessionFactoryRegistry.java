package com.jees.core.database.config;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.core.database.support.ISupportDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.support.*;
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
        String type = CommonConfig.getString( head + "dbtype" );
        String bean = _name + "XADataSource";
        Properties xaProperties = new Properties();

        xaProperties.setProperty("user", CommonConfig.getString(head + "user"));
        xaProperties.setProperty("password", CommonConfig.getString(head + "password"));
        xaProperties.setProperty("url", CommonConfig.getString(head + "url"));

        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.rootBeanDefinition(AbsXADataSource.class);

        // 这里没有列举AbstractDataSourceBean的所有可用属性
        if( type.equalsIgnoreCase( "mysql" ) ) {
            xaProperties.setProperty("pinGlobalTxToPhysicalConnection",
                    CommonConfig.getString(head + "pinGlobalTxToPhysicalConnection", "true"));

            beanDefinitionBuilder.addPropertyValue("uniqueResourceName",
                    CommonConfig.getString( head + "uniqueResourceName" ));
            beanDefinitionBuilder.addPropertyValue("xaDataSourceClassName",
                    CommonConfig.getString( head + "xaDataSourceClassName" ) );

            beanDefinitionBuilder.addPropertyValue("xaProperties", xaProperties);

            beanDefinitionBuilder.addPropertyValue("maxPoolSize",
                    CommonConfig.getString( head + "maxPoolSize", "1" ));
            beanDefinitionBuilder.addPropertyValue("minPoolSize",
                    CommonConfig.getString( head + "minPoolSize", "1" ));
            beanDefinitionBuilder.addPropertyValue("maxIdleTime",
                    CommonConfig.getString( head + "maxIdleTime", "60" ));

            beanDefinitionBuilder.addPropertyValue("poolSize", CommonConfig.getString( head + "poolSize", "1" ) );
        }

        ConfigurableApplicationContext context = (ConfigurableApplicationContext) CommonContextHolder.getApplicationContext();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();

        beanFactory.registerBeanDefinition( bean, beanDefinitionBuilder.getBeanDefinition() );

        AbsXADataSource xaDataSource = CommonContextHolder.getBean( bean );
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

            hibernateProperties.setProperty("hibernate.dialect",
                    CommonConfig.getString( hibernate + "dialect","org.hibernate.dialect.MySQL55Dialect") );
            hibernateProperties.setProperty("hibernate.show_sql",
                    CommonConfig.getString( hibernate + "showSql","true" ) );
            hibernateProperties.setProperty("hibernate.transaction.factory_class",
                    CommonConfig.getString( hibernate + "factoryClass","org.hibernate.transaction.JTATransactionFactory" ) );
            hibernateProperties.setProperty("hibernate.hbm2ddl.auto",
                    CommonConfig.getString( hibernate + "hbm2ddl", "none" ) );
            hibernateProperties.setProperty("hibernate.transaction.jta.platform",
                    CommonConfig.getString( hibernate + "platform", "com.jees.core.database.config.AtomikosJtaPlatform" ) );
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
