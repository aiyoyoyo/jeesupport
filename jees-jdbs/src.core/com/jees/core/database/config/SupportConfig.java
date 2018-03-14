package com.jees.core.database.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;

//@SpringBootConfiguration
//@EnableTransactionManagement( proxyTargetClass = true )
public class SupportConfig {
    static Logger logger =  LoggerFactory.getLogger( SupportConfig.class );

    /**
     * atomikos事务管理器，一般情况无需修改
     * @return
     */
//    @Bean( initMethod = "init", destroyMethod = "close" )
    public UserTransactionManager atomikosTM(){
        UserTransactionManager utm = new UserTransactionManager();

        utm.setForceShutdown( false );

        logger.debug( "--Spring Bean[atomikosTM]初始化." );
        return utm;
    }

    /**
     * atomikos事务实现，一般情况无需修改
     * @return
     */
//    @Bean
    public UserTransactionImp atomikosUT() throws SystemException {
        UserTransactionImp uti = new UserTransactionImp();

        uti.setTransactionTimeout( 3000 );

        logger.debug( "--Spring Bean[atomikosUT]初始化." );
        return uti;
    }

    /**
     * spring jta 事务管理器，一般情况无需修改
     * @param _utm
     * @param _uti
     * @return
     */
//    @Bean
    public JtaTransactionManager defaultTM(@Qualifier( "atomikosTM" ) UserTransactionManager _utm,
                                           @Qualifier( "atomikosUT" ) UserTransactionImp _uti ){
        JtaTransactionManager jtm = new JtaTransactionManager();

        jtm.setTransactionManager( _utm );
        jtm.setUserTransaction( _uti );

        jtm.setAllowCustomIsolationLevels( true );

        logger.debug( "--Spring Bean[defaultTM]初始化." );
        return jtm;
    }

//    @Bean
//    public AtomikosJtaPlatform atomikosJP( @Qualifier( "defaultTM" ) JtaTransactionManager _jtm ){
//        logger.debug( "--Spring Bean[atomikosJP]初始化." );
//        return new AtomikosJtaPlatform( _jtm );
//    }
}
