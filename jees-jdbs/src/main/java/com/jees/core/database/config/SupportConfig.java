package com.jees.core.database.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.jta.atomikos.AtomikosProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;
import java.util.StringTokenizer;

@SpringBootConfiguration
@EnableTransactionManagement( proxyTargetClass = true )
@DependsOn( { "commonContextHolder" } )
@Log4j2
public class SupportConfig {

    @Bean
    public AtomikosProperties getAtomikosProperties(){
        AtomikosProperties ap = new AtomikosProperties();

        return ap;
    }

    /**
     * atomikos事务管理器，一般情况无需修改
     * @return 用户事务
     */
    @Bean( initMethod = "init", destroyMethod = "close" )
    public UserTransactionManager atomikosTM(){
        if( !CommonConfig.getBoolean( "jees.jdbs.enable" ) ){
            if(!CommonConfig.getBoolean( "jees.jdbs.localdb.enable" )) {
                return null;
            }
        }

        UserTransactionManager utm = new UserTransactionManager();
        utm.setForceShutdown( false );

        log.debug( "--Spring Bean[atomikosTM]初始化." );
        return utm;
    }

    /**
     * atomikos事务实现，一般情况无需修改
     * @return 用户事物实现类
     * @throws SystemException 系统异常
     */
    @Bean
    public UserTransactionImp atomikosUT() throws SystemException {
        UserTransactionImp uti = new UserTransactionImp();

        if( !CommonConfig.getBoolean( "jees.jdbs.enable" ) ) return uti;

        uti.setTransactionTimeout( CommonConfig.getInteger("jees.jdbs.trans.timeout", 300 ) );

        log.debug( "--Spring Bean[atomikosUT]初始化." );
        return uti;
    }

    /**
     * spring jta 事务管理器，一般情况无需修改
     * @param _utm 用户事务管理器
     * @param _uti 用户事务实现
     * @return jta事务管理器
     */
    @Bean
    public JtaTransactionManager transactionManager(@Qualifier( "atomikosTM" ) UserTransactionManager _utm,
                                           @Qualifier( "atomikosUT" ) UserTransactionImp _uti ){
        JtaTransactionManager jtm = new JtaTransactionManager();

        jtm.setTransactionManager( _utm );
        jtm.setUserTransaction( _uti );

        jtm.setAllowCustomIsolationLevels(
                CommonConfig.getBoolean("jees.jdbs.trans.allowCustomIsolationLevels", false ) );

        log.debug( "--Spring Bean[defaultTM]初始化." );
        return jtm;
    }

    @Bean
    public JtaPlatform atomikosJP(@Qualifier( "transactionManager" ) JtaTransactionManager _jtm ){
        if( !CommonConfig.getBoolean( "jees.jdbs.enable" )){
            if(!CommonConfig.getBoolean( "jees.jdbs.localdb.enable" )) {
                return null;
            }
        }
        log.debug( "--Spring Bean[atomikosJP]初始化." );
        return new AtomikosJtaPlatform( _jtm );
    }

    /**
     * 找到声明的数据库配置清单依次注入，并返回默认数据库
     * @return SessionFactory
     */
    @Bean
    @DependsOn( "atomikosJP" )
    public SessionFactory sessionFactory(){
        if( !CommonConfig.getBoolean( "jees.jdbs.enable" ) ){
            //
            if(CommonConfig.getBoolean( "jees.jdbs.localdb.enable" )) {
                SessionFactoryRegistry sfr = new SessionFactoryRegistry();
                sfr.registerSessionFactory(CommonConfig.getString( "jees.jdbs.localdb.name" ));
                return null;
            }else{
                return null;
            }
        }

        SessionFactoryRegistry sfr = new SessionFactoryRegistry();
        SessionFactoryImpl def_ssf = null;
        String[] db_names = CommonConfig.getArray( "jees.jdbs.dataSources", String.class);
        if( db_names.length == 0){
            def_ssf = sfr.registerSessionFactory( "default" );//尝试注册默认数据源
        }else{

            for( String db_name : db_names ){
                SessionFactoryImpl tmp_def = sfr.registerSessionFactory(db_name.trim());
                if( def_ssf == null ){
                    def_ssf = tmp_def;
                }
            }
        }
        return null;
    }
}
