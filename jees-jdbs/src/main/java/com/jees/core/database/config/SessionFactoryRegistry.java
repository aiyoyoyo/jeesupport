package com.jees.core.database.config;

import com.atomikos.icatch.config.Configuration;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import com.atomikos.util.IntraVmObjectRegistry;
import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import com.jees.core.database.support.ISupportDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.naming.NameNotFoundException;
import java.util.Properties;

@Log4j2
public class SessionFactoryRegistry {
    public SessionFactoryImpl registerSessionFactory(String _name) {
        AtomikosDataSourceBean ds = _create_data_source( _name, null);
        SessionFactoryImpl bean = _create_session_factory( _name, ds, null );
        ISupportDao dao = CommonContextHolder.getBean(ISupportDao.class);
        dao.putSessionFactory( _name , bean );
        return bean;
    }
    public void registerDynamicSessionFactory( String _name, Properties _prop ){
        if( !_name.toUpperCase().startsWith( "DM_" ) ){
            throw new RuntimeException("动态数据源需要以DM_开头!");
        }

        AtomikosDataSourceBean ds = _create_data_source(_name, _prop);
        SessionFactoryImpl bean = _create_session_factory( _name, ds, _prop );

        ISupportDao dao = CommonContextHolder.getBean(ISupportDao.class);
        dao.putSessionFactory( _name , bean );
    }

    public static synchronized void reRegisterSessionFactory( String _name ){
        ISupportDao dao = CommonContextHolder.getBean(ISupportDao.class);
        SessionFactory ssf = dao.getSessionFactory(_name);

        AtomikosDataSourceBean ds = (AtomikosDataSourceBean) ssf.getProperties().get("hibernate.connection.datasource");
        String res_name = ds.getUniqueResourceName();
        if( ssf.isOpen() ){
            ssf.close();
        }

        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) CommonContextHolder.getApplicationContext().getAutowireCapableBeanFactory();
        beanDefinitionRegistry.getBeanDefinition(_name + "SessionFactory");
        beanDefinitionRegistry.removeBeanDefinition( _name + "SessionFactory" );
        Configuration.removeResource(res_name);
        try {
            IntraVmObjectRegistry.removeResource(res_name);
        } catch (NameNotFoundException e) {
        }
        new SessionFactoryRegistry().registerSessionFactory(_name);
    }
    private <T> T _get_config( Properties _prop, String _key, T _def ){
        if( _prop == null){
            return CommonConfig.get(_key, _def );
        }else{
            return (T) _prop.get( _key );
        }
    }
    private AtomikosDataSourceBean _create_data_source(String _name, Properties _prop ){
        String head = "jees.jdbs.config." + _name + ".";
        String type = _get_config( _prop, head + "dbtype", null );
        String uniqueResourceName = _get_config( _prop, head + "uniqueResourceName", null );
        String bean = _name + "DataSource";
        Properties xaProperties = new Properties();

        xaProperties.setProperty("user", _get_config( _prop, head + "user", null ));
        xaProperties.setProperty("password", _get_config( _prop, head + "password", null ));

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
//        AtomikosNonXADataSourceBean nonXaDataSource = new AtomikosNonXADataSourceBean();
        xaDataSource.setBeanName( bean );

        String test_query = "SELECT 1";
//        // 这里没有列举AbstractDataSourceBean的所有可用属性
        if (type.startsWith("mysql")) {
            xaProperties.setProperty("url", _get_config( _prop, head + "url", null ));
            xaProperties.setProperty("pinGlobalTxToPhysicalConnection",
                    _get_config( _prop, head + "pinGlobalTxToPhysicalConnection", "true" )
            );
            if( "mysql8".equalsIgnoreCase(type)){
                xaDataSource.setXaDataSourceClassName(
                        _get_config( _prop, head + "xaDataSourceClassName", "com.mysql.cj.jdbc.MysqlXADataSource" )
                );
            }else{
                xaDataSource.setXaDataSourceClassName(
                        _get_config( _prop, head + "xaDataSourceClassName", "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource" )
                );
            }
            test_query = _get_config( _prop, head + "testQuery", "SELECT 1" );
        }else if ("oracle".equalsIgnoreCase(type)) {
            xaProperties.setProperty("URL",
                    _get_config( _prop, head + "url", null )
            );
            xaDataSource.setXaDataSourceClassName(
                    _get_config( _prop, head + "xaDataSourceClassName", null )
            );
            test_query = _get_config( _prop, head + "testQuery", "SELECT 1 FROM dual" );
        }else if(type.equalsIgnoreCase("sqlserver")){
            String url = _get_config( _prop, head + "url", null );
            String database = _get_config( _prop, head + "com/jees/core/database", null );
            String use_xa = _get_config( _prop, head + "xaDataSourceClassName", null );
            String driver_class = _get_config( _prop, head + "driverClassName", null );
            url += ";databaseName=" + database + ";";
            if( !driver_class.isEmpty() ){
                SQLServerXADataSource xa_ds = new SQLServerXADataSource();
                xa_ds.setURL( url );
                xa_ds.setUser( _get_config( _prop, head + "user", null ));
                xa_ds.setPassword( _get_config( _prop, head + "password", null ));
                xaDataSource.setXaDataSource(xa_ds);
            }else {
                xaDataSource.setXaDataSourceClassName( use_xa );
//                xaDataSource.setBorrowConnectionTimeout(60);
//                atomikosDataSourceBean.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
            }
            xaProperties.setProperty("URL", url);
        }else if("DB2".equalsIgnoreCase(type)){
            test_query = _get_config( _prop, head + "testQuery", "SELECT 1 FROM sysibm.sysdummy1" );
        }

        xaDataSource.setUniqueResourceName( uniqueResourceName );
        xaDataSource.setXaProperties( xaProperties );
        xaDataSource.setMaxPoolSize( _get_config( _prop, head + "maxPoolSize", 30 ));
        xaDataSource.setMinPoolSize( _get_config( _prop, head + "minPoolSize", 10 ));
        xaDataSource.setMaxIdleTime( _get_config( _prop, head + "maxIdleTime", 60 ));
        xaDataSource.setPoolSize(_get_config( _prop, head + "poolSize", 30 ));
        xaDataSource.setBorrowConnectionTimeout( _get_config( _prop, head + "borrowConnectionTimeout", 60 ));
        xaDataSource.setMaintenanceInterval( _get_config( _prop, head + "maintenanceInterval", 60 ));
        xaDataSource.setTestQuery(test_query);
        log.debug("--创建XADataSource[" + bean + "]。");
        return xaDataSource;
    }
    private SessionFactoryImpl _create_session_factory( String _name, AtomikosDataSourceBean _ds, Properties _prop ){
        String head = "jees.jdbs.config." + _name + ".";
        String hibernate = head + "hibernate.";
        String orm = _get_config( _prop,head + "orm", null );
        String bean = _name + "SessionFactory";

        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.rootBeanDefinition(LocalSessionFactoryBean.class);

        if( orm.equalsIgnoreCase("hibernate") ){
            Properties hibernateProperties = new Properties();

            String type = _get_config( _prop,head + "dbtype", null );
            if (type.startsWith("mysql") ) {
                if( type.equalsIgnoreCase("mysql8")){
                    hibernateProperties.setProperty("hibernate.dialect",
                            _get_config( _prop,hibernate + "dialect", "org.hibernate.dialect.MySQL8Dialect" )
                    );
                }else{
                    hibernateProperties.setProperty("hibernate.dialect",
                            _get_config( _prop,hibernate + "dialect", "org.hibernate.dialect.MySQL55Dialect" )
                    );
                }
            }else if(type.equalsIgnoreCase("oracle")){
                hibernateProperties.setProperty("hibernate.dialect",
                        _get_config( _prop,hibernate + "dialect", "org.hibernate.dialect.Oracle8iDialect" )
                );
            }

            hibernateProperties.setProperty("hibernate.show_sql",
                    _get_config( _prop,hibernate + "showSql", "true" )
            );
            hibernateProperties.setProperty("hibernate.transaction.factory_class",
                    _get_config( _prop,hibernate + "factoryClass", "org.hibernate.transaction.JTATransactionFactory" )
            );
            hibernateProperties.setProperty("hibernate.hbm2ddl.auto",
                    _get_config( _prop,hibernate + "hbm2ddl", "none" )
            );
            hibernateProperties.setProperty("hibernate.transaction.jta.platform",
                    _get_config( _prop,hibernate + "platform", "com.jees.core.database.config.AtomikosJtaPlatform" )
            );
            hibernateProperties.setProperty("hibernate.transaction.coordinator_class",
                    _get_config( _prop,hibernate + "coordinatorClass", "jta" )
            );
            hibernateProperties.setProperty("hibernate.autoReconnect",
                    _get_config( _prop,hibernate + "autoReconnect", "true" )
            );

            boolean use_c3p0 = _get_config( _prop, hibernate + "c3p0.enable", false );
            if( use_c3p0 ){
                hibernateProperties.setProperty("hibernate.connection.provider_class",
                        _get_config( _prop,hibernate + "c3p0.provider_class", "org.hibernate.c3p0.internal.C3P0ConnectionProvider" )
                );
                hibernateProperties.setProperty("hibernate.c3p0.min_size",
                        _get_config( _prop,hibernate + "c3p0.min_size", "10" )
                );
                hibernateProperties.setProperty("hibernate.c3p0.max_size",
                        _get_config( _prop,hibernate + "c3p0.max_size", "30" )
                );
                hibernateProperties.setProperty("hibernate.c3p0.timeout",
                        _get_config( _prop,hibernate + "c3p0.timeout", "1800" )
                );
                hibernateProperties.setProperty("hibernate.c3p0.max_statements",
                        _get_config( _prop,hibernate + "c3p0.max_statements", "50" )
                );
                hibernateProperties.setProperty("hibernate.c3p0.testConnectionOnCheckout",
                        _get_config( _prop,hibernate + "c3p0.testConnectionOnCheckout", "true" )
                );
            }

            beanDefinitionBuilder.addPropertyValue("dataSource", _ds);
            beanDefinitionBuilder.addPropertyValue("packagesToScan",
                    _get_config( _prop,head + "packagesToScan", null )
            );
            beanDefinitionBuilder.addPropertyValue("hibernateProperties", hibernateProperties);
        }

        ConfigurableApplicationContext context = (ConfigurableApplicationContext) CommonContextHolder.getApplicationContext();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();

        log.debug("--创建LocalSessionFactoryBean[" + bean + "]。");

        beanFactory.registerBeanDefinition(bean, beanDefinitionBuilder.getBeanDefinition());
        return CommonContextHolder.getBean( bean );
    }
}
