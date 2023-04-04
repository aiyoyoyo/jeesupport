package com.jees.core.database.dialect.identity;

import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

/**
 * @Description: TODO
 * @Package: com.jees.core.database.dialect.identity
 * @ClassName: SQLiteIdentityColumnSupport
 * @Author: 刘甜
 * @Date: 2023/4/4 10:52
 * @Version: 1.0
 */
public class SQLiteIdentityColumnSupport extends IdentityColumnSupportImpl {
    public SQLiteIdentityColumnSupport() {
    }

    public boolean supportsIdentityColumns() {
        return true;
    }

    public String getIdentitySelectString(String table, String column, int type) {
        return "select last_insert_id()";
    }

    public String getIdentityColumnString(int type) {
        return "not null auto_increment";
    }
}
