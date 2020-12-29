package com.jees.test;

import com.jees.common.CommonConfig;
import com.jees.tool.utils.CustomSystemUtil;
import com.jees.tool.utils.FileUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.Context;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan( "com.jees" )
@DependsOn({"commonContextHolder", "commonConfig" })
@Log4j2
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder _builder) {
        System.out.println( "Project Path:" + FileUtil.path( "classpath:" ) );
        log.info( "服务器启动: http"
                + ( CommonConfig.getBoolean( "server.useSSL", false ) ? "s" : "" )
                + "://" + CustomSystemUtil.INTRANET_IP  + ":"
                + CommonConfig.getString( "server.port", "8080" )
                + CommonConfig.getString( "server.path", "/" )
        );
        return _builder.sources(Application.class);
    }

    @Bean
    public TomcatServletWebServerFactory tomcatFactory(){
        return new TomcatServletWebServerFactory(){
            @Override
            protected void postProcessContext(Context _context) {
                ((StandardJarScanner)_context.getJarScanner()).setScanManifest(false);
            }
        };
    }
}