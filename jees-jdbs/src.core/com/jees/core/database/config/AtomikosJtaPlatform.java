package com.jees.core.database.config;

import lombok.extern.log4j.Log4j2;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
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
@Log4j2
public class AtomikosJtaPlatform extends AbstractJtaPlatform {

    static JtaTransactionManager   jtaTransactionManager;

    public AtomikosJtaPlatform(){
        super();
    }

    public AtomikosJtaPlatform( JtaTransactionManager _jtm ){
        jtaTransactionManager = _jtm;
    }

    public void setJtaTransactionManager( JtaTransactionManager jtaTransactionManager ){
        log.debug( "设置JtaTransactionManager：" + jtaTransactionManager );
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
