package com.jees.core.database.config;

import com.jees.common.CommonContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import java.util.Properties;

public class SessionFactoryRegistry {
    static Logger logger =  LoggerFactory.getLogger( SessionFactoryRegistry.class );

    public LocalSessionFactoryBean registerSessionFactory(String _name ){
        Properties hibernateProperties = new Properties();

        hibernateProperties.setProperty( "hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        hibernateProperties.setProperty( "hibernate.dialect", "org.hibernate.dialect.MySQL55Dialect");
        hibernateProperties.setProperty( "hibernate.hbm2ddl.auto", "create");
        hibernateProperties.setProperty( "hibernate.show_sq", "true");
        hibernateProperties.setProperty( "hibernate.transaction.factory_class",
                "org.hibernate.transaction.JTATransactionFactory");
        hibernateProperties.setProperty( "hibernate.transaction.jta.platform",
                "com.jees.core.database.config.AtomikosJtaPlatform");
        hibernateProperties.setProperty( "hibernate.transaction.coordinator_class", "jta");
        hibernateProperties.setProperty( "hibernate.current_session_context_class",
                "org.springframework.orm.hibernate5.SpringSessionContext" );

        LocalSessionFactoryBean lsfb = getSessionFactoryBean( _name );

        AtomikosDataSourceBean ds = createXADataSource( _name );

        lsfb.setHibernateProperties( hibernateProperties );
        lsfb.setDataSource( ds );
        lsfb.setPackagesToScan( "com.jees.test.entity" );

        logger.debug( "--registerSessionFactory[" + _name + "]。" );

        return lsfb;
    }

    private AtomikosDataSourceBean createXADataSource( String _name ){
        Properties xaProperties = new Properties();

        xaProperties.setProperty( "username", "root" );
        xaProperties.setProperty( "password", "root" );
        xaProperties.setProperty( "url", "jdbc:mysql://localhost:3306/testa" );
        xaProperties.setProperty( "pinGlobalTxToPhysicalConnection", "true" );

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setUniqueResourceName( _name );
        xaDataSource.setXaProperties( xaProperties );

        logger.debug( "--创建AbsXADataSource[" + _name + "]。" );
        return xaDataSource;
    }

    private LocalSessionFactoryBean getSessionFactoryBean( String _name ){
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition( LocalSessionFactoryBean.class );

        ConfigurableApplicationContext context = (ConfigurableApplicationContext) CommonContextHolder.getApplicationContext();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)context.getBeanFactory();

        beanFactory.registerBeanDefinition( _name, beanDefinitionBuilder.getBeanDefinition() );

        logger.debug( "--创建LocalSessionFactoryBean[" + _name + "]。" );
        return beanFactory.getBean( _name, LocalSessionFactoryBean.class );
    }
}
