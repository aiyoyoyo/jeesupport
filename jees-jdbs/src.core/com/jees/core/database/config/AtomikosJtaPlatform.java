package com.jees.core.database.config;

import com.jees.common.CommonLogger;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 *
 * 提供Atomikos的桥接入口，此为Hibernate5需要的内容。
 *
 * @author aiyoyoyo
 *
 */
public class AtomikosJtaPlatform extends AbstractJtaPlatform {

    static JtaTransactionManager   jtaTransactionManager;

    public AtomikosJtaPlatform(){
        super();
    }

    public AtomikosJtaPlatform( JtaTransactionManager _jtm ){
        jtaTransactionManager = _jtm;
    }

    public void setJtaTransactionManager( JtaTransactionManager jtaTransactionManager ){
        CommonLogger.getLogger().debug( "设置JtaTransactionManager：" + jtaTransactionManager );
        AtomikosJtaPlatform.jtaTransactionManager = jtaTransactionManager;
    }

    @Override
    protected TransactionManager locateTransactionManager() {
        return jtaTransactionManager.getTransactionManager();
    }


    @Override
    protected UserTransaction locateUserTransaction() {
        return jtaTransactionManager.getUserTransaction();
    }


}
