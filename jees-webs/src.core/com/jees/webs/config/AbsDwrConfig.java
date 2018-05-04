package com.jees.webs.config;

import com.jees.common.CommonConfig;
import org.directwebremoting.spring.DwrSpringServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;

@ImportResource(locations = "classpath:config/dwr.xml")
public abstract class AbsDwrConfig {
    @SuppressWarnings( "unchecked" )
    @Bean
    @DependsOn( "commonConfig" )
    public ServletRegistrationBean servletRegistrationBean() {
        String dwr_url = CommonConfig.getString("jees.webs.dwr.url", "/dwr" );
        DwrSpringServlet servlet = new DwrSpringServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet, dwr_url + "/*");
        registrationBean.addInitParameter("debug", CommonConfig.getString("jees.webs.dwr.debug", "false" ));
        return registrationBean;

    }
}
