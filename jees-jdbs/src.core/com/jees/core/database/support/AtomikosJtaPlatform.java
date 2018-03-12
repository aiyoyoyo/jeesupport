package com.jees.core.database.support;

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
public class AtomikosJtaPlatform extends AbstractJtaPlatform {

    static JtaTransactionManager   jtaTransactionManager;

    public void setJtaTransactionManager(JtaTransactionManager jtaTransactionManager) {
        this.jtaTransactionManager = jtaTransactionManager;
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
