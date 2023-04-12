package com.jees.webs.modals.dwr.config;

import com.jees.common.CommonConfig;
import com.jees.webs.core.abs.AbsSupportModel;
import org.directwebremoting.spring.DwrSpringServlet;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@ImportResource(locations = "classpath:config/dwr.xml")
public class DwrConfig extends AbsSupportModel {
    @Override
    public void initialize() {
    }

    @SuppressWarnings("unchecked")
    @Bean
    @DependsOn("commonConfig")
    public ServletRegistrationBean servletRegistrationBean() {
        String dwr_url = this.getModelConfig("dwr.url", "/dwr");
        DwrSpringServlet servlet = new DwrSpringServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet, dwr_url + "/*");
        registrationBean.addInitParameter("debug", this.getModelConfig("dwr.debug", "false"));
        return registrationBean;
    }

    /**
     * 去掉@Bean注释，如果包含懒加载的部分，则不需要在代码中显示调用。即让懒加载生效。
     *
     * @return 过滤器
     */
//    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        OpenSessionInViewFilter filter = new OpenSessionInViewFilter();
        registrationBean.setFilter(filter);
        return registrationBean;
    }

    public void setHttpSecurity(HttpSecurity _hs) throws Exception {
        String dwr_url = this.getModelConfig("dwr.url", "/dwr");
        _hs.authorizeRequests().antMatchers(dwr_url + "/**").permitAll();
        if (CommonConfig.getBoolean("jees.webs.security.cross", false)) {
            _hs.csrf().ignoringAntMatchers(dwr_url + "/**");
        } else {
            _hs.csrf().disable();
        }
    }
}
