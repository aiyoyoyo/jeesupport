package com.jees.core.database.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.common.CommonLogger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;
import java.util.StringTokenizer;

@Configuration
@EnableTransactionManagement( proxyTargetClass = true )
@DependsOn( { "commonContextHolder", "commonLogger" } )
public class SupportConfig {
    /**
     * atomikos事务管理器，一般情况无需修改
     * @return
     */
    @Bean( initMethod = "init", destroyMethod = "close" )
    public UserTransactionManager atomikosTM(){
        UserTransactionManager utm = new UserTransactionManager();

        utm.setForceShutdown( false );

        CommonLogger.getLogger().debug( "--Spring Bean[atomikosTM]初始化." );
        return utm;
    }

    /**
     * atomikos事务实现，一般情况无需修改
     * @return
     */
    @Bean
    public UserTransactionImp atomikosUT() throws SystemException {
        UserTransactionImp uti = new UserTransactionImp();

        uti.setTransactionTimeout( CommonConfig.getInteger("jees.jdbs.trans.timeout", 300 ) );

        CommonLogger.getLogger().debug( "--Spring Bean[atomikosUT]初始化." );
        return uti;
    }

    /**
     * spring jta 事务管理器，一般情况无需修改
     * @param _utm
     * @param _uti
     * @return
     */
    @Bean
    public JtaTransactionManager transactionManager(@Qualifier( "atomikosTM" ) UserTransactionManager _utm,
                                           @Qualifier( "atomikosUT" ) UserTransactionImp _uti ){
        JtaTransactionManager jtm = new JtaTransactionManager();

        jtm.setTransactionManager( _utm );
        jtm.setUserTransaction( _uti );

        jtm.setAllowCustomIsolationLevels(
                CommonConfig.getBoolean("jees.jdbs.trans.allowCustomIsolationLevels", false ) );

        CommonLogger.getLogger().debug( "--Spring Bean[defaultTM]初始化." );
        return jtm;
    }

    @Bean
    public AtomikosJtaPlatform atomikosJP( @Qualifier( "transactionManager" ) JtaTransactionManager _jtm ){
        CommonLogger.getLogger().debug( "--Spring Bean[atomikosJP]初始化." );
        return new AtomikosJtaPlatform( _jtm );
    }

    /**
     * 找到声明的数据库配置清单依次注入，并返回默认数据库
     * @return
     */
    @Bean
    @DependsOn( "atomikosJP" )
    public SessionFactory sessionFactory(){
        SessionFactoryRegistry sfr = new SessionFactoryRegistry();

        StringTokenizer st = CommonConfig.getStringTokenizer( "jees.jdbs.dbNames");

        while( st.hasMoreTokens() ){
            String d = st.nextToken();
            sfr.registerSessionFactory( d.trim() );
        }

        return CommonContextHolder.getBean( CommonConfig.getString( "jees.jdbs.defaultDB", "default" ) + "SessionFactory" );
    }
}
