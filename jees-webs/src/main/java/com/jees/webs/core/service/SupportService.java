package com.jees.webs.core.service;

import com.jees.common.CommonContextHolder;
import com.jees.webs.core.abs.AbsSupport;
import com.jees.webs.security.service.SecurityManagerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Log4j2
@Service
public class SupportService extends AbsSupport {
    /**
     * 拦截器，只有AbsControllerConfig被继承，且被扫描到后生效。
     *
     * @return 拦截器
     */
    @Bean
    public HandlerInterceptor handlerInterceptor() {
        log.debug("--应用DefaultController拦截器。");
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest _request, HttpServletResponse _response, Object _handler) {
                SecurityManagerService service = CommonContextHolder.getBean(SecurityManagerService.class);
                Objects.requireNonNull(service).getSupportELService().onPreHandle(_request, _response, _handler);
                return true;
            }
        };
    }
}
