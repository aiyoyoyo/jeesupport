package com.jees.core.database.config;

import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.InitBinder;

public class AbsXADataSource extends AtomikosDataSourceBean {
}
