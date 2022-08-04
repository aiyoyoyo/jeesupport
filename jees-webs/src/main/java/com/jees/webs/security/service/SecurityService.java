package com.jees.webs.security.service;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class SecurityService {
    @Getter
    @Value("${jees.webs.security.enable:true}")
    boolean enable;

    public SecurityService(){
        log.info( "安全服务模块：" + (this.enable ? "启用" : "禁用" ) );
    }
}
