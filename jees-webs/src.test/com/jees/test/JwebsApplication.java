package com.jees.test;


import com.jees.common.CommonContextHolder;
import com.jees.webs.support.ISuperService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan( "com.jees" )
@DependsOn({"commonContextHolder", "commonConfig" })
public class JwebsApplication{
    public static void main(String[] args){
        SpringApplication.run( JwebsApplication.class, args);
        CommonContextHolder.getBean( ISuperService.class ).initialize();
    }
}


