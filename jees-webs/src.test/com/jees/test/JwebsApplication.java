package com.jees.test;


import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.tool.utils.CustomSystemUtil;
import com.jees.tool.utils.FileUtil;
import com.jees.webs.support.ISuperService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;

import java.util.Arrays;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan( "com.jees" )
@DependsOn({"commonContextHolder", "commonConfig" })
@Log4j2
public class JwebsApplication{
    public static void main(String[] args){
        System.out.println( "Project Path:" + FileUtil.path( "classpath:" ) );
        System.out.println( "Start with:" + Arrays.toString( args ) );
        SpringApplication.run( JwebsApplication.class, args);
//        CommonContextHolder.getBean( ISuperService.class ).initialize();
        log.info( "服务器启动: http"
                + ( CommonConfig.getBoolean( "server.useSSL", false ) ? "s" : "" )
                + "://" + CustomSystemUtil.INTRANET_IP  + ":"
                + CommonConfig.getString( "server.port", "8080" )
                + CommonConfig.getString( "server.path", "/" )
        );
    }
}


