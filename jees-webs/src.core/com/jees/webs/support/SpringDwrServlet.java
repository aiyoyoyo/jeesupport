package com.jees.webs.support;

import org.directwebremoting.impl.StartupUtil;
import org.directwebremoting.spring.DwrSpringServlet;
import org.directwebremoting.spring.SpringContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.ServletConfig;

//@Component
public class SpringDwrServlet extends DwrSpringServlet {

    /**  */
    private static final long serialVersionUID = 1L;

    @Override
    protected SpringContainer createContainer(ServletConfig servletConfig) {
        ApplicationContext appContext = getApplicationContext(servletConfig.getServletContext());

        SpringDwrContainer springContainer = new SpringDwrContainer();
        springContainer.setBeanFactory(appContext);
        StartupUtil.setupDefaultContainer(springContainer, servletConfig);
        return springContainer;
    }

}
