package com.jees.webs.core.service;

import com.jees.webs.core.abs.AbsSupport;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Service
public class SupportService extends AbsSupport {

    @Autowired
    SupportELService supportELService;

    /**
     * 拦截器，只有AbsControllerConfig被继承，且被扫描到后生效。
     *
     * @return 拦截器
     */
    @Bean
    public HandlerInterceptor handlerInterceptor(){
        log.debug( "--应用DefaultController拦截器。" );
        return new HandlerInterceptor(){
            @Override
            public boolean preHandle(HttpServletRequest _request, HttpServletResponse _response, Object _handler ){
                supportELService.onPreHandle( _request, _response, _handler );
//                String uri = UrlUtil.uri2root( _request.getRequestURI() );
//                _load_menus( _request );

                return true;
            }
        };
    }
}
