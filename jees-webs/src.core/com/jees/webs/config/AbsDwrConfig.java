package com.jees.webs.config;

import com.jees.common.CommonConfig;
import org.directwebremoting.spring.DwrSpringServlet;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;

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

    /**
     * 去掉@Bean注释，如果包含懒加载的部分，则不需要在代码中显示调用。即让懒加载生效。
     * @return
     */
//    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        OpenSessionInViewFilter filter = new OpenSessionInViewFilter();
        registrationBean.setFilter( filter );
        return registrationBean;
    }
}
