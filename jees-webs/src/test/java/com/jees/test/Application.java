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
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan( "com.jees" )
@DependsOn({"commonContextHolder", "commonConfig" })
@Log4j2
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder _builder) {
        return _builder.sources(Application.class);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        System.out.println( "Project Path:" + FileUtil.path( "classpath:" ) );
        log.info( "服务器启动: http"
                + ( CommonConfig.getBoolean( "server.useSSL", false ) ? "s" : "" )
                + "://" + CustomSystemUtil.INTRANET_IP  + ":"
                + CommonConfig.getString( "server.port", "8080" )
                + CommonConfig.getString( "server.servlet.context-path", "/" )
        );
        CommonContextHolder.getBean( ISuperService.class ).initialize();
    }

    //    @Bean
//    public TomcatServletWebServerFactory tomcatFactory(){
//        return new TomcatServletWebServerFactory(){
//            @Override
//            protected void postProcessContext(Context _context) {
//                ((StandardJarScanner)_context.getJarScanner()).setScanManifest(false);
//            }
//        };
//    }
}